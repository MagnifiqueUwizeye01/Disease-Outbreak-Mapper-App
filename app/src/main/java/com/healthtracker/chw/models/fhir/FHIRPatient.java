package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Patient Resource
 * Represents a patient in FHIR-compliant format
 * https://www.hl7.org/fhir/patient.html
 */
public class FHIRPatient {
    @SerializedName("resourceType")
    private String resourceType = "Patient";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("identifier")
    private List<Identifier> identifier;
    
    @SerializedName("name")
    private List<HumanName> name;
    
    @SerializedName("gender")
    private String gender; // male | female | other | unknown
    
    @SerializedName("birthDate")
    private String birthDate; // YYYY-MM-DD format
    
    @SerializedName("address")
    private List<Address> address;

    public FHIRPatient() {
        this.identifier = new ArrayList<>();
        this.name = new ArrayList<>();
        this.address = new ArrayList<>();
    }

    public FHIRPatient(String id, String name, String gender, String birthDate) {
        this();
        this.id = id;
        this.resourceType = "Patient";
        
        // Add name
        HumanName humanName = new HumanName();
        humanName.setFamily(name);
        humanName.setText(name);
        this.name.add(humanName);
        
        this.gender = gender;
        this.birthDate = birthDate;
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

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<HumanName> getName() {
        return name;
    }

    public void setName(List<HumanName> name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    // Nested classes for FHIR structure
    public static class Identifier {
        @SerializedName("system")
        private String system;
        
        @SerializedName("value")
        private String value;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class HumanName {
        @SerializedName("text")
        private String text;
        
        @SerializedName("family")
        private String family;
        
        @SerializedName("given")
        private List<String> given;

        public HumanName() {
            this.given = new ArrayList<>();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public List<String> getGiven() {
            return given;
        }

        public void setGiven(List<String> given) {
            this.given = given;
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

