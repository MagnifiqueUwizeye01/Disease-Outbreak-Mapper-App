package com.healthtracker.chw.models;

/**
 * Risk Assessment model
 * Represents the risk level assessment for a disease outbreak
 * 
 * Relationships:
 * - Produced by 1 DiseaseReport (Many-to-One)
 */
public class RiskAssessment {
    private String riskId;
    private String level; // low, medium, high
    private String description;
    private DiseaseReport diseaseReport; // Many-to-One relationship

    // Default constructor for Gson/Retrofit
    public RiskAssessment() {
    }

    // Constructor with required fields
    public RiskAssessment(String riskId, String level, String description) {
        this.riskId = riskId;
        this.level = level;
        this.description = description;
    }

    // Full constructor
    public RiskAssessment(String riskId, String level, String description, DiseaseReport diseaseReport) {
        this.riskId = riskId;
        this.level = level;
        this.description = description;
        this.diseaseReport = diseaseReport;
    }

    // Getters and Setters
    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiseaseReport getDiseaseReport() {
        return diseaseReport;
    }

    public void setDiseaseReport(DiseaseReport diseaseReport) {
        this.diseaseReport = diseaseReport;
    }
}

