package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Location Resource
 * Represents a geographical location in FHIR-compliant format
 * https://www.hl7.org/fhir/location.html
 */
public class FHIRLocation {
    @SerializedName("resourceType")
    private String resourceType = "Location";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("position")
    private Position position;
    
    @SerializedName("address")
    private Address address;

    public FHIRLocation() {
    }

    public FHIRLocation(String id, String name, String description, Double latitude, Double longitude, String addressText) {
        this.id = id;
        this.resourceType = "Location";
        this.name = name != null ? name : "GPS Location";
        this.description = description;
        
        // Set position (coordinates)
        this.position = new Position();
        this.position.setLatitude(latitude);
        this.position.setLongitude(longitude);
        
        // Set address
        if (addressText != null && !addressText.isEmpty()) {
            this.address = new Address();
            this.address.setText(addressText);
        }
    }

    // Getters and Setters
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    // Nested classes
    public static class Position {
        @SerializedName("latitude")
        private Double latitude;
        
        @SerializedName("longitude")
        private Double longitude;
        
        @SerializedName("altitude")
        private Double altitude;

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

        public Double getAltitude() {
            return altitude;
        }

        public void setAltitude(Double altitude) {
            this.altitude = altitude;
        }
    }

    public static class Address {
        @SerializedName("text")
        private String text;
        
        @SerializedName("line")
        private List<String> line;
        
        @SerializedName("city")
        private String city;
        
        @SerializedName("state")
        private String state;
        
        @SerializedName("postalCode")
        private String postalCode;
        
        @SerializedName("country")
        private String country;

        public Address() {
            this.line = new ArrayList<>();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getLine() {
            return line;
        }

        public void setLine(List<String> line) {
            this.line = line;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}

