package com.example.healthtracker.models;

public class CaseHistory {

    private String caseId;
    private String patientName;
    private String dateReported;

    // Constructor
    public CaseHistory(String caseId, String patientName, String dateReported) {
        this.caseId = caseId;
        this.patientName = patientName;
        this.dateReported = dateReported;
    }

    // Getters
    public String getCaseId() {
        return caseId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDateReported() {
        return dateReported;
    }

    // Setters (optional)
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }
}
