package com.healthtracker.chw.models;

import java.util.Date;

/**
 * Observation model
 * Represents clinical observations recorded during an encounter
 * 
 * Relationships:
 * - Belongs to 1 Encounter (Many-to-One)
 */
public class Observation {
    private String observationId;
    private String details;
    private Date timestamp;
    private Encounter encounter; // Many-to-One relationship

    // Default constructor for Gson/Retrofit
    public Observation() {
    }

    // Constructor with required fields
    public Observation(String observationId, String details, Date timestamp) {
        this.observationId = observationId;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Full constructor
    public Observation(String observationId, String details, Date timestamp, Encounter encounter) {
        this.observationId = observationId;
        this.details = details;
        this.timestamp = timestamp;
        this.encounter = encounter;
    }

    // Getters and Setters
    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
}

