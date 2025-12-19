package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Observation Resource
 * Represents clinical observations in FHIR-compliant format
 * https://www.hl7.org/fhir/observation.html
 */
public class FHIRObservation {
    @SerializedName("resourceType")
    private String resourceType = "Observation";

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status; // registered | preliminary | final | amended | corrected | cancelled |
                           // entered-in-error | unknown

    @SerializedName("code")
    private CodeableConcept code;

    @SerializedName("subject")
    private Reference subject; // Reference to Patient

    @SerializedName("encounter")
    private Reference encounter; // Reference to Encounter

    @SerializedName("performer")
    private List<Reference> performer; // List of performers (e.g. Practitioner)

    @SerializedName("effectiveDateTime")
    private String effectiveDateTime; // ISO 8601 format

    @SerializedName("valueString")
    private String valueString;

    @SerializedName("valueCodeableConcept")
    private CodeableConcept valueCodeableConcept;

    @SerializedName("component")
    private List<Component> component;

    public FHIRObservation() {
        this.component = new ArrayList<>();
    }

    public FHIRObservation(String id, String status, String diseaseType, String patientReference,
            String encounterReference, String effectiveDateTime, String severity,
            List<String> symptoms, String observationDetails) {
        this();
        this.id = id;
        this.resourceType = "Observation";
        this.status = status != null ? status : "final";

        // Set code (disease type)
        this.code = new CodeableConcept();
        Coding diseaseCoding = new Coding();
        diseaseCoding.setSystem("http://healthtracker.org/data/diseases");
        diseaseCoding.setCode(diseaseType != null ? diseaseType.toLowerCase().replace(" ", "-") : "unknown");
        diseaseCoding.setDisplay(diseaseType != null ? diseaseType : "Unknown Disease");
        this.code.getCoding().add(diseaseCoding);
        this.code.setText(diseaseType);

        // Set subject (Patient reference)
        this.subject = new Reference();
        this.subject.setReference(patientReference);

        // Set encounter reference
        this.encounter = new Reference();
        this.encounter.setReference(encounterReference);

        // Set effective date/time
        this.effectiveDateTime = effectiveDateTime;

        // Set value (severity)
        if (severity != null) {
            this.valueCodeableConcept = new CodeableConcept();
            Coding severityCoding = new Coding();
            severityCoding.setSystem("http://healthtracker.org/data/severity");
            severityCoding.setCode(severity);
            severityCoding.setDisplay(severity.substring(0, 1).toUpperCase() + severity.substring(1));
            this.valueCodeableConcept.getCoding().add(severityCoding);
            this.valueCodeableConcept.setText(severity);
        }

        // Set observation details as valueString if provided
        if (observationDetails != null && !observationDetails.isEmpty()) {
            this.valueString = observationDetails;
        }

        // Add symptoms as components
        if (symptoms != null && !symptoms.isEmpty()) {
            for (String symptom : symptoms) {
                Component comp = new Component();
                comp.setCode(new CodeableConcept());
                Coding symptomCoding = new Coding();
                symptomCoding.setSystem("http://healthtracker.org/data/symptoms");
                symptomCoding.setCode(symptom.toLowerCase().replace(" ", "-"));
                symptomCoding.setDisplay(symptom);
                comp.getCode().getCoding().add(symptomCoding);
                comp.getCode().setText(symptom);
                comp.setValueCodeableConcept(new CodeableConcept());
                comp.getValueCodeableConcept().setText("present");
                this.component.add(comp);
            }
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

    public CodeableConcept getCode() {
        return code;
    }

    public void setCode(CodeableConcept code) {
        this.code = code;
    }

    public Reference getSubject() {
        return subject;
    }

    public void setSubject(Reference subject) {
        this.subject = subject;
    }

    public Reference getEncounter() {
        return encounter;
    }

    public void setEncounter(Reference encounter) {
        this.encounter = encounter;
    }

    public List<Reference> getPerformer() {
        return performer;
    }

    public void setPerformer(List<Reference> performer) {
        this.performer = performer;
    }

    public String getEffectiveDateTime() {
        return effectiveDateTime;
    }

    public void setEffectiveDateTime(String effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public CodeableConcept getValueCodeableConcept() {
        return valueCodeableConcept;
    }

    public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
        this.valueCodeableConcept = valueCodeableConcept;
    }

    public List<Component> getComponent() {
        return component;
    }

    public void setComponent(List<Component> component) {
        this.component = component;
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

    public static class Component {
        @SerializedName("code")
        private CodeableConcept code;

        @SerializedName("valueCodeableConcept")
        private CodeableConcept valueCodeableConcept;

        @SerializedName("valueString")
        private String valueString;

        public CodeableConcept getCode() {
            return code;
        }

        public void setCode(CodeableConcept code) {
            this.code = code;
        }

        public CodeableConcept getValueCodeableConcept() {
            return valueCodeableConcept;
        }

        public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
            this.valueCodeableConcept = valueCodeableConcept;
        }

        public String getValueString() {
            return valueString;
        }

        public void setValueString(String valueString) {
            this.valueString = valueString;
        }
    }
}
