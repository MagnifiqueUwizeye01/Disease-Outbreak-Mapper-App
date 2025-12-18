package com.healthtracker.chw.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Encounter model
 * Represents a healthcare encounter between a CHW and a Patient
 * 
 * Relationships:
 * - Created by 1 CHW (Many-to-One)
 * - Visited by 1 Patient (Many-to-One)
 * - Has 1 GPSLocation (One-to-One)
 * - Records many Observations (One-to-Many)
 * - Generates 1 DiseaseReport (One-to-One)
 */
public class Encounter {
    private String encounterId;
    private Date encounterDate;
    private String encounterType; // home, clinic, emergency
    private CHW chw; // Many-to-One relationship
    private Patient patient; // Many-to-One relationship
    private GPSLocation gpsLocation; // One-to-One relationship
    private List<Observation> observations; // One-to-Many relationship
    private DiseaseReport diseaseReport; // One-to-One relationship

    // Default constructor for Gson/Retrofit
    public Encounter() {
        this.observations = new ArrayList<>();
    }

    // Constructor with required fields
    public Encounter(String encounterId, Date encounterDate, String encounterType) {
        this.encounterId = encounterId;
        this.encounterDate = encounterDate;
        this.encounterType = encounterType;
        this.observations = new ArrayList<>();
    }

    // Full constructor
    public Encounter(String encounterId, Date encounterDate, String encounterType,
                     CHW chw, Patient patient, GPSLocation gpsLocation,
                     List<Observation> observations, DiseaseReport diseaseReport) {
        this.encounterId = encounterId;
        this.encounterDate = encounterDate;
        this.encounterType = encounterType;
        this.chw = chw;
        this.patient = patient;
        this.gpsLocation = gpsLocation;
        this.observations = observations != null ? observations : new ArrayList<>();
        this.diseaseReport = diseaseReport;
    }

    // Getters and Setters
    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public Date getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Date encounterDate) {
        this.encounterDate = encounterDate;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public CHW getChw() {
        return chw;
    }

    public void setChw(CHW chw) {
        this.chw = chw;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public GPSLocation getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(GPSLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        this.observations = observations != null ? observations : new ArrayList<>();
    }

    public void addObservation(Observation observation) {
        if (observations == null) {
            observations = new ArrayList<>();
        }
        observations.add(observation);
    }

    public DiseaseReport getDiseaseReport() {
        return diseaseReport;
    }

    public void setDiseaseReport(DiseaseReport diseaseReport) {
        this.diseaseReport = diseaseReport;
    }
}

