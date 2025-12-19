package com.healthtracker.chw.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "unsynced_reports")
public class UnsyncedReport {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String patientName;
    public String gender;
    public String dateOfBirth;
    public Integer patientAge;

    // CHW Info
    public String chwName;
    public String chwId;
    public String chwEmail;

    // Location
    public Double latitude;
    public Double longitude;
    public String address;

    // Encounter
    public String encounterDate;
    public String encounterType;

    // Observation
    public String diseaseType;
    public String symptomsJson; // JSON list
    public String severity;
    public String observationDetails;
    public String notes;

    public long timestamp;

    public UnsyncedReport() {
        this.timestamp = System.currentTimeMillis();
    }
}
