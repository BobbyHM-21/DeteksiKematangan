public class KNNClassifier {
    private List<double[]> trainingData;
    private List<String> labels;
    private int k;

    public KNNClassifier(List<double[]> trainingData, List<String> labels, int k) {
        this.trainingData = trainingData;
        this.labels = labels;
        this.k = k;
    }

    public String classify(double[] testSample) {
        List<Neighbor> neighbors = new ArrayList<>();

        // Hitung jarak ke semua data pelatihan
        for (int i = 0; i < trainingData.size(); i++) {
            double[] trainingSample = trainingData.get(i);
            String label = labels.get(i);
            double distance = calculateEuclideanDistance(trainingSample, testSample);
            neighbors.add(new Neighbor(distance, label));
        }

        // Urutkan tetangga berdasarkan jarak
        Collections.sort(neighbors);

        // Ambil k tetangga terdekat
        Map<String, Integer> votes = new HashMap<>();
        for (int i = 0; i < k; i++) {
            Neighbor neighbor = neighbors.get(i);
            String label = neighbor.getLabel();
            votes.put(label, votes.getOrDefault(label, 0) + 1);
        }

        // Tentukan label dengan voting terbanyak
        String predictedLabel = null;
        int maxVotes = 0;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                predictedLabel = entry.getKey();
                maxVotes = entry.getValue();
            }
        }

        return predictedLabel;
    }

    private double calculateEuclideanDistance(double[] sample1, double[] sample2) {
        double distance = 0.0;

        for (int i = 0; i < sample1.length; i++) {
            distance += Math.pow(sample1[i] - sample2[i], 2);
        }

        return Math.sqrt(distance);
    }

    private static class Neighbor implements Comparable<Neighbor> {
        private double distance;
        private String label;

        public Neighbor(double distance, String label) {
            this.distance = distance;
            this.label = label;
        }

        public double getDistance() {
            return distance;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public int compareTo(Neighbor neighbor) {
            return Double.compare(distance, neighbor.getDistance());
        }
    }
	
	  // Data pelatihan untuk kelas "Matang Karbit"
        trainingData.add(new double[]{0.75, 0.65, 0.8, 0.9, 0.6});
        labels.add("Matang Karbit");
        trainingData.add(new double[]{0.8, 0.6, 0.7, 0.95, 0.5});
        labels.add("Matang Karbit");
        trainingData.add(new double[]{0.85, 0.55, 0.65, 0.8, 0.7});
        labels.add("Matang Karbit");

        // Data pelatihan untuk kelas "Matang Alami"
        trainingData.add(new double[]{0.3, 0.8, 0.5, 0.4, 0.9});
        labels.add("Matang Alami");
        trainingData.add(new double[]{0.4, 0.9, 0.6, 0.3, 0.8});
        labels.add("Matang Alami");
        trainingData.add(new double[]{0.2, 0.85, 0.7, 0.2, 0.95});
        labels.add("Matang Alami");

}
