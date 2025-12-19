package com.healthtracker.chw.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Patient model
 * Represents a patient who visits encounters
 * 
 * Relationships:
 * - Visits many Encounters (One-to-Many)
 */
public class Patient {
    private String patientId;
    private String name;
    private Date dateOfBirth;
    private String gender; // male, female, other
    private List<Encounter> encounters; // One-to-Many relationship

    // Default constructor for Gson/Retrofit
    public Patient() {
        this.encounters = new ArrayList<>();
    }

    // Constructor with required fields
    public Patient(String patientId, String name, String gender) {
        this.patientId = patientId;
        this.name = name;
        this.gender = gender;
        this.encounters = new ArrayList<>();
    }

    // Full constructor
    public Patient(String patientId, String name, Date dateOfBirth, String gender, List<Encounter> encounters) {
        this.patientId = patientId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.encounters = encounters != null ? encounters : new ArrayList<>();
    }

    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<Encounter> getEncounters() {
        return encounters;
    }

    public void setEncounters(List<Encounter> encounters) {
        this.encounters = encounters != null ? encounters : new ArrayList<>();
    }

    public void addEncounter(Encounter encounter) {
        if (encounters == null) {
            encounters = new ArrayList<>();
        }
        encounters.add(encounter);
    }
}

