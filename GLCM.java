public class GLCMProcessor {
    private int[][] image;

    public GLCMProcessor(int[][] image) {
        this.image = image;
    }

    public int[][] calculateGLCM(int distance, int angle) {
        int[][] glcm = new int[256][256];
        int numRows = image.length;
        int numCols = image[0].length;

        // Hitung matriks GLCM
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int pixelValue = image[row][col];

                // Hitung koordinat piksel tetangga berdasarkan jarak dan sudut
                int neighborRow = row + distance * (int) Math.cos(Math.toRadians(angle));
                int neighborCol = col + distance * (int) Math.sin(Math.toRadians(angle));

                // Pastikan koordinat tetangga berada dalam batas gambar
                if (isValidCoordinate(neighborRow, neighborCol, numRows, numCols)) {
                    int neighborValue = image[neighborRow][neighborCol];
                    glcm[pixelValue][neighborValue]++;
                }
            }
        }

        return glcm;
    }

    public double calculateContrast(int[][] glcm) {
        double contrast = 0.0;
        int numLevels = glcm.length;

        // Hitung kontras
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                contrast += Math.pow(i - j, 2) * glcm[i][j];
            }
        }

        return contrast;
    }

    public double calculateEnergy(int[][] glcm) {
        double energy = 0.0;
        int numLevels = glcm.length;

        // Hitung energi
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                energy += Math.pow(glcm[i][j], 2);
            }
        }

        return energy;
    }

    public double calculateEntropy(int[][] glcm) {
        double entropy = 0.0;
        int numLevels = glcm.length;
        int totalPixels = 0;

        // Hitung total piksel dalam GLCM
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                totalPixels += glcm[i][j];
            }
        }

        // Hitung entropi
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                double probability = (double) glcm[i][j] / totalPixels;
                if (probability > 0) {
                    entropy -= probability * Math.log(probability);
                }
            }
        }

        return entropy;
    }

    public double calculateHomogeneity(int[][] glcm) {
        double homogeneity = 0.0;
        int numLevels = glcm.length;

        // Hitung keseragaman
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                homogeneity += glcm[i][j] / (1 + Math.abs(i - j));
            }
        }

        return homogeneity;
    }

    public double calculateCorrelation(int[][] glcm) {
        double correlation = 0.0;
        int numLevels = glcm.length;
        double[] rowMeans = new double[numLevels];
        double[] colMeans = new double[numLevels];
        double rowStdDev = 0.0;
        double colStdDev = 0.0;

        // Hitung rata-rata per baris dan per kolom
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                rowMeans[i] += glcm[i][j];
                colMeans[j] += glcm[i][j];
            }
        }

        // Hitung standar deviasi per baris dan per kolom
        for (int i = 0; i < numLevels; i++) {
            rowStdDev += Math.pow(i - rowMeans[i], 2);
            colStdDev += Math.pow(i - colMeans[i], 2);
        }
        rowStdDev = Math.sqrt(rowStdDev);
        colStdDev = Math.sqrt(colStdDev);

        // Hitung korelasi
        for (int i = 0; i < numLevels; i++) {
            for (int j = 0; j < numLevels; j++) {
                correlation += ((i - rowMeans[i]) * (j - colMeans[j]) * glcm[i][j]) / (rowStdDev * colStdDev);
            }
        }

        return correlation;
    }

    private boolean isValidCoordinate(int row, int col, int numRows, int numCols) {
        return (row >= 0 && row < numRows && col >= 0 && col < numCols);
    }
}
