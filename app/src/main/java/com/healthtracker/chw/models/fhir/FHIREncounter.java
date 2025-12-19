package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Encounter Resource
 * Represents a healthcare encounter in FHIR-compliant format
 * https://www.hl7.org/fhir/encounter.html
 */
public class FHIREncounter {
    @SerializedName("resourceType")
    private String resourceType = "Encounter";
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("status")
    private String status; // planned | arrived | triaged | in-progress | onleave | finished | cancelled
    
    @SerializedName("class")
    private Coding encounterClass;
    
    @SerializedName("type")
    private List<CodeableConcept> type;
    
    @SerializedName("subject")
    private Reference subject; // Reference to Patient
    
    @SerializedName("period")
    private Period period;
    
    @SerializedName("location")
    private List<EncounterLocation> location;

    public FHIREncounter() {
        this.type = new ArrayList<>();
        this.location = new ArrayList<>();
    }

    public FHIREncounter(String id, String status, String encounterType, String patientReference, String locationReference, String startDateTime) {
        this();
        this.id = id;
        this.resourceType = "Encounter";
        this.status = status != null ? status : "finished";
        
        // Set encounter class
        this.encounterClass = new Coding();
        this.encounterClass.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        this.encounterClass.setCode("AMB");
        this.encounterClass.setDisplay("ambulatory");
        
        // Set encounter type
        CodeableConcept typeConcept = new CodeableConcept();
        Coding typeCoding = new Coding();
        typeCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        typeCoding.setCode(encounterType != null ? encounterType : "home");
        typeCoding.setDisplay(encounterType != null ? encounterType.substring(0, 1).toUpperCase() + encounterType.substring(1) + " Visit" : "Home Visit");
        typeConcept.setCoding(new ArrayList<>());
        typeConcept.getCoding().add(typeCoding);
        this.type.add(typeConcept);
        
        // Set subject (Patient reference)
        this.subject = new Reference();
        this.subject.setReference(patientReference);
        
        // Set period
        this.period = new Period();
        this.period.setStart(startDateTime);
        this.period.setEnd(startDateTime);
        
        // Set location
        if (locationReference != null) {
            EncounterLocation encLocation = new EncounterLocation();
            Reference locRef = new Reference();
            locRef.setReference(locationReference);
            encLocation.setLocation(locRef);
            this.location.add(encLocation);
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Coding getEncounterClass() {
        return encounterClass;
    }

    public void setEncounterClass(Coding encounterClass) {
        this.encounterClass = encounterClass;
    }

    public List<CodeableConcept> getType() {
        return type;
    }

    public void setType(List<CodeableConcept> type) {
        this.type = type;
    }

    public Reference getSubject() {
        return subject;
    }

    public void setSubject(Reference subject) {
        this.subject = subject;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public List<EncounterLocation> getLocation() {
        return location;
    }

    public void setLocation(List<EncounterLocation> location) {
        this.location = location;
    }

    // Nested classes
    public static class Coding {
        @SerializedName("system")
        private String system;
        
        @SerializedName("code")
        private String code;
        
        @SerializedName("display")
        private String display;

        public String getSystem() {
            return system;
        }

        public void setSystem(String system) {
            this.system = system;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public static class CodeableConcept {
        @SerializedName("coding")
        private List<Coding> coding;
        
        @SerializedName("text")
        private String text;

        public CodeableConcept() {
            this.coding = new ArrayList<>();
        }

        public List<Coding> getCoding() {
            return coding;
        }

        public void setCoding(List<Coding> coding) {
            this.coding = coding;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Reference {
        @SerializedName("reference")
        private String reference;
        
        @SerializedName("display")
        private String display;

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }

    public static class Period {
        @SerializedName("start")
        private String start;
        
        @SerializedName("end")
        private String end;

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }

    public static class EncounterLocation {
        @SerializedName("location")
        private Reference location;

        public Reference getLocation() {
            return location;
        }

        public void setLocation(Reference location) {
            this.location = location;
        }
    }
}

