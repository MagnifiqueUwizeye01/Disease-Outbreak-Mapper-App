package com.healthmapper.chwapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import java.util.Date;

@Entity(tableName = "disease_report")
public class DiseaseReport {
    @PrimaryKey
    @NonNull
    private String reportId;
    private String diseaseType;
    private Date reportDate;
    private String status; // DRAFT, PENDING, SUBMITTED
    private int patientAge;
    private String chwId; // Foreign key to CHW
    private double latitude;
    private double longitude;
    private String notes;

    public DiseaseReport(@NonNull String reportId, String diseaseType, Date reportDate,
                         String status, int patientAge, String chwId,
                         double latitude, double longitude, String notes) {
        this.reportId = reportId;
        this.diseaseType = diseaseType;
        this.reportDate = reportDate;
        this.status = status;
        this.patientAge = patientAge;
        this.chwId = chwId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notes = notes;
    }

    // Getters and Setters
    @NonNull
    public String getReportId() { return reportId; }
    public void setReportId(@NonNull String reportId) { this.reportId = reportId; }

    public String getDiseaseType() { return diseaseType; }
    public void setDiseaseType(String diseaseType) { this.diseaseType = diseaseType; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPatientAge() { return patientAge; }
    public void setPatientAge(int patientAge) { this.patientAge = patientAge; }

    public String getChwId() { return chwId; }
    public void setChwId(String chwId) { this.chwId = chwId; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
