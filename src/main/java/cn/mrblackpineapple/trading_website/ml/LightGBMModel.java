package cn.mrblackpineapple.trading_website.ml;

public class LightGBMModel {
    public static class Tree {
        public int numLeaves;
        public int[] splitFeature;
        public double[] threshold;
        public String[] decisionType;
        public int[] leftChild;
        public int[] rightChild;
        public double[] leafValue;
        public boolean[] defaultLeft;

        public double predict(double[] features) {
            int node = 0;
            while (true) {
                if (node < 0) {
                    int leafIndex = -node - 1;
                    if (leafIndex >= 0 && leafIndex < leafValue.length) return leafValue[leafIndex];
                    return 0.0;
                }

                if (leftChild[node] < 0 && rightChild[node] < 0) {
                    int leafIndex = -leftChild[node] - 1;
                    if (leafIndex >= 0 && leafIndex < leafValue.length) return leafValue[leafIndex];
                    return 0.0;
                }

                int f = splitFeature[node];
                double val = 0.0;
                if (f >= 0 && f < features.length) {
                    val = features[f];
                } else {
                    val = 0.0;
                }

                boolean goLeft;
                String dt = (decisionType != null && decisionType.length > node) ? decisionType[node] : "2";
                if ("<=".equals(dt) || "<".equals(dt) || "2".equals(dt)) {
                    goLeft = val <= threshold[node];
                } else {
                    goLeft = Double.compare(val, threshold[node]) == 0;
                }

                int child = goLeft ? leftChild[node] : rightChild[node];

                if (child < 0) {
                    int leafIndex = -child - 1;
                    if (leafIndex >= 0 && leafIndex < leafValue.length) return leafValue[leafIndex];
                    return 0.0;
                }

                node = child;
            }
        }

    }

    private final java.util.List<Tree> trees = new java.util.ArrayList<>();

    public void addTree(Tree t) { trees.add(t); }

    public double predict(double[] features) {
        double sum = 0.0;
        for (Tree t : trees) sum += t.predict(features);
        return sum;
    }
}