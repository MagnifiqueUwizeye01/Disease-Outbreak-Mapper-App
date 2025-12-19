package com.healthtracker.chw.models;

/**
 * GPS Location model
 * Represents a geographical location with coordinates and address
 * 
 * Relationships:
 * - Belongs to 1 Encounter (Many-to-One)
 */
public class GPSLocation {
    private String locationId;
    private Double latitude;
    private Double longitude;
    private String address;
    private Encounter encounter; // Many-to-One relationship

    // Default constructor for Gson/Retrofit
    public GPSLocation() {
    }

    // Constructor with coordinates
    public GPSLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Full constructor
    public GPSLocation(String locationId, Double latitude, Double longitude, String address) {
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    // Getters and Setters
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
}

