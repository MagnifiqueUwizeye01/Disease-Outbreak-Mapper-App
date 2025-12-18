package com.healthtracker.chw.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.healthtracker.chw.R;
import android.content.SharedPreferences;
import com.healthtracker.chw.services.GPSService;
<<<<<<< HEAD
import com.healthtracker.chw.services.SupabaseService;

import org.json.JSONObject;

import org.json.JSONObject;
=======
import com.healthtracker.chw.services.FHIRService;
>>>>>>> de27ee7 (Implement user authentication service and session management)

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Disease reporting form for CHWs
 * Implements FR001: Disease dropdown
 * Implements FR002: Auto-capture GPS coordinates on submit
 * Implements FR003: Validate required fields
 * Implements FR004: Convert submitted data into FHIR Observation JSON
 */
public class ReportCaseFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // Disease types for dropdown (FR001)
    private static final String[] DISEASE_TYPES = {
<<<<<<< HEAD
        "Malaria",
        "Cholera",
        "Dengue",
        "Ebola",
        "COVID-19",
        "Tuberculosis",
        "Measles",
        "Yellow Fever",
        "Meningitis",
        "Other"
=======
            "Malaria",
            "Cholera",
            "Dengue",
            "Ebola",
            "COVID-19",
            "Tuberculosis",
            "Measles",
            "Yellow Fever",
            "Meningitis",
            "Other"
>>>>>>> de27ee7 (Implement user authentication service and session management)
    };

    // UI Components - Patient Information
    private TextInputEditText etPatientName;
    private TextInputEditText etDateOfBirth;
    private TextInputEditText etPatientAge;
    private ChipGroup chipGender;
<<<<<<< HEAD
    
    // UI Components - Encounter Information
    private TextInputEditText etEncounterDate;
    private ChipGroup chipEncounterType;
    
=======

    // UI Components - Encounter Information
    private TextInputEditText etEncounterDate;
    private ChipGroup chipEncounterType;

>>>>>>> de27ee7 (Implement user authentication service and session management)
    // UI Components - Disease Report
    private android.widget.AutoCompleteTextView etDiseaseType;
    private TextInputEditText etReportDate;
    private ChipGroup chipSymptoms;
    private android.widget.RadioGroup radioSeverity;
    private TextInputEditText etObservationDetails;
<<<<<<< HEAD
    
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
    // UI Components - Location
    private View cardLocation;
    private android.widget.TextView tvLocationStatus;
    private android.widget.TextView tvLocationCoordinates;
    private android.widget.TextView tvLocationAddress;
<<<<<<< HEAD
    
    // UI Components - Additional
    private TextInputEditText etNotes;
    private MaterialButton btnSubmitCase;
=======

    // UI Components - Additional
    private TextInputEditText etNotes;
    private MaterialButton btnSubmitCase;
    private com.google.android.material.button.MaterialButtonToggleGroup toggleConnectionMode; // Added
>>>>>>> de27ee7 (Implement user authentication service and session management)

    // Data
    private Double capturedLatitude;
    private Double capturedLongitude;
    private String capturedAddress;
    private GPSService gpsService;

    @Nullable
    @Override
<<<<<<< HEAD
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_case, container, false);
        
=======
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_case, container, false);

>>>>>>> de27ee7 (Implement user authentication service and session management)
        initializeViews(view);
        setupDiseaseDropdown();
        setupLocationCapture();
        setupSubmitButton();
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        return view;
    }

    private void initializeViews(View view) {
<<<<<<< HEAD
        // Patient Information
        etPatientName = view.findViewById(R.id.et_patient_name);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etPatientAge = view.findViewById(R.id.et_patient_age);
        chipGender = view.findViewById(R.id.chip_gender);
        
        // Encounter Information
        etEncounterDate = view.findViewById(R.id.et_encounter_date);
        chipEncounterType = view.findViewById(R.id.chip_encounter_type);
        
        // Disease Report
        etDiseaseType = (android.widget.AutoCompleteTextView) view.findViewById(R.id.et_disease_type);
=======
        // Toggle Mode
        toggleConnectionMode = view.findViewById(R.id.toggle_connection_mode); // Added

        // Patient Information
        etPatientName = view.findViewById(R.id.et_patient_name);

        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etPatientAge = view.findViewById(R.id.et_patient_age);
        chipGender = view.findViewById(R.id.chip_gender);

        // Encounter Information
        etEncounterDate = view.findViewById(R.id.et_encounter_date);
        chipEncounterType = view.findViewById(R.id.chip_encounter_type);

        // Disease Report
        // Safely cast to AutoCompleteTextView - if layout has wrong type, this will
        // fail gracefully
        try {
            View diseaseTypeView = view.findViewById(R.id.et_disease_type);
            if (diseaseTypeView instanceof android.widget.AutoCompleteTextView) {
                etDiseaseType = (android.widget.AutoCompleteTextView) diseaseTypeView;
            } else {
                android.util.Log.e("ReportCaseFragment", "et_disease_type is not AutoCompleteTextView, found: " +
                        (diseaseTypeView != null ? diseaseTypeView.getClass().getName() : "null"));
                // Fallback: create a new AutoCompleteTextView or handle error
                throw new ClassCastException("et_disease_type must be AutoCompleteTextView");
            }
        } catch (ClassCastException e) {
            android.util.Log.e("ReportCaseFragment", "Error casting disease type view", e);
            // Show error to user
            if (isAdded() && getContext() != null) {
                android.widget.Toast.makeText(getContext(),
                        "Layout error: Disease type field is incorrectly configured",
                        android.widget.Toast.LENGTH_LONG).show();
            }
        }
>>>>>>> de27ee7 (Implement user authentication service and session management)
        etReportDate = view.findViewById(R.id.et_report_date);
        chipSymptoms = view.findViewById(R.id.chip_symptoms);
        radioSeverity = view.findViewById(R.id.radio_severity);
        etObservationDetails = view.findViewById(R.id.et_observation_details);
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Location
        cardLocation = view.findViewById(R.id.card_location);
        tvLocationStatus = view.findViewById(R.id.tv_location_status);
        tvLocationCoordinates = view.findViewById(R.id.tv_location_coordinates);
        tvLocationAddress = view.findViewById(R.id.tv_location_address);
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Additional
        etNotes = view.findViewById(R.id.et_notes);
        btnSubmitCase = view.findViewById(R.id.btn_submit_case);

        gpsService = new GPSService(requireContext());
<<<<<<< HEAD
        
        // Set current date/time for encounter and report date
        setupDateFields();
        
        // Setup chip selection listeners for visual feedback
        setupChipSelections();
    }
    
=======

        // Set current date/time for encounter and report date
        setupDateFields();

        // Setup chip selection listeners for visual feedback
        setupChipSelections();
    }

>>>>>>> de27ee7 (Implement user authentication service and session management)
    /**
     * Setup chip selection listeners to show visual feedback
     */
    private void setupChipSelections() {
        // Gender chips - already handled by ChipGroup with singleSelection
        chipGender.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Choice style
        });
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Encounter type chips - already handled by ChipGroup with singleSelection
        chipEncounterType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Choice style
        });
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Symptoms chips - multiple selection with Filter style
        chipSymptoms.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Filter style
            // Selected chips will show checked state
        });
    }
<<<<<<< HEAD
    
    private void setupDateFields() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        
        // Set current date for report date
        etReportDate.setText(dateFormat.format(new Date()));
        etReportDate.setOnClickListener(v -> showDatePicker(etReportDate, dateFormat));
        
        // Set current date/time for encounter date
        etEncounterDate.setText(dateTimeFormat.format(new Date()));
        etEncounterDate.setOnClickListener(v -> showDateTimePicker(etEncounterDate, dateTimeFormat));
        
        // Setup date of birth picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker(etDateOfBirth, dateFormat));
    }
    
=======

    private void setupDateFields() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault());
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault());

        // Set current date for report date
        etReportDate.setText(dateFormat.format(new Date()));
        etReportDate.setOnClickListener(v -> showDatePicker(etReportDate, dateFormat));

        // Set current date/time for encounter date
        etEncounterDate.setText(dateTimeFormat.format(new Date()));
        etEncounterDate.setOnClickListener(v -> showDateTimePicker(etEncounterDate, dateTimeFormat));

        // Setup date of birth picker
        etDateOfBirth.setOnClickListener(v -> showDatePicker(etDateOfBirth, dateFormat));
    }

>>>>>>> de27ee7 (Implement user authentication service and session management)
    private void showDatePicker(TextInputEditText editText, java.text.SimpleDateFormat format) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try {
            if (!TextUtils.isEmpty(editText.getText())) {
                calendar.setTime(format.parse(editText.getText().toString()));
            }
        } catch (Exception e) {
            // Use current date
        }
<<<<<<< HEAD
        
        new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            editText.setText(format.format(calendar.getTime()));
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), 
           calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }
    
=======

        new android.app.DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            editText.setText(format.format(calendar.getTime()));
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

>>>>>>> de27ee7 (Implement user authentication service and session management)
    private void showDateTimePicker(TextInputEditText editText, java.text.SimpleDateFormat format) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try {
            if (!TextUtils.isEmpty(editText.getText())) {
                calendar.setTime(format.parse(editText.getText().toString()));
            }
        } catch (Exception e) {
            // Use current date/time
        }
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        new android.app.DatePickerDialog(requireContext(), (dateView, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            new android.app.TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(java.util.Calendar.MINUTE, minute);
                editText.setText(format.format(calendar.getTime()));
            }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true).show();
<<<<<<< HEAD
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), 
           calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
=======
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
>>>>>>> de27ee7 (Implement user authentication service and session management)
    }

    /**
     * Setup disease dropdown (FR001)
     * Creates an AutoCompleteTextView dropdown with disease types
     */
    private void setupDiseaseDropdown() {
        // Create adapter for dropdown
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
<<<<<<< HEAD
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            DISEASE_TYPES
        );
        
=======
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                DISEASE_TYPES);

>>>>>>> de27ee7 (Implement user authentication service and session management)
        etDiseaseType.setAdapter(adapter);
        etDiseaseType.setThreshold(1); // Show suggestions after 1 character
        etDiseaseType.setOnClickListener(v -> {
            if (etDiseaseType.getText().toString().isEmpty()) {
                etDiseaseType.showDropDown();
            }
        });
        etDiseaseType.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && etDiseaseType.getText().toString().isEmpty()) {
                etDiseaseType.showDropDown();
            }
        });
    }

    /**
     * Setup location capture (FR002)
     */
    private void setupLocationCapture() {
        cardLocation.setOnClickListener(v -> captureGPSLocation());
    }

    /**
     * Capture GPS location
     */
    private void captureGPSLocation() {
        // Check permissions
<<<<<<< HEAD
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
=======
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
>>>>>>> de27ee7 (Implement user authentication service and session management)
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        tvLocationStatus.setText("Capturing location...");
        tvLocationCoordinates.setText("Please wait...");

        gpsService.captureLocation(new GPSService.LocationCallback() {
            @Override
            public void onLocationCaptured(double latitude, double longitude, String address, long timestamp) {
                capturedLatitude = latitude;
                capturedLongitude = longitude;
                capturedAddress = address;

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded() && tvLocationStatus != null && tvLocationCoordinates != null) {
<<<<<<< HEAD
                    tvLocationStatus.setText("Location captured");
                    tvLocationCoordinates.setText(String.format("%.6f, %.6f", latitude, longitude));
=======
                            tvLocationStatus.setText("Location captured");
                            tvLocationCoordinates.setText(String.format("%.6f, %.6f", latitude, longitude));
>>>>>>> de27ee7 (Implement user authentication service and session management)
                            if (tvLocationAddress != null && address != null) {
                                tvLocationAddress.setText(address);
                            }
                        }
<<<<<<< HEAD
                });
=======
                    });
>>>>>>> de27ee7 (Implement user authentication service and session management)
                }
            }

            @Override
            public void onLocationError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded() && getContext() != null) {
                            if (tvLocationStatus != null) {
<<<<<<< HEAD
                    tvLocationStatus.setText("Location capture failed");
                            }
                            if (tvLocationCoordinates != null) {
                    tvLocationCoordinates.setText(error);
=======
                                tvLocationStatus.setText("Location capture failed");
                            }
                            if (tvLocationCoordinates != null) {
                                tvLocationCoordinates.setText(error);
>>>>>>> de27ee7 (Implement user authentication service and session management)
                            }
                            Toast.makeText(getContext(), "Location error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
<<<<<<< HEAD
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
=======
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
>>>>>>> de27ee7 (Implement user authentication service and session management)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureGPSLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Setup submit button
     */
    private void setupSubmitButton() {
        btnSubmitCase.setOnClickListener(v -> submitCaseReport());
    }

    /**
     * Submit case report
     * Implements FR003: Validate required fields
     * Implements FR004: Convert to FHIR JSON
     */
    private void submitCaseReport() {
        // Validate required fields (FR003)
        if (!validateForm()) {
            return;
        }

        // Auto-capture GPS if not already captured (FR002)
        if (capturedLatitude == null || capturedLongitude == null) {
            Toast.makeText(requireContext(), "Capturing location...", Toast.LENGTH_SHORT).show();
            captureGPSLocation();
<<<<<<< HEAD
            
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
            // Wait a bit and try again
            cardLocation.postDelayed(this::submitCaseReport, 2000);
            return;
        }

        try {
            // Get Patient Information
            String patientName = etPatientName.getText().toString().trim();
            String dateOfBirth = etDateOfBirth.getText().toString().trim();
            Integer patientAge = null;
            try {
                String ageStr = etPatientAge.getText().toString().trim();
                if (!TextUtils.isEmpty(ageStr)) {
                    patientAge = Integer.parseInt(ageStr);
                }
            } catch (NumberFormatException e) {
                // Age is optional
            }
            String gender = getSelectedGender();
<<<<<<< HEAD
            
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
            // Get Encounter Information
            String encounterDate = etEncounterDate.getText().toString().trim();
            String encounterType = getSelectedEncounterType();

            // Get Disease Report Information
            String diseaseType = etDiseaseType.getText().toString().trim();
            String reportDate = etReportDate.getText().toString().trim();
            String symptoms = getSelectedSymptoms();
            String severity = getSelectedSeverity();
            String observationDetails = etObservationDetails.getText().toString().trim();
<<<<<<< HEAD
            
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
            // Get Additional Notes
            String notes = etNotes.getText().toString().trim();

            // Get CHW information
            String[] chwInfo = getCHWInfo();
            String chwName = chwInfo[0];
            String chwId = chwInfo[1];
<<<<<<< HEAD
            
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
            // Get symptoms as list
            List<String> symptomsList = getSelectedSymptomsList();
            if (symptomsList.isEmpty()) {
                // At least one symptom should be selected or add a default
                symptomsList.add("General symptoms");
            }

            // Validate critical data
            if (patientName == null || patientName.isEmpty()) {
                Toast.makeText(requireContext(), "Patient name is required", Toast.LENGTH_SHORT).show();
                return;
            }
<<<<<<< HEAD
            
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
            if (diseaseType == null || diseaseType.isEmpty()) {
                Toast.makeText(requireContext(), "Disease type is required", Toast.LENGTH_SHORT).show();
                return;
            }
<<<<<<< HEAD
            
            if (capturedLatitude == null || capturedLongitude == null) {
                Toast.makeText(requireContext(), "GPS location is required. Please capture location first.", Toast.LENGTH_LONG).show();
                return;
            }
            
=======

            if (capturedLatitude == null || capturedLongitude == null) {
                Toast.makeText(requireContext(), "GPS location is required. Please capture location first.",
                        Toast.LENGTH_LONG).show();
                return;
            }

>>>>>>> de27ee7 (Implement user authentication service and session management)
            // Disable submit button to prevent multiple submissions
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(false);
                btnSubmitCase.setText("Submitting...");
            }
<<<<<<< HEAD
            
            // Show loading message
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Saving report to Supabase...", Toast.LENGTH_SHORT).show();
            }
            
            saveToSupabase(patientName, gender, dateOfBirth, patientAge, chwName, chwId,
=======

            // Show loading message
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Saving report to FHIR server...", Toast.LENGTH_SHORT).show();
            }

            saveToFHIR(patientName, gender, dateOfBirth, patientAge, chwName, chwId,
>>>>>>> de27ee7 (Implement user authentication service and session management)
                    capturedLatitude, capturedLongitude, capturedAddress,
                    encounterDate, encounterType, diseaseType, symptomsList, severity,
                    observationDetails, notes);

        } catch (Exception e) {
            android.util.Log.e("ReportCaseFragment", "Exception in submitCaseReport", e);
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Error submitting report: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            // Re-enable button in case of error
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(true);
                btnSubmitCase.setText("Submit Case Report");
            }
        }
    }

    /**
     * Validate form fields (FR003)
     */
    private boolean validateForm() {
        boolean isValid = true;

        // Patient name is required
        if (TextUtils.isEmpty(etPatientName.getText())) {
            etPatientName.setError("Patient name is required");
            isValid = false;
        }

        // Gender is required
        if (chipGender.getCheckedChipId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select patient gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Encounter type is required
        if (chipEncounterType.getCheckedChipId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select encounter type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Disease type is required
        if (TextUtils.isEmpty(etDiseaseType.getText())) {
            etDiseaseType.setError("Disease type is required");
            isValid = false;
        }

        // Severity is required
        if (radioSeverity.getCheckedRadioButtonId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select case severity", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    /**
     * Get selected gender
     */
    private String getSelectedGender() {
        int selectedId = chipGender.getCheckedChipId();
<<<<<<< HEAD
        if (selectedId == R.id.chip_male) return "male";
        if (selectedId == R.id.chip_female) return "female";
        if (selectedId == R.id.chip_other) return "other";
=======
        if (selectedId == R.id.chip_male)
            return "male";
        if (selectedId == R.id.chip_female)
            return "female";
        if (selectedId == R.id.chip_other)
            return "other";
>>>>>>> de27ee7 (Implement user authentication service and session management)
        return null;
    }

    /**
     * Get selected encounter type
     */
    private String getSelectedEncounterType() {
        int selectedId = chipEncounterType.getCheckedChipId();
<<<<<<< HEAD
        if (selectedId == R.id.chip_encounter_home) return "home";
        if (selectedId == R.id.chip_encounter_clinic) return "clinic";
        if (selectedId == R.id.chip_encounter_emergency) return "emergency";
        return "home"; // Default
    }
    
    /**
     * Build Encounter FHIR resource
     */
    private JSONObject buildEncounterResource(String encounterId, String patientId, String locationId, 
                                               String encounterDate, String encounterType) {
        try {
            JSONObject encounter = new JSONObject();
            encounter.put("resourceType", "Encounter");
            encounter.put("id", encounterId);
            encounter.put("status", "finished");
            encounter.put("class", new JSONObject().put("code", "AMB"));
            
            // Subject (Patient)
            encounter.put("subject", new JSONObject().put("reference", "Patient/" + patientId));
            
            // Location
            encounter.put("location", new org.json.JSONArray().put(
                new JSONObject()
                    .put("location", new JSONObject().put("reference", "Location/" + locationId))
            ));
            
            // Period
            if (!TextUtils.isEmpty(encounterDate)) {
                try {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                    Date date = format.parse(encounterDate);
                    java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    encounter.put("period", new JSONObject()
                        .put("start", isoFormat.format(date))
                        .put("end", isoFormat.format(date))
                    );
                } catch (Exception e) {
                    // Use current date
                    java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    String now = isoFormat.format(new Date());
                    encounter.put("period", new JSONObject().put("start", now).put("end", now));
                }
            }
            
            // Type
            encounter.put("type", new org.json.JSONArray().put(
                new JSONObject().put("coding", new org.json.JSONArray().put(
                    new JSONObject()
                        .put("system", "http://terminology.hl7.org/CodeSystem/v3-ActCode")
                        .put("code", encounterType)
                        .put("display", encounterType.substring(0, 1).toUpperCase() + encounterType.substring(1) + " Visit")
                ))
            ));
            
            return encounter;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
=======
        if (selectedId == R.id.chip_encounter_home)
            return "home";
        if (selectedId == R.id.chip_encounter_clinic)
            return "clinic";
        if (selectedId == R.id.chip_encounter_emergency)
            return "emergency";
        return "home"; // Default
    }
>>>>>>> de27ee7 (Implement user authentication service and session management)

    /**
     * Get selected symptoms as List
     */
    private List<String> getSelectedSymptomsList() {
        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < chipSymptoms.getChildCount(); i++) {
            View child = chipSymptoms.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
<<<<<<< HEAD
            if (chip.isChecked()) {
                symptoms.add(chip.getText().toString());
            }
        }
        }
        return symptoms;
    }
    
=======
                if (chip.isChecked()) {
                    symptoms.add(chip.getText().toString());
                }
            }
        }
        return symptoms;
    }

>>>>>>> de27ee7 (Implement user authentication service and session management)
    /**
     * Get selected symptoms as String
     */
    private String getSelectedSymptoms() {
        List<String> symptoms = getSelectedSymptomsList();
        return symptoms.isEmpty() ? "" : String.join(", ", symptoms);
    }
<<<<<<< HEAD
    
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
    /**
     * Get CHW information from SharedPreferences or use defaults
     */
    private String[] getCHWInfo() {
<<<<<<< HEAD
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        String chwName = prefs.getString("chw_name", "CHW User");
        String chwId = prefs.getString("chw_id", "chw_" + System.currentTimeMillis());
        
=======
        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs",
                android.content.Context.MODE_PRIVATE);
        String chwName = prefs.getString("chw_name", "CHW User");
        String chwId = prefs.getString("chw_id", "chw_" + System.currentTimeMillis());

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // If not set, try to get from auth token or user info
        if ("CHW User".equals(chwName)) {
            String userId = prefs.getString("user_id", null);
            if (userId != null) {
                chwId = userId;
            }
        }
<<<<<<< HEAD
        
        return new String[]{chwName, chwId};
=======

        return new String[] { chwName, chwId };
>>>>>>> de27ee7 (Implement user authentication service and session management)
    }

    /**
     * Get selected severity
     */
    private String getSelectedSeverity() {
        int selectedId = radioSeverity.getCheckedRadioButtonId();
<<<<<<< HEAD
        if (selectedId == R.id.radio_mild) return "mild";
        if (selectedId == R.id.radio_moderate) return "moderate";
        if (selectedId == R.id.radio_severe) return "severe";
=======
        if (selectedId == R.id.radio_mild)
            return "mild";
        if (selectedId == R.id.radio_moderate)
            return "moderate";
        if (selectedId == R.id.radio_severe)
            return "severe";
>>>>>>> de27ee7 (Implement user authentication service and session management)
        return null;
    }

    /**
<<<<<<< HEAD
     * Save data to Supabase using model classes
     */
    private void saveToSupabase(String patientName, String gender, String dateOfBirth, Integer patientAge,
                                String chwName, String chwId,
                                Double latitude, Double longitude, String address,
                                String encounterDate, String encounterType,
                                String diseaseType, List<String> symptoms, String severity,
                                String observationDetails, String notes) {
        // Check if fragment is still attached
        if (!isAdded() || getContext() == null) {
            android.util.Log.w("ReportCaseFragment", "Fragment not attached, cannot save to Supabase");
=======
     * Save data to FHIR server using FHIR-compliant resources
     */
    private void saveToFHIR(String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes) {
        // Check if fragment is still attached
        if (!isAdded() || getContext() == null) {
            android.util.Log.w("ReportCaseFragment", "Fragment not attached, cannot save to FHIR");
>>>>>>> de27ee7 (Implement user authentication service and session management)
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(true);
                btnSubmitCase.setText("Submit Case Report");
            }
            return;
        }
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Capture context before entering background thread
        final android.content.Context context = getContext();
        if (context == null) {
            android.util.Log.w("ReportCaseFragment", "Context is null, cannot save");
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(true);
                btnSubmitCase.setText("Submit Case Report");
            }
            return;
        }
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Ensure we're not on main thread for service initialization
        // Use ExecutorService for better thread management
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
<<<<<<< HEAD
        
        executor.execute(() -> {
            try {
                // Initialize service on background thread
                SupabaseService supabaseService = new SupabaseService(context);
                
                // Now save the report (this already uses async Retrofit calls)
                supabaseService.saveDiseaseReport(
                patientName, gender, dateOfBirth, patientAge,
                chwName, chwId,
                latitude, longitude, address,
                encounterDate, encounterType,
                diseaseType, symptoms, severity,
                observationDetails, notes,
                new SupabaseService.SaveCallback() {
                    @Override
                    public void onSuccess(String reportId) {
                        // Use Handler to post to main thread
                        mainHandler.post(() -> {
                            // Check if fragment is still attached
                            if (!isAdded() || getActivity() == null || getContext() == null) {
                                android.util.Log.w("ReportCaseFragment", "Fragment not attached, ignoring success callback");
                                return;
                            }
                            
                            try {
                                android.util.Log.d("ReportCaseFragment", "✅ Data saved to Supabase successfully. Report ID: " + reportId);
                                
                                // Show success message
                                String successMsg = "✅ Report submitted successfully!\nReport ID: " + reportId;
                                Toast.makeText(getContext(), successMsg, Toast.LENGTH_LONG).show();
                                
                                // Re-enable submit button
=======

        executor.execute(() -> {
            try {
                // Initialize FHIR service on background thread
                FHIRService fhirService = new FHIRService(context);

                // Check Toggle State
                boolean isOfflineMode = false;
                if (toggleConnectionMode != null) {
                    isOfflineMode = (toggleConnectionMode.getCheckedButtonId() == R.id.btn_mode_offline);
                }

                FHIRService.SaveCallback callback = new FHIRService.SaveCallback() {
                    @Override
                    public void onSuccess(String reportId, String locationId) {
                        mainHandler.post(() -> {
                            if (!isAdded() || getActivity() == null || getContext() == null) {
                                return;
                            }
                            try {
                                boolean isPending = reportId.startsWith("PENDING-SYNC");
                                String successMsg;
                                if (isPending) {
                                    String reason = "Unknown";
                                    if (reportId.contains(":")) {
                                        reason = reportId.substring(reportId.indexOf(":") + 1);
                                    }

                                    // Custom message based on intention
                                    if (reason.contains("Manual")) {
                                        successMsg = "✅ Report Saved (Offline Mode)\n\nQueued locally as requested.";
                                    } else {
                                        // It was intended to be Online, but failed
                                        successMsg = "✅ Report Saved (Offline Mode)\n\nConnection failed (" + reason
                                                + "), but report is safely queued.";
                                    }
                                } else {
                                    successMsg = "✅ Report Saved & Submitted!\n\nID: " + reportId;
                                }
                                Toast.makeText(getContext(), successMsg, Toast.LENGTH_LONG).show();
>>>>>>> de27ee7 (Implement user authentication service and session management)
                                if (btnSubmitCase != null) {
                                    btnSubmitCase.setEnabled(true);
                                    btnSubmitCase.setText("Submit Case Report");
                                }
<<<<<<< HEAD
                                
                                // Clear form
                                clearForm();
                            } catch (Exception e) {
                                android.util.Log.e("ReportCaseFragment", "Error in success callback", e);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        // Use Handler to post to main thread
                        mainHandler.post(() -> {
                            // Check if fragment is still attached
                            if (!isAdded() || getActivity() == null || getContext() == null) {
                                android.util.Log.w("ReportCaseFragment", "Fragment not attached, ignoring error callback");
                                return;
                            }
                            
                            try {
                                android.util.Log.e("ReportCaseFragment", "❌ Error saving to Supabase: " + error);
                                
                                // Re-enable submit button
                                if (btnSubmitCase != null) {
                                    btnSubmitCase.setEnabled(true);
                                    btnSubmitCase.setText("Submit Case Report");
                                }
                                
                                // Show detailed error message
                                String errorMsg = formatErrorMessage(error);
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                builder.setTitle("❌ Submission Failed")
                                        .setMessage(errorMsg)
                                        .setPositiveButton("OK", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } catch (Exception e) {
                                android.util.Log.e("ReportCaseFragment", "Error in error callback", e);
                                // Fallback: just show a toast
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Error: " + (error != null ? error : "Unknown error"), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("ReportCaseFragment", "Failed to initialize SupabaseService", e);
                mainHandler.post(() -> {
                    if (btnSubmitCase != null) {
                        btnSubmitCase.setEnabled(true);
                        btnSubmitCase.setText("Submit Case Report");
                    }
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to initialize service: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
=======
                                clearForm();

                                // Prepare Map Data
                                refreshMapWithNewLocation(locationId, diseaseType, severity);

                                // Navigate to MapFragment
                                androidx.navigation.fragment.NavHostFragment.findNavController(ReportCaseFragment.this)
                                        .navigate(com.healthtracker.chw.R.id.mapFragment);

                            } catch (Exception e) {
                                android.util.Log.e("ReportCaseFragment", "Error UI", e);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        mainHandler.post(() -> {
                            if (!isAdded() || getActivity() == null || getContext() == null) {
                                return;
                            }
                            android.util.Log.e("ReportCaseFragment", "❌ Error: " + error);
                            if (btnSubmitCase != null) {
                                btnSubmitCase.setEnabled(true);
                                btnSubmitCase.setText("Submit Case Report");
                            }

                            // Show Alert
                            new android.app.AlertDialog.Builder(getContext())
                                    .setTitle("❌ Submission Failed")
                                    .setMessage(formatErrorMessage(error))
                                    .setPositiveButton("OK", null)
                                    .show();
                        });
                    }
                };

                // Branch Logic
                // Branch Logic
                if (isOfflineMode) {
                    fhirService.submitReportOffline(
                            patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId,
                            latitude, longitude, address,
                            encounterDate, encounterType,
                            diseaseType, symptoms, severity,
                            observationDetails, notes,
                            callback);
                } else {
                    // Start Online Mode - FORCE network attempt
                    fhirService.saveDiseaseReport(
                            patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId,
                            latitude, longitude, address,
                            encounterDate, encounterType,
                            diseaseType, symptoms, severity,
                            observationDetails, notes,
                            callback, true); // Force network attempt
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (btnSubmitCase != null) {
                        btnSubmitCase.setEnabled(true);
                    }
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
>>>>>>> de27ee7 (Implement user authentication service and session management)
                });
            }
        });
    }
<<<<<<< HEAD
    
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
    /**
     * Format error message for user-friendly display
     */
    private String formatErrorMessage(String error) {
        if (error == null || error.isEmpty()) {
            return "An unknown error occurred while saving the report. Please try again.";
        }
<<<<<<< HEAD
        
        String lowerError = error.toLowerCase();
        
        // Network errors
        if (lowerError.contains("network") || lowerError.contains("connection") || 
            lowerError.contains("timeout") || lowerError.contains("unreachable")) {
            return "❌ Network Error\n\n" +
                   "Unable to connect to the server. Please check your internet connection and try again.\n\n" +
                   "Details: " + error;
        }
        
        // Authentication errors
        if (lowerError.contains("unauthorized") || lowerError.contains("401") || 
            lowerError.contains("forbidden") || lowerError.contains("403")) {
            return "❌ Authentication Error\n\n" +
                   "Your session has expired or you don't have permission to submit reports.\n\n" +
                   "Please log in again or contact your administrator.\n\n" +
                   "Details: " + error;
        }
        
        // Server errors
        if (lowerError.contains("500") || lowerError.contains("502") || 
            lowerError.contains("503") || lowerError.contains("server error")) {
            return "❌ Server Error\n\n" +
                   "The server is currently experiencing issues. Please try again in a few moments.\n\n" +
                   "Details: " + error;
        }
        
        // Validation errors
        if (lowerError.contains("validation") || lowerError.contains("invalid") || 
            lowerError.contains("required") || lowerError.contains("constraint")) {
            return "❌ Validation Error\n\n" +
                   "Some of the data you entered is invalid. Please check your form and try again.\n\n" +
                   "Details: " + error;
        }
        
        // Database errors
        if (lowerError.contains("database") || lowerError.contains("sql") || 
            lowerError.contains("duplicate") || lowerError.contains("constraint")) {
            return "❌ Database Error\n\n" +
                   "There was an issue saving your data. The report may have already been submitted.\n\n" +
                   "Details: " + error;
        }
        
        // Generic error
        return "❌ Error Saving Report\n\n" +
               "An error occurred while saving your report:\n\n" + error +
               "\n\nPlease check your data and try again. If the problem persists, contact support.";
=======

        String lowerError = error.toLowerCase();

        // Network errors
        if (lowerError.contains("network") || lowerError.contains("connection") ||
                lowerError.contains("timeout") || lowerError.contains("unreachable")) {
            return "❌ Network Error\n\n" +
                    "Unable to connect to the server. Please check your internet connection and try again.\n\n" +
                    "Details: " + error;
        }

        // Authentication errors
        if (lowerError.contains("unauthorized") || lowerError.contains("401") ||
                lowerError.contains("forbidden") || lowerError.contains("403")) {
            return "❌ Authentication Error\n\n" +
                    "Your session has expired or you don't have permission to submit reports.\n\n" +
                    "Please log in again or contact your administrator.\n\n" +
                    "Details: " + error;
        }

        // Server errors
        if (lowerError.contains("500") || lowerError.contains("502") ||
                lowerError.contains("503") || lowerError.contains("server error")) {
            return "❌ Server Error\n\n" +
                    "The server is currently experiencing issues. Please try again in a few moments.\n\n" +
                    "Details: " + error;
        }

        // Validation errors
        if (lowerError.contains("validation") || lowerError.contains("invalid") ||
                lowerError.contains("required") || lowerError.contains("constraint")) {
            return "❌ Validation Error\n\n" +
                    "Some of the data you entered is invalid. Please check your form and try again.\n\n" +
                    "Details: " + error;
        }

        // Database errors
        if (lowerError.contains("database") || lowerError.contains("sql") ||
                lowerError.contains("duplicate") || lowerError.contains("constraint")) {
            return "❌ Database Error\n\n" +
                    "There was an issue saving your data. The report may have already been submitted.\n\n" +
                    "Details: " + error;
        }

        // Generic error
        return "❌ Error Saving Report\n\n" +
                "An error occurred while saving your report:\n\n" + error +
                "\n\nPlease check your data and try again. If the problem persists, contact support.";
>>>>>>> de27ee7 (Implement user authentication service and session management)
    }

    /**
     * Clear form after submission
     */
    private void clearForm() {
        // Patient Information
        etPatientName.setText("");
        etDateOfBirth.setText("");
        etPatientAge.setText("");
        chipGender.clearCheck();
<<<<<<< HEAD
        
        // Encounter Information
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        etEncounterDate.setText(dateTimeFormat.format(new Date()));
        chipEncounterType.clearCheck();
        
        // Disease Report
        etDiseaseType.setText("");
        etDiseaseType.dismissDropDown();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        etReportDate.setText(dateFormat.format(new Date()));
        
=======

        // Encounter Information
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault());
        etEncounterDate.setText(dateTimeFormat.format(new Date()));
        chipEncounterType.clearCheck();

        // Disease Report
        etDiseaseType.setText("");
        etDiseaseType.dismissDropDown();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault());
        etReportDate.setText(dateFormat.format(new Date()));

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Clear symptoms chips
        for (int i = 0; i < chipSymptoms.getChildCount(); i++) {
            View child = chipSymptoms.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
<<<<<<< HEAD
            chip.setChecked(false);
        }
        }
        
        radioSeverity.clearCheck();
        etObservationDetails.setText("");
        
=======
                chip.setChecked(false);
            }
        }

        radioSeverity.clearCheck();
        etObservationDetails.setText("");

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Location
        capturedLatitude = null;
        capturedLongitude = null;
        capturedAddress = null;
        tvLocationStatus.setText("Tap to capture GPS location");
        tvLocationCoordinates.setText("Latitude, Longitude");
        if (tvLocationAddress != null) {
            tvLocationAddress.setText("");
        }
<<<<<<< HEAD
        
=======

>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Additional Notes
        etNotes.setText("");
    }

<<<<<<< HEAD
=======
    /**
     * Refresh map with new location after successful submission
     */
    private void refreshMapWithNewLocation(String locationId, String diseaseType, String severity) {
        if (locationId == null || locationId.isEmpty()) {
            android.util.Log.w("ReportCaseFragment", "Location ID is null, cannot refresh map");
            return;
        }

        // Determine risk level from severity
        String riskLevel = "low";
        if (severity != null) {
            String severityLower = severity.toLowerCase();
            if (severityLower.contains("severe") || severityLower.contains("high")) {
                riskLevel = "high";
            } else if (severityLower.contains("moderate") || severityLower.contains("medium")) {
                riskLevel = "medium";
            }
        }

        // Try to find MapFragment and refresh it
        try {
            if (getActivity() != null) {
                androidx.fragment.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    // Find NavHostFragment first
                    androidx.navigation.fragment.NavHostFragment navHostFragment = (androidx.navigation.fragment.NavHostFragment) fragmentManager
                            .findFragmentById(com.healthtracker.chw.R.id.nav_host_fragment);

                    if (navHostFragment != null) {
                        // Get the child fragment manager from NavHostFragment
                        androidx.fragment.app.FragmentManager childFragmentManager = navHostFragment
                                .getChildFragmentManager();

                        // Try to find MapFragment in the navigation graph
                        com.healthtracker.chw.fragments.MapFragment mapFragment = (com.healthtracker.chw.fragments.MapFragment) childFragmentManager
                                .findFragmentById(com.healthtracker.chw.R.id.mapFragment);

                        // If not found by ID, try to find by iterating through fragments
                        if (mapFragment == null) {
                            for (androidx.fragment.app.Fragment fragment : childFragmentManager.getFragments()) {
                                if (fragment instanceof com.healthtracker.chw.fragments.MapFragment) {
                                    mapFragment = (com.healthtracker.chw.fragments.MapFragment) fragment;
                                    break;
                                }
                            }
                        }

                        if (mapFragment != null && mapFragment.isAdded() && mapFragment.isVisible()) {
                            android.util.Log.d("ReportCaseFragment",
                                    "Found visible MapFragment, adding new marker for Location: " + locationId);
                            mapFragment.addLocationMarker(locationId, diseaseType, riskLevel);
                        } else {
                            // MapFragment exists but not visible - store location ID for later refresh
                            android.util.Log.d("ReportCaseFragment",
                                    "MapFragment not visible, storing location ID for refresh when map is opened");
                            // Store in SharedPreferences so MapFragment can check on resume
                            android.content.SharedPreferences prefs = getContext().getSharedPreferences("map_refresh",
                                    android.content.Context.MODE_PRIVATE);
                            prefs.edit()
                                    .putString("pending_location_id", locationId)
                                    .putString("pending_disease_type", diseaseType)
                                    .putString("pending_risk_level", riskLevel)
                                    .apply();
                        }
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ReportCaseFragment", "Error refreshing map", e);
            // Don't fail the submission if map refresh fails
        }
    }

>>>>>>> de27ee7 (Implement user authentication service and session management)
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gpsService != null) {
            gpsService.stopLocationUpdates();
        }
    }
}
