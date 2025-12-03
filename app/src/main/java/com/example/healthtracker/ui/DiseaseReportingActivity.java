package com.example.healthtracker.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.healthtracker.R;
import com.example.healthtracker.db.entities.ReportEntity;
import com.example.healthtracker.fhir.FHIRPayloadBuilder;
import com.example.healthtracker.models.GPSLocation;
import com.example.healthtracker.models.ReportModel;
import com.example.healthtracker.repository.ReportRepository;
import com.example.healthtracker.services.GPSService;
import com.example.healthtracker.workers.OfflineSyncWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class DiseaseReportingActivity extends AppCompatActivity {

    private Spinner diseaseSpinner;
    private EditText patientNameInput;
    private EditText patientAgeInput;
    private EditText notesInput;
    private Button submitBtn;

    private GPSService gpsService;
    private ReportRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_reporting);

        diseaseSpinner = findViewById(R.id.spinner_disease);
        patientNameInput = findViewById(R.id.input_patient_name);
        patientAgeInput = findViewById(R.id.input_patient_age);
        notesInput = findViewById(R.id.input_notes);
        submitBtn = findViewById(R.id.button_submit);

        // simple disease list
        String[] diseases = new String[]{"Malaria", "Cholera", "Dengue", "COVID-19", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diseases);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diseaseSpinner.setAdapter(adapter);

        gpsService = new GPSService(this);
        repo = new ReportRepository(this);

        submitBtn.setOnClickListener(v -> onSubmit());
    }

    private void onSubmit() {
        String disease = (String) diseaseSpinner.getSelectedItem();
        String patientName = patientNameInput.getText().toString().trim();
        String patientAgeStr = patientAgeInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        // FR003: validate required fields
        if (disease == null || disease.isEmpty()) {
            Toast.makeText(this, "Select disease", Toast.LENGTH_SHORT).show();
            return;
        }
        if (patientName.isEmpty()) {
            patientNameInput.setError("Patient name required");
            return;
        }

        // ✔ SAFE AGE PARSING
        int tempAge = 0;
        try {
            tempAge = Integer.parseInt(patientAgeStr);
        } catch (Exception ignored) { }
        final int patientAge = tempAge;

        // FR002: capture location then build FHIR and queue
        gpsService.requestLocation(new GPSService.Callback() {
            @Override
            public void onLocation(String isoTimestamp, double lat, double lon, String address) {
                ReportModel rm = new ReportModel();
                rm.id = UUID.randomUUID().toString();
                rm.disease = disease;
                rm.patientName = patientName;
                rm.patientAge = patientAge;
                rm.notes = notes;

                GPSLocation gl = new GPSLocation();
                gl.latitude = lat;
                gl.longitude = lon;
                gl.address = address;
                gl.timestampIso = isoTimestamp;

                rm.location = gl;
                rm.timestampIso = isoTimestamp;
                rm.status = "PENDING";

                // Build FHIR payload
                try {
                    JSONObject observation = FHIRPayloadBuilder.buildObservation(rm);
                    rm.fhirPayload = observation.toString();

                    // convert to entity
                    ReportEntity e = new ReportEntity();
                    e.uuid = rm.id;
                    e.disease = rm.disease;
                    e.patientName = rm.patientName;
                    e.patientAge = rm.patientAge;
                    e.notes = rm.notes;
                    e.latitude = rm.location.latitude;
                    e.longitude = rm.location.longitude;
                    e.timestampIso = rm.timestampIso;
                    e.payloadJson = rm.fhirPayload;
                    e.status = "PENDING";

                    repo.insert(e, id -> {
                        runOnUiThread(() -> {
                            Toast.makeText(DiseaseReportingActivity.this, "Report queued (offline-safe)", Toast.LENGTH_SHORT).show();

                            OneTimeWorkRequest workRequest =
                                    new OneTimeWorkRequest.Builder(OfflineSyncWorker.class).build();
                            WorkManager.getInstance(DiseaseReportingActivity.this).enqueue(workRequest);
                        });
                    });
                } catch (JSONException ex) {
                    runOnUiThread(() ->
                            Toast.makeText(DiseaseReportingActivity.this,
                                    "Failed to build payload: " + ex.getMessage(),
                                    Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onError(String reason) {
                runOnUiThread(() ->
                        Toast.makeText(DiseaseReportingActivity.this,
                                "GPS error: " + reason,
                                Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted =
                grantResults.length > 0 &&
                        grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED;

        if (granted) {
            Toast.makeText(this, "Permission granted. Please tap Submit again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location permission required to submit reports.", Toast.LENGTH_LONG).show();
        }
    }
}
