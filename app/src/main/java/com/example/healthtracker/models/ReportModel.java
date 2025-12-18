package com.example.healthtracker.models;
public class ReportModel {
    public String id; // local UUID
    public String disease;
    public String patientName;
    public int patientAge;
    public String notes;
    public GPSLocation location;
    public String timestampIso; // ISO 8601
    public String fhirPayload; // JSON as String (optional cached)
    public String status; // PENDING, SYNCED, FAILED

    public ReportModel() {}
}
