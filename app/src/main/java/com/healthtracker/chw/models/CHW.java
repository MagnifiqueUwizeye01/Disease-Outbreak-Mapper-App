package com.healthtracker.chw.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Community Health Worker model
 * Represents a CHW who creates encounters and reports disease cases
 * 
 * Relationships:
 * - Creates many Encounters (One-to-Many)
 */
public class CHW {
    private String id;
    private String name;
    private String role;
    private List<Encounter> encounters; // One-to-Many relationship

    // Default constructor for Gson/Retrofit
    public CHW() {
        this.encounters = new ArrayList<>();
    }

    // Constructor with required fields
    public CHW(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.encounters = new ArrayList<>();
    }

    // Full constructor
    public CHW(String id, String name, String role, List<Encounter> encounters) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.encounters = encounters != null ? encounters : new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

