package com.example.healthtracker.fhir;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import com.example.healthtracker.models.ReportModel;
import com.example.healthtracker.models.GPSLocation;
import com.google.firebase.firestore.util.Assert;

public class FHIRPayloadBuilderTest {

    @Test
    public void testBuildObservationMin() throws Exception {
        ReportModel r = new ReportModel();
        r.id = "local-1";
        r.disease = "Malaria";
        r.patientName = "John Doe";
        r.timestampIso = "2025-12-01T15:00:00Z";
        GPSLocation gl = new GPSLocation();
        gl.latitude = -1.95;
        gl.longitude = 30.05;
        r.location = gl;

        JSONObject o = FHIRPayloadBuilder.buildObservation(r);
        assertEquals("Observation", o.getString("resourceType"));
        assertEquals("final", o.getString("status"));
        assertTrue(o.getString("valueString").contains("Malaria"));
        assertNotNull(o.getJSONObject("location"));
    }
}