package com.example.kematanganKeluak.ui.main;

import android.graphics.Bitmap;
import android.graphics.Color;

public class GLCMFeatureExtractor {

    // Fungsi untuk menghitung GLCM dari gambar gray-level
    public static double[][] calculateGLCM(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] grayImage = new int[width][height];

        // Ubah gambar menjadi citra gray-level
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = bitmap.getPixel(x, y);
                int grayValue = (int) (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel));
                grayImage[x][y] = grayValue;
            }
        }

        // Hitung GLCM dengan jarak dan sudut tertentu (misalnya, jarak 1 dan sudut 0 derajat)
        int distance = 1;
        int angle = 0;
        int maxGrayLevel = 256; // Maksimum gray-level dalam gambar
        double[][] glcm = new double[maxGrayLevel][maxGrayLevel];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x + distance < width && y + distance < height) {
                    int gray1 = grayImage[x][y];
                    int gray2 = grayImage[x + distance][y + distance];
                    glcm[gray1][gray2]++;
                }
            }
        }

        // Normalisasi matriks GLCM
        double sum = 0.0;
        for (int i = 0; i < maxGrayLevel; i++) {
            for (int j = 0; j < maxGrayLevel; j++) {
                sum += glcm[i][j];
            }
        }

        for (int i = 0; i < maxGrayLevel; i++) {
            for (int j = 0; j < maxGrayLevel; j++) {
                glcm[i][j] /= sum;
            }
        }

        return glcm;
    }

    // Fungsi untuk menghitung energi (energy) dari matriks GLCM
    public static double calculateEnergy(double[][] glcm) {
        double energy = 0.0;

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                energy += Math.pow(glcm[i][j], 2);
            }
        }

        return energy;
    }

    // Fungsi untuk menghitung kontras (contrast) dari matriks GLCM
    public static double calculateContrast(double[][] glcm) {
        double contrast = 0.0;

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                contrast += Math.pow(i - j, 2) * glcm[i][j];
            }
        }

        return contrast;
    }

    // Fungsi untuk menghitung homogenitas (homogeneity) dari matriks GLCM
    public static double calculateHomogeneity(double[][] glcm) {
        double homogeneity = 0.0;

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                homogeneity += glcm[i][j] / (1 + Math.abs(i - j));
            }
        }

        return homogeneity;
    }

    // Fungsi untuk menghitung entropi (entropy) dari matriks GLCM
    public static double calculateEntropy(double[][] glcm) {
        double entropy = 0.0;

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                if (glcm[i][j] > 0) {
                    entropy -= glcm[i][j] * Math.log(glcm[i][j]);
                }
            }
        }

        return entropy;
    }

    // Fungsi untuk menghitung korelasi (correlation) dari matriks GLCM
    public static double calculateCorrelation(double[][] glcm) {
        double meanX = 0.0;
        double meanY = 0.0;
        double varX = 0.0;
        double varY = 0.0;
        double correlation = 0.0;

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                meanX += i * glcm[i][j];
                meanY += j * glcm[i][j];
            }
        }

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                varX += Math.pow(i - meanX, 2) * glcm[i][j];
                varY += Math.pow(j - meanY, 2) * glcm[i][j];
            }
        }

        for (int i = 0; i < glcm.length; i++) {
            for (int j = 0; j < glcm[0].length; j++) {
                correlation += (i - meanX) * (j - meanY) * glcm[i][j] / (Math.sqrt(varX) * Math.sqrt(varY));
            }
        }

        return correlation;
    }
}
