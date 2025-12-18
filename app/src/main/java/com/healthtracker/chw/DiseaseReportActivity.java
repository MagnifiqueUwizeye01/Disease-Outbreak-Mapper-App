package com.healthtracker.chw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class DiseaseReportActivity extends AppCompatActivity {

    private Spinner spinnerDisease;
    private LinearLayout layoutOtherDisease;
    private EditText etOtherDisease, etPatientFullName, etPatientAge, etSymptoms;
    private TextView tvLocation;
    private Button btnSubmit, btnCaptureLocation, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_report);

        // Hide the action bar that says "HealthTracker"
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupBackPressHandler();
        initViews();
        setupClickListeners();
        setupDiseaseSpinner();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Go back to dashboard
            }
        });
    }

    private void initViews() {
        spinnerDisease = findViewById(R.id.spinnerDisease);
        layoutOtherDisease = findViewById(R.id.layoutOtherDisease);
        etOtherDisease = findViewById(R.id.etOtherDisease);
        etPatientFullName = findViewById(R.id.etPatientFullName);
        etPatientAge = findViewById(R.id.etPatientAge);
        etSymptoms = findViewById(R.id.etSymptoms);
        tvLocation = findViewById(R.id.tvLocation);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCaptureLocation = findViewById(R.id.btnCaptureLocation);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupDiseaseSpinner() {
        // Disease options including "Other"
        String[] diseases = {
                "Select Disease *",
                "Cholera",
                "Measles",
                "Dysentery",
                "Malaria",
                "Tuberculosis",
                "COVID-19",
                "Typhoid",
                "Hepatitis",
                "HIV/AIDS",
                "Other (Specify below)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diseases);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisease.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Disease spinner listener - show/hide "Other" input
        spinnerDisease.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedDisease = parent.getItemAtPosition(position).toString();
                if ("Other (Specify below)".equals(selectedDisease)) {
                    layoutOtherDisease.setVisibility(View.VISIBLE);
                } else {
                    layoutOtherDisease.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                layoutOtherDisease.setVisibility(View.GONE);
            }
        });

        btnCaptureLocation.setOnClickListener(v -> {
            // Simulate GPS capture
            tvLocation.setText("Location: -1.9396, 30.0588 (Kigali, Rwanda)");
            Toast.makeText(this, "GPS location captured!", Toast.LENGTH_SHORT).show();
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateAndSubmitReport()) {
                Toast.makeText(this, "Disease report submitted successfully!", Toast.LENGTH_LONG).show();
                finish(); // Go back to dashboard
            }
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Go back to dashboard
        });
    }

    private boolean validateAndSubmitReport() {
        String disease = spinnerDisease.getSelectedItem().toString();
        String otherDisease = etOtherDisease.getText().toString().trim();
        String patientFullName = etPatientFullName.getText().toString().trim();
        String patientAge = etPatientAge.getText().toString().trim();
        String location = tvLocation.getText().toString();
        String symptoms = etSymptoms.getText().toString().trim();

        // Validation
        if (disease.equals("Select Disease *")) {
            Toast.makeText(this, "Please select a disease", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (disease.equals("Other (Specify below)") && otherDisease.isEmpty()) {
            Toast.makeText(this, "Please specify the disease name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (patientFullName.isEmpty()) {
            Toast.makeText(this, "Please enter patient full name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (patientAge.isEmpty()) {
            Toast.makeText(this, "Please enter patient age", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (location.equals("Location: Not captured")) {
            Toast.makeText(this, "Please capture location first", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get final disease name
        String finalDisease = disease.equals("Other (Specify below)") ? otherDisease : disease;

        // ✅ SAVE REPORT TO HISTORY
        SharedPreferences sharedPreferences = getSharedPreferences("HealthTrackerPrefs", MODE_PRIVATE);
        ReportHistoryActivity.saveReport(sharedPreferences, finalDisease, patientFullName, patientAge, location, symptoms);

        // Show success message with report summary
        String reportSummary = "✅ Report Submitted!\n" +
                "Disease: " + finalDisease +
                "\nPatient: " + patientFullName +
                "\nAge: " + patientAge +
                "\nLocation: " + location;

        Toast.makeText(this, reportSummary, Toast.LENGTH_LONG).show();
        return true;
    }
}