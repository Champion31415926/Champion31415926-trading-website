package cn.mrblackpineapple.trading_website.ml;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureExtractor {
    private static final List<String> BRAND_LIST = Arrays.asList("canon","sony","nikon","fujifilm","leica","hasselblad","ricoh","kodak","panasonic","om system","olympus","pentax","sigma","blackmagic");
    private static final Map<String,List<String>> TIER_MAP = new HashMap<>();
    static {
        TIER_MAP.put("Flagship", Arrays.asList("z9","r1","r3","a1","a9","gfx","x-h2s","s1h","sl3","m ev1"));
        TIER_MAP.put("Pro", Arrays.asList("r5","z8","a7r","a7s","x-h2","s5 ii","s5ii","gr iv","x2d"));
        TIER_MAP.put("Mid", Arrays.asList("r6","z6","a7","s5","x-t","om-1","x-t30"));
        TIER_MAP.put("Entry", Arrays.asList("r8","r50","r100","z5","z30","z50","a6","zv-e","x-s","x-t30","fz55","elph"));
    }
    private final LocalDate CURRENT_DATE = LocalDate.of(2025,11,26);

    public Map<String,Object> extract(String input){
        String full = input==null? "" : input.toLowerCase();
        String nameLower = full.contains("|") ? full.split("\\|",2)[0].trim() : full;
        Map<String,Object> m = new HashMap<>();
        m.put("full_text", full);
        m.put("name_lower", nameLower);
        m.put("Brand", getBrand(full));
        m.put("Tier", mapTier(nameLower));
        m.put("Sensor_Format", getSensor(full));
        m.put("Megapixels", getMegapixels(full));
        m.put("Sensor_Stacked", full.contains("stacked")||full.contains("expeed 7")?1:0);
        m.put("Sensor_BSI", (full.contains("bsi")||full.contains("back-illuminated")||full.contains("exmor r"))?1:0);
        m.put("Has_Global_Shutter", full.contains("global shutter")?1:0);
        m.put("In_Camera_Upscale", (full.contains("upscaling")||full.contains("upscale")||full.contains("179mp"))?1:0);
        m.put("Video_Res_Score", getVideoRes(full));
        m.put("Has_Raw_Video", (full.contains("raw video")||full.contains("prores raw")||full.contains("braw")||full.contains("internal raw")||full.contains("n-raw"))?1:0);
        m.put("Video_Bit_Depth", full.contains("12-bit")?12:(full.contains("10-bit")?10:(full.contains("16-bit")?16:8)));
        m.put("Has_Open_Gate", (full.contains("open gate")||full.contains("open-gate"))?1:0);
        m.put("Has_Timecode", full.contains("timecode")?1:0);
        m.put("Is_Cinema_Camera", (full.contains("cinema camera")||full.contains("blackmagic")||full.contains("cine"))?1:0);
        double pv = getProVideoScore(full);
        m.put("Pro_Video_Score", pv);
        m.put("Burst_FPS", getBurst(full));
        m.put("AF_Eye_Control", (full.contains("eye control")||full.contains("eye af")||full.contains("real-time eye"))?1:0);
        m.put("AF_Subject_Detect", (full.contains("subject detect")||full.contains("intelligent af")||full.contains("ai tracking")||full.contains("ai-based")||full.contains("hybrid af"))?1:0);
        m.put("Has_Pre_Capture", (full.contains("pre-continuous")||full.contains("pre-shoot")||full.contains("pre-capture"))?1:0);
        m.put("EVF_Res", getEvf(full));
        m.put("Screen_Type", full.contains("vari-angle")?"Vari-Angle":(full.contains("tilting")?"Tilting":"Fixed"));
        m.put("Has_IBIS", (full.contains("ibis")||full.contains("in-body stabilization")||full.contains("5-axis")||full.contains("shake reduction")||full.contains("sensor-shift"))?1:0);
        m.put("Weather_Sealed", (full.contains("weather-sealed")||full.contains("dust")||full.contains("moisture")||full.contains("sealed"))?1:0);
        m.put("Battery_Life_Score", full.contains("extended battery")?3:(full.contains("rechargeable")?2:1));
        m.put("Storage_CFexpress", (full.contains("cfexpress")||full.contains("xqd"))?1:0);
        m.put("Storage_UHS_II", full.contains("uhs-ii")?1:0);
        m.put("Has_Dual_Slot", Pattern.compile("\\b(dual|two)\\b.*\\b(slot|card)").matcher(full).find()?1:0);
        m.put("Has_Wi_Fi_6E", (full.contains("wi-fi 6e")||full.contains("wi-fi 6"))?1:0);
        m.put("Has_Full_HDMI", (full.contains("full-size hdmi")||full.contains("full size hdmi")||full.contains("hdmi"))?1:0);
        m.put("Has_Mic_Port", full.contains("mic port")?1:0);
        m.put("Has_Headphone_Port", full.contains("headphone port")?1:0);

        Map<String,Integer> kit = analyzeKit(nameLower, full);
        m.put("Is_Kit", kit.get("is_kit"));
        m.put("Lens_Score", kit.get("lens_score"));
        m.put("Lens_OIS", kit.get("lens_ois"));
        m.put("Stabilization_Combined", ((int)m.get("Has_IBIS")) + kit.get("lens_ois"));
        m.put("Edition_Limited", (full.contains("limited edition")||full.contains("anniversary")||full.contains("monochrom"))?1:0);
        m.put("Market_Age", full.contains("new")? (double) ChronoUnit.DAYS.between(LocalDate.of(2020,1,1), CURRENT_DATE)/365.0 : 3.0);
        m.put("price_per_mp", 0.0);

        return m;
    }

    public double[] toFeatureVector(Map<String,Object> feats, List<String> featureOrder, Map<String,List<String>> categoricalMapping) {
        double[] arr = new double[featureOrder.size()];
        for (int i = 0; i < featureOrder.size(); i++) {
            String f = featureOrder.get(i);
            Object v = feats.get(f);
            if (v == null) {
                arr[i] = 0.0;
            } else if (v instanceof Number) {
                arr[i] = ((Number) v).doubleValue();
            } else {
                String s = String.valueOf(v);
                List<String> cats = categoricalMapping != null ? categoricalMapping.get(f) : null;
                if (cats != null) {
                    int idx = cats.indexOf(s);
                    arr[i] = (idx >= 0) ? idx : cats.size();
                } else {
                    arr[i] = (double) s.hashCode();
                }
            }
        }
        return arr;
    }

    private String getBrand(String t) {
        for (String b : BRAND_LIST) if (t.contains(b)) return capitalize(b);
        String[] tokens = t.split("\\s+");
        return tokens.length > 0 ? capitalize(tokens[0]) : "Other";
    }
    private String mapTier(String name) {
        String n = name.toLowerCase();
        for (Map.Entry<String,List<String>> e: TIER_MAP.entrySet()) for (String p: e.getValue()) if (n.contains(p)) return e.getKey();
        if (n.matches(".*(mark|mk|iii|iv|ii).*")) return "New_Gen";
        return "Standard";
    }
    private String getSensor(String s) {
        if (s.contains("medium format")) return "Medium Format";
        if (s.contains("full-frame")||s.contains("full frame")||s.contains("fx-format")) return "Full-Frame";
        if (s.contains("aps-c")) return "APS-C";
        if (s.contains("micro four thirds")||s.contains("m4/3")||s.contains("m4/3")) return "M4/3";
        return "Other";
    }
    private double getMegapixels(String s) {
        Matcher m = Pattern.compile("(\\d+\\.?\\d*)\\s*mp").matcher(s);
        if (m.find()) return Double.parseDouble(m.group(1));
        return 24.0;
    }
    private int getVideoRes(String s) {
        if (s.contains("8k")||s.contains("8.3k")) return 8;
        if (s.contains("7k")) return 7;
        if (s.contains("6k")||s.contains("6.2k")) return 6;
        if (s.contains("4k")||s.contains("uhd")) return 4;
        return 0;
    }
    private double getProVideoScore(String s) {
        int vr = getVideoRes(s);
        int raw = (s.contains("raw video")||s.contains("prores raw")||s.contains("braw"))?1:0;
        int bd = s.contains("12-bit")?12:(s.contains("10-bit")?10:(s.contains("16-bit")?16:8));
        int og = (s.contains("open gate")||s.contains("open-gate"))?1:0;
        int tc = s.contains("timecode")?1:0;
        int cine = (s.contains("cinema camera")||s.contains("blackmagic")||s.contains("cine"))?1:0;
        return (vr + raw + (bd/4.0) + og + tc + cine) / 6.0;
    }
    private int getBurst(String s){
        Matcher m = Pattern.compile("up to (\\d+)\\s*fps").matcher(s);
        if (m.find()) return Integer.parseInt(m.group(1));
        return 10;
    }
    private double getEvf(String s){
        Matcher m = Pattern.compile("(\\d+\\.?\\d*)\\s*m-?dot").matcher(s);
        if (m.find()) return Double.parseDouble(m.group(1));
        return 2.36;
    }
    private Map<String,Integer> analyzeKit(String name, String text){
        int isKit=0,lensScore=0,lensOis=0;
        if (!name.contains("body only") && (name.contains("with")||name.contains("lens")||name.contains("kit")||name.contains("bundle"))){
            isKit=1;
            if (Pattern.compile("f/?(1\\.2|1\\.4|2\\.8)").matcher(text).find()) lensScore=3;
            else if (Pattern.compile("f/?4").matcher(text).find()) lensScore=2;
            else lensScore=1;
            if (name.contains("2-lens")||name.contains("two lens")||name.contains("and")) lensScore++;
            if (text.contains("ois")||text.contains("stabiliz")||text.contains("image stabilization")) lensOis=1;
        }
        Map<String,Integer> r = new HashMap<>(); r.put("is_kit",isKit); r.put("lens_score",lensScore); r.put("lens_ois",lensOis); return r;
    }
    private String capitalize(String s){ if (s==null||s.isEmpty()) return s; return s.substring(0,1).toUpperCase()+s.substring(1); }
}