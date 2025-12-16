package cn.mrblackpineapple.trading_website.ml;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelLoader {

    private static final Pattern TREE_HEADER = Pattern.compile("^Tree=\\d+");
    private static final Pattern PANDAS_CAT = Pattern.compile("pandas_categorical:\\[(.*)\\]", Pattern.DOTALL);

    public static class ModelMeta {
        public List<String> feature_order;
        public Map<String, List<String>> pandas_categorical;
    }

    public static Pair<LightGBMModel, ModelMeta> loadModel(File modelTxtFile) throws IOException {
        String content = new String(Files.readAllBytes(modelTxtFile.toPath()));
        LightGBMModel model = new LightGBMModel();

        ModelMeta meta = new ModelMeta();
        meta.pandas_categorical = new HashMap<>();

        Matcher mpc = PANDAS_CAT.matcher(content);
        if (mpc.find()) {
            String inner = mpc.group(1).trim();
            List<List<String>> lists = parseJsonLikeArrayOfArrays(inner);
            for (int i = 0; i < lists.size(); i++) {
                meta.pandas_categorical.put(Integer.toString(i), lists.get(i));
            }
        }

        File metaFile = new File(modelTxtFile.getParentFile(), modelTxtFile.getName().replace(".txt", ".meta.json"));
        ObjectMapper om = new ObjectMapper();
        if (metaFile.exists()) {
            ModelMeta diskMeta = om.readValue(metaFile, ModelMeta.class);
            if (diskMeta.feature_order != null) meta.feature_order = diskMeta.feature_order;
            if (diskMeta.pandas_categorical != null) meta.pandas_categorical = diskMeta.pandas_categorical;
        }

        BufferedReader br = new BufferedReader(new StringReader(content));
        String line;
        List<String> block = null;
        while ((line = br.readLine()) != null) {
            if (TREE_HEADER.matcher(line).find()) {
                block = new ArrayList<>();
                continue;
            }
            if (block != null) {
                if (line.trim().isEmpty()) {
                    LightGBMModel.Tree tree = parseTreeBlock(block);
                    if (tree != null) model.addTree(tree);
                    block = null;
                } else {
                    block.add(line.trim());
                }
            }
        }
        if (block != null && !block.isEmpty()) {
            LightGBMModel.Tree tree = parseTreeBlock(block);
            if (tree != null) model.addTree(tree);
        }

        return new Pair<>(model, meta);
    }

    private static LightGBMModel.Tree parseTreeBlock(List<String> block) {
        Map<String, String> kv = new HashMap<>();
        for (String l : block) {
            int idx = l.indexOf('=');
            if (idx > 0) {
                String k = l.substring(0, idx).trim();
                String v = l.substring(idx + 1).trim();
                kv.put(k, v);
            }
        }
        LightGBMModel.Tree t = new LightGBMModel.Tree();
        t.numLeaves = Integer.parseInt(kv.getOrDefault("num_leaves", "0"));
        t.splitFeature = parseIntArray(kv.get("split_feature"));
        t.threshold = parseDoubleArray(kv.get("threshold"));
        t.decisionType = parseStringArray(kv.get("decision_type"));
        t.leftChild = parseIntArray(kv.get("left_child"));
        t.rightChild = parseIntArray(kv.get("right_child"));
        t.leafValue = parseDoubleArray(kv.get("leaf_value"));
        t.defaultLeft = parseBoolArray(kv.get("default_left"));
        return t;
    }

    private static int[] parseIntArray(String s) {
        if (s == null || s.isEmpty()) return new int[0];
        String[] parts = s.trim().split("\\s+");
        int[] a = new int[parts.length];
        for (int i = 0; i < parts.length; i++) a[i] = Integer.parseInt(parts[i]);
        return a;
    }

    private static double[] parseDoubleArray(String s) {
        if (s == null || s.isEmpty()) return new double[0];
        String[] parts = s.trim().split("\\s+");
        double[] a = new double[parts.length];
        for (int i = 0; i < parts.length; i++) a[i] = Double.parseDouble(parts[i]);
        return a;
    }

    private static String[] parseStringArray(String s) {
        if (s == null || s.isEmpty()) return new String[0];
        return s.trim().split("\\s+");
    }

    private static boolean[] parseBoolArray(String s) {
        if (s == null || s.isEmpty()) return new boolean[0];
        String[] parts = s.trim().split("\\s+");
        boolean[] a = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) a[i] = "true".equalsIgnoreCase(parts[i]);
        return a;
    }

    private static List<List<String>> parseJsonLikeArrayOfArrays(String inner) {
        List<List<String>> result = new ArrayList<>();
        int i = 0, n = inner.length();
        while (i < n) {
            while (i < n && inner.charAt(i) != '[') i++;
            if (i >= n) break;
            int start = i;
            int depth = 0;
            while (i < n) {
                char c = inner.charAt(i);
                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        String arrText = inner.substring(start + 1, i).trim();
                        List<String> items = new ArrayList<>();
                        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(arrText);
                        while (m.find()) items.add(m.group(1));
                        result.add(items);
                        i++;
                        break;
                    }
                }
                i++;
            }
        }
        return result;
    }

    public static class Pair<A,B> {
        public final A first;
        public final B second;
        public Pair(A a, B b){ this.first=a; this.second=b; }
    }
}