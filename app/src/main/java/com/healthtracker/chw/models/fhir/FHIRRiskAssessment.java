package com.healthtracker.chw.models.fhir;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * FHIR RiskAssessment Resource
 * Represents risk assessment in FHIR-compliant format
 * https://www.hl7.org/fhir/riskassessment.html
 */
public class FHIRRiskAssessment {
    @SerializedName("resourceType")
    private String resourceType = "RiskAssessment";

    @SerializedName("id")
    private String id;

    @SerializedName("status")
    private String status; // registered | preliminary | final | amended | corrected | entered-in-error |
                           // cancelled

    @SerializedName("subject")
    private Reference subject; // Reference to Patient

    @SerializedName("encounter")
    private Reference encounter; // Reference to Encounter

    @SerializedName("basis")
    private List<Reference> basis; // References to Observations or other resources

    @SerializedName("prediction")
    private List<Prediction> prediction;

    public FHIRRiskAssessment() {
        this.basis = new ArrayList<>();
        this.prediction = new ArrayList<>();
    }

    public FHIRRiskAssessment(String id, String status, String patientReference,
            String encounterReference, String riskLevel, String explanation) {
        this();
        this.id = id;
        this.resourceType = "RiskAssessment";
        this.status = status != null ? status : "final";

        // Set subject (Patient reference)
        this.subject = new Reference();
        this.subject.setReference(patientReference);

        // Set encounter reference
        this.encounter = new Reference();
        this.encounter.setReference(encounterReference);

        // Set prediction (risk level)
        Prediction pred = new Prediction();
        CodeableConcept outcome = new CodeableConcept();
        Coding outcomeCoding = new Coding();
        outcomeCoding.setSystem("http://healthtracker.org/data/risk");
        outcomeCoding.setCode(riskLevel != null ? riskLevel.toLowerCase() : "low");
        outcomeCoding.setDisplay(
                riskLevel != null ? riskLevel.substring(0, 1).toUpperCase() + riskLevel.substring(1) : "Low");
        outcome.getCoding().add(outcomeCoding);
        outcome.setText(riskLevel != null ? riskLevel : "Low Risk");
        pred.setOutcome(outcome);

        // Set probability (risk score based on level)
        if (riskLevel != null) {
            if (riskLevel.equalsIgnoreCase("severe") || riskLevel.equalsIgnoreCase("high")) {
                pred.setProbabilityDecimal(0.8);
            } else if (riskLevel.equalsIgnoreCase("moderate") || riskLevel.equalsIgnoreCase("medium")) {
                pred.setProbabilityDecimal(0.5);
            } else {
                pred.setProbabilityDecimal(0.2);
            }
        } else {
            pred.setProbabilityDecimal(0.2);
        }

        // Set explanation
        if (explanation != null && !explanation.isEmpty()) {
            pred.setRationale(explanation);
        }

        this.prediction.add(pred);
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

    public List<Reference> getBasis() {
        return basis;
    }

    public void setBasis(List<Reference> basis) {
        this.basis = basis;
    }

    public List<Prediction> getPrediction() {
        return prediction;
    }

    public void setPrediction(List<Prediction> prediction) {
        this.prediction = prediction;
    }

    // Nested classes
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

    public static class Prediction {
        @SerializedName("outcome")
        private CodeableConcept outcome;

        @SerializedName("probabilityDecimal")
        private Double probabilityDecimal;

        @SerializedName("rationale")
        private String rationale;

        public CodeableConcept getOutcome() {
            return outcome;
        }

        public void setOutcome(CodeableConcept outcome) {
            this.outcome = outcome;
        }

        public Double getProbabilityDecimal() {
            return probabilityDecimal;
        }

        public void setProbabilityDecimal(Double probabilityDecimal) {
            this.probabilityDecimal = probabilityDecimal;
        }

        public String getRationale() {
            return rationale;
        }

        public void setRationale(String rationale) {
            this.rationale = rationale;
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
}
