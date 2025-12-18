package com.example.healthtracker.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "reports")
public class ReportEntity {
    @PrimaryKey(autoGenerate = true)
    public long localId;

    @ColumnInfo(name = "uuid")
    public String uuid; // user generated id

    @ColumnInfo(name = "disease")
    public String disease;

    @ColumnInfo(name = "patient_name")
    public String patientName;

    @ColumnInfo(name = "patient_age")
    public int patientAge;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "timestamp")
    public String timestampIso;

    @ColumnInfo(name = "payload_json")
    public String payloadJson;

    @ColumnInfo(name = "status")
    public String status; // PENDING, SYNCED, FAILED
}
