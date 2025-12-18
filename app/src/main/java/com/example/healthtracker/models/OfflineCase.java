package com.example.healthtracker.models;

public class OfflineCase {

    private String caseId;
    private String patientName;
    private String status;

    // Constructor
    public OfflineCase(String caseId, String patientName, String status) {
        this.caseId = caseId;
        this.patientName = patientName;
        this.status = status;
    }

    // Getters
    public String getCaseId() {
        return caseId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getStatus() {
        return status;
    }

    // Setters (optional)
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
