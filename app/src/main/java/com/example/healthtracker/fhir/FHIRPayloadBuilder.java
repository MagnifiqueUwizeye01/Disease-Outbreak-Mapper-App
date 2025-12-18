package com.example.healthtracker.fhir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.healthtracker.models.GPSLocation;
import com.example.healthtracker.models.PatientModel;
import com.example.healthtracker.models.ReportModel;

import java.util.UUID;

/**
 * Simple FHIR R4 JSON builders for Patient, Observation, Location, RiskAssessment.
 * This is intentionally minimal and creates essential fields required in the spec.
 */
public class FHIRPayloadBuilder {

    public static JSONObject buildPatient(PatientModel p) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("resourceType", "Patient");
        if (p.id == null) p.id = UUID.randomUUID().toString();
        obj.put("id", p.id);

        if (p.name != null) {
            JSONArray nameArr = new JSONArray();
            JSONObject nameObj = new JSONObject();
            nameObj.put("text", p.name);
            nameArr.put(nameObj);
            obj.put("name", nameArr);
        }

        if (p.identifier != null) {
            JSONArray ids = new JSONArray();
            JSONObject idObj = new JSONObject();
            idObj.put("value", p.identifier);
            ids.put(idObj);
            obj.put("identifier", ids);
        }
        return obj;
    }

    public static JSONObject buildLocation(GPSLocation loc) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("resourceType", "Location");
        obj.put("id", UUID.randomUUID().toString());
        JSONObject position = new JSONObject();
        position.put("latitude", loc.latitude);
        position.put("longitude", loc.longitude);
        obj.put("position", position);
        if (loc.address != null) obj.put("address", new JSONObject().put("text", loc.address));
        return obj;
    }

    public static JSONObject buildObservation(ReportModel r) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("resourceType", "Observation");
        obj.put("status", "final");

        // category
        JSONArray categoryArr = new JSONArray();
        JSONObject cat = new JSONObject();
        JSONObject coding = new JSONObject();
        coding.put("system", "http://terminology.hl7.org/CodeSystem/observation-category");
        coding.put("code", "survey");
        JSONArray codingArr = new JSONArray();
        codingArr.put(coding);
        cat.put("coding", codingArr);
        categoryArr.put(cat);
        obj.put("category", categoryArr);

        JSONObject code = new JSONObject();
        code.put("text", "Disease Report");
        obj.put("code", code);

        if (r.patientName != null) {
            JSONObject subject = new JSONObject();
            // referencing a patient resource (if created); use local reference
            // note: in full implementation you'd create or lookup Patient/{id}
            subject.put("reference", "Patient/" + (r.id != null ? r.id : "local-" + UUID.randomUUID().toString()));
            obj.put("subject", subject);
        }

        if (r.timestampIso != null) obj.put("effectiveDateTime", r.timestampIso);
        obj.put("valueString", r.disease != null ? r.disease : "unknown");

        if (r.location != null) {
            JSONObject locRef = new JSONObject();
            locRef.put("reference", "Location/" + UUID.randomUUID().toString());
            obj.put("location", locRef);
        }

        // attach notes
        if (r.notes != null && !r.notes.isEmpty()) obj.put("note", new JSONArray().put(new JSONObject().put("text", r.notes)));

        return obj;
    }

    public static JSONObject buildRiskAssessment(String id, String status, String riskLevel, String subjectRef) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("resourceType", "RiskAssessment");
        obj.put("id", id != null ? id : UUID.randomUUID().toString());
        obj.put("status", status != null ? status : "final");
        if (subjectRef != null) obj.put("subject", new JSONObject().put("reference", subjectRef));
        obj.put("prediction", new JSONArray().put(new JSONObject()
                .put("outcome", new JSONObject().put("text", "Community risk level"))
                .put("probabilityString", riskLevel)));
        return obj;
    }
}