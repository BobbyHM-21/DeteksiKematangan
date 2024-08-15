package com.example.kematanganKeluak.ui.main;

import java.util.ArrayList;
import java.util.List;

public class KNNwithGLCM {
    private List<double[][]> trainingData; // Matriks GLCM dari data pelatihan
    private List<String> labels; // Label untuk data pelatihan

    public KNNwithGLCM() {
        trainingData = new ArrayList<>();
        labels = new ArrayList<>();
    }

    public void addTrainingData(double[][] glcm, String label) {
        trainingData.add(glcm);
        labels.add(label);
    }

    public String classify(double[][] glcm, int k) {
        if (trainingData.size() == 0 || k > trainingData.size()) {
            return "Data pelatihan tidak mencukupi atau k terlalu besar";
        }

        // Hitung jarak antara data uji dan data pelatihan
        double[] distances = new double[trainingData.size()];
        for (int i = 0; i < trainingData.size(); i++) {
            double distance = calculateGLCMDistance(glcm, trainingData.get(i));
            distances[i] = distance;
        }

        // Ambil k tetangga terdekat
        int[] nearestIndices = getKNearestIndices(distances, k);

        // Hitung mayoritas label dari tetangga terdekat
        String[] nearestLabels = new String[k];
        for (int i = 0; i < k; i++) {
            nearestLabels[i] = labels.get(nearestIndices[i]);
        }

        return getMajority(nearestLabels);
    }

    private double calculateGLCMDistance(double[][] glcm1, double[][] glcm2) {
        // Hitung jarak antara dua matriks GLCM (bisa menggunakan metode jarak Euclidean atau yang lain)
        // Contoh: jarak Euclidean
        double distance = 0.0;
        for (int i = 0; i < glcm1.length; i++) {
            for (int j = 0; j < glcm1[0].length; j++) {
                distance += Math.pow(glcm1[i][j] - glcm2[i][j], 2);
            }
        }
        return Math.sqrt(distance);
    }

    private int[] getKNearestIndices(double[] distances, int k) {
        int[] nearestIndices = new int[k];
        return nearestIndices;
    }

    private String getMajority(String[] labels) {
        String majorityLabel = "";

        return majorityLabel;
    }

    public static void main(String[] args) {
        KNNwithGLCM knn = new KNNwithGLCM();


        // Lakukan klasifikasi dengan k = 7
        int k = 8;

        // Hasil klasifikasi adalah predictedClass
    }
}
