package com.example.kematanganKeluak;

public class ListItem {
    private String id;
    private String imageUrl;
    private String text;
    private String energy;
    private String entropy;
    private String correlation;
    private String homogeneity;
    private String contrast;
    private String descriptionName;

    public ListItem(String id, String imageUrl, String text, String energy, String entropy, String correlation, String homogeneity, String contrast, String descriptionName) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.text = text;
        this.energy = energy;
        this.entropy = entropy;
        this.correlation = correlation;
        this.homogeneity = homogeneity;
        this.contrast = contrast;
        this.descriptionName = descriptionName;
    }

    public String getId() {
        return id;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getText() {
        return text;
    }
    public String getEnergy() {
        return energy;
    }

    public String getEntropy() {
        return entropy;
    }

    public String getCorrelation() {
        return correlation;
    }

    public String getHomogeneity() {
        return homogeneity;
    }

    public String getContrast() {
        return contrast;
    }

    public String getDescriptionName() {
        return descriptionName;
    }
}
