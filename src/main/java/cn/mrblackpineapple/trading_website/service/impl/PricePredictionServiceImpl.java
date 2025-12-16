package cn.mrblackpineapple.trading_website.service.impl;

import cn.mrblackpineapple.trading_website.ml.FeatureExtractor;
import cn.mrblackpineapple.trading_website.ml.LightGBMModel;
import cn.mrblackpineapple.trading_website.ml.ModelLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricePredictionServiceImpl {
    private final Map<String, LightGBMModel> models = new HashMap<>();
    private final Map<String, List<String>> featureOrderMap = new HashMap<>();
    private final Map<String, Map<String, List<String>>> categoricalMap = new HashMap<>();
    private final FeatureExtractor extractor = new FeatureExtractor();
    private final ObjectMapper om = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            Path modelsDir = Paths.get("src/main/resources/lgbm_models");
            if (!Files.exists(modelsDir)) {
                modelsDir = Paths.get("lgbm_models");
            }
            if (!Files.exists(modelsDir)) {
                System.out.println("WARNING: lgbm models directory not found: " + modelsDir.toAbsolutePath());
                return;
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(modelsDir, "lgbm_*.txt")) {
                for (Path p : stream) {
                    String fname = p.getFileName().toString();
                    String brand = fname.substring("lgbm_".length(), fname.length() - 4);
                    ModelLoader.Pair<LightGBMModel, ModelLoader.ModelMeta> pair = ModelLoader.loadModel(p.toFile());
                    models.put(capitalize(brand), pair.first);
                    List<String> featureOrder = null;
                    if (pair.second != null && pair.second.feature_order != null) {
                        featureOrder = pair.second.feature_order;
                    } else {
                        try {
                            File f = new File("src/main/resources/model_feature_columns_v2.json");
                            if (f.exists()) {
                                featureOrder = Arrays.asList(om.readValue(f, String[].class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    featureOrderMap.put(capitalize(brand), featureOrder);
                    Map<String, List<String>> cmap = new HashMap<>();
                    if (pair.second != null && pair.second.pandas_categorical != null) {
                        for (Map.Entry<String, List<String>> e : pair.second.pandas_categorical.entrySet()) {
                            cmap.put(e.getKey(), e.getValue());
                        }
                    }
                    categoricalMap.put(capitalize(brand), cmap);
                    System.out.println("Loaded model for " + capitalize(brand) + " features=" + (featureOrder==null? "null" : featureOrder.size()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double predictPrice(String input) {
        Map<String, Object> feats = extractor.extract(input);
        String brand = String.valueOf(feats.get("Brand"));
        LightGBMModel model = models.get(brand);
        if (model == null) {
            throw new IllegalArgumentException("Unsupported/unknown brand: " + brand);
        }
        List<String> featureOrder = featureOrderMap.get(brand);
        if (featureOrder == null) throw new IllegalStateException("Feature order for brand " + brand + " not loaded.");
        Map<String, List<String>> catMapForBrand = categoricalMap.get(brand);
        if (catMapForBrand != null && catMapForBrand.containsKey("0") && featureOrder != null) {
            if (catMapForBrand.size()==3) {
                Map<String, List<String>> remapped = new HashMap<>();
                remapped.put("Tier", catMapForBrand.get("0"));
                remapped.put("Sensor_Format", catMapForBrand.get("1"));
                remapped.put("Screen_Type", catMapForBrand.get("2"));
                catMapForBrand = remapped;
                categoricalMap.put(brand, catMapForBrand);
            }
        }

        double[] vec = extractor.toFeatureVector(feats, featureOrder, catMapForBrand);
        if (featureOrder != null && vec.length != featureOrder.size()) {
            System.err.printf("Feature vector length mismatch for brand=%s: vecLen=%d expected=%d%n",
                    brand, vec.length, featureOrder.size());
            throw new IllegalStateException("Feature vector length mismatch: " + vec.length + " vs " + featureOrder.size());
        }
        double predLog = model.predict(vec);
        double pred = Math.expm1(predLog);
        BigDecimal bd = BigDecimal.valueOf(pred).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private String capitalize(String s){ if (s==null||s.isEmpty()) return s; return s.substring(0,1).toUpperCase()+s.substring(1); }
}