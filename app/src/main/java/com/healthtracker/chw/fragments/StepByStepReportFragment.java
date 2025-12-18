package com.healthtracker.chw.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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
import com.healthtracker.chw.services.GPSService;
<<<<<<< HEAD
import com.healthtracker.chw.services.SupabaseService;
=======
import com.healthtracker.chw.services.FHIRService;
>>>>>>> de27ee7 (Implement user authentication service and session management)

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Step-by-step disease reporting form
 * Step 1: Create Patient -> /fhir/Patient
 * Step 2: Capture Location -> /fhir/Location
 * Step 3: Report Case -> /fhir/Observation
 */
public class StepByStepReportFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1002;
    private static final String[] DISEASE_TYPES = {
        "Malaria", "Cholera", "Dengue", "Ebola", "COVID-19", 
        "Tuberculosis", "Measles", "Yellow Fever", "Meningitis", "Other"
    };

    private int currentStep = 1;
    
    // Step 1 Views
    private TextInputEditText etStepPatientAge;
    private ChipGroup chipStepGender;
    
    // Step 2 Views
    private View cardStepLocation;
    private android.widget.TextView tvStepLocationStatus;
    private android.widget.TextView tvStepLocationCoords;
    
    // Step 3 Views
    private TextInputEditText etStepDiseaseType;
    private ChipGroup chipStepSymptoms;
    private RadioGroup radioStepSeverity;
    private TextInputEditText etStepNotes;
    
    // Step Cards
    private View step1Card, step2Card, step3Card;
    private View step1Indicator, step2Indicator, step3Indicator;
    
    // Data
    private Integer patientAge;
    private String patientGender;
    private String createdPatientId;
    private Double capturedLatitude;
    private Double capturedLongitude;
    private String capturedAddress;
    private String createdLocationId;
    
    private GPSService gpsService;
<<<<<<< HEAD
    private SupabaseService supabaseService;
=======
    private FHIRService fhirService;
>>>>>>> de27ee7 (Implement user authentication service and session management)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_report, container, false);
        
        initializeViews(view);
        setupStep1();
        setupStep2();
        setupStep3();
        showStep(1);
        
        gpsService = new GPSService(requireContext());
<<<<<<< HEAD
        supabaseService = new SupabaseService(requireContext());
=======
        fhirService = new FHIRService(requireContext());
>>>>>>> de27ee7 (Implement user authentication service and session management)
        
        return view;
    }

    private void initializeViews(View view) {
        // Step indicators
        step1Indicator = view.findViewById(R.id.step1_indicator);
        step2Indicator = view.findViewById(R.id.step2_indicator);
        step3Indicator = view.findViewById(R.id.step3_indicator);
        
        // Step cards
        step1Card = view.findViewById(R.id.step1_card);
        step2Card = view.findViewById(R.id.step2_card);
        step3Card = view.findViewById(R.id.step3_card);
        
        // Step 1
        etStepPatientAge = view.findViewById(R.id.et_step_patient_age);
        chipStepGender = view.findViewById(R.id.chip_step_gender);
        
        // Step 2
        cardStepLocation = view.findViewById(R.id.card_step_location);
        tvStepLocationStatus = view.findViewById(R.id.tv_step_location_status);
        tvStepLocationCoords = view.findViewById(R.id.tv_step_location_coords);
        
        // Step 3
        etStepDiseaseType = view.findViewById(R.id.et_step_disease_type);
        chipStepSymptoms = view.findViewById(R.id.chip_step_symptoms);
        radioStepSeverity = view.findViewById(R.id.radio_step_severity);
        etStepNotes = view.findViewById(R.id.et_step_notes);
    }

    private void setupStep1() {
        MaterialButton btnNext = step1Card.findViewById(R.id.btn_step1_next);
        btnNext.setOnClickListener(v -> {
            if (validateStep1()) {
                submitPatient();
            }
        });
    }

    private void setupStep2() {
        cardStepLocation.setOnClickListener(v -> captureGPSLocation());
        
        MaterialButton btnBack = step2Card.findViewById(R.id.btn_step2_back);
        btnBack.setOnClickListener(v -> showStep(1));
        
        MaterialButton btnNext = step2Card.findViewById(R.id.btn_step2_next);
        btnNext.setOnClickListener(v -> {
            if (validateStep2()) {
                submitLocation();
            }
        });
    }

    private void setupStep3() {
        MaterialButton btnBack = step3Card.findViewById(R.id.btn_step3_back);
        btnBack.setOnClickListener(v -> showStep(2));
        
        MaterialButton btnSubmit = step3Card.findViewById(R.id.btn_step3_submit);
        btnSubmit.setOnClickListener(v -> {
            if (validateStep3()) {
                submitCaseReport();
            }
        });
    }

    private void showStep(int step) {
        currentStep = step;
        
        // Hide all cards
        step1Card.setVisibility(View.GONE);
        step2Card.setVisibility(View.GONE);
        step3Card.setVisibility(View.GONE);
        
        // Show current step
        if (step == 1) {
            step1Card.setVisibility(View.VISIBLE);
            updateStepIndicator(1, true);
        } else if (step == 2) {
            step2Card.setVisibility(View.VISIBLE);
            updateStepIndicator(2, true);
        } else if (step == 3) {
            step3Card.setVisibility(View.VISIBLE);
            updateStepIndicator(3, true);
        }
    }

    private void updateStepIndicator(int step, boolean active) {
        // Update indicator colors based on step
        // Implementation for visual feedback
    }

    private boolean validateStep1() {
        try {
            String ageStr = etStepPatientAge.getText().toString().trim();
            if (!TextUtils.isEmpty(ageStr)) {
                patientAge = Integer.parseInt(ageStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid age", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        int selectedId = chipStepGender.getCheckedChipId();
        if (selectedId == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (selectedId == R.id.chip_step_male) patientGender = "male";
        else if (selectedId == R.id.chip_step_female) patientGender = "female";
        else if (selectedId == R.id.chip_step_other) patientGender = "other";
        
        return true;
    }

    private boolean validateStep2() {
        if (capturedLatitude == null || capturedLongitude == null) {
            Toast.makeText(requireContext(), "Please capture location first", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateStep3() {
        if (TextUtils.isEmpty(etStepDiseaseType.getText())) {
            etStepDiseaseType.setError("Disease type is required");
            return false;
        }
        
        if (radioStepSeverity.getCheckedRadioButtonId() == View.NO_ID) {
            Toast.makeText(requireContext(), "Please select severity", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void submitPatient() {
        // For step-by-step flow, we'll create patient when submitting the full report
        // Just move to next step
        createdPatientId = "patient_" + System.currentTimeMillis();
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Patient information saved", Toast.LENGTH_SHORT).show();
            showStep(2);
        });
    }

    private void captureGPSLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        
        tvStepLocationStatus.setText("Capturing location...");
        tvStepLocationCoords.setText("Please wait...");
        
        gpsService.captureLocation(new GPSService.LocationCallback() {
            @Override
            public void onLocationCaptured(double latitude, double longitude, String address, long timestamp) {
                capturedLatitude = latitude;
                capturedLongitude = longitude;
                capturedAddress = address;
                
                requireActivity().runOnUiThread(() -> {
                    tvStepLocationStatus.setText("Location captured");
                    tvStepLocationCoords.setText(String.format("%.6f, %.6f", latitude, longitude));
                });
            }
            
            @Override
            public void onLocationError(String error) {
                requireActivity().runOnUiThread(() -> {
                    tvStepLocationStatus.setText("Location capture failed");
                    tvStepLocationCoords.setText(error);
                    Toast.makeText(requireContext(), "Location error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void submitLocation() {
        // Location will be saved when submitting the full report
        // Just move to next step
        createdLocationId = "location_" + System.currentTimeMillis();
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Location saved", Toast.LENGTH_SHORT).show();
            showStep(3);
        });
    }

    private void submitCaseReport() {
        String diseaseType = etStepDiseaseType.getText().toString().trim();
        String symptoms = getSelectedSymptoms();
        String severity = getSelectedSeverity();
        String notes = etStepNotes.getText().toString().trim();
        
        // Convert symptoms string to list
        List<String> symptomsList = new ArrayList<>();
        if (symptoms != null && !symptoms.isEmpty()) {
            String[] symptomArray = symptoms.split(",");
            for (String symptom : symptomArray) {
                symptomsList.add(symptom.trim());
            }
        }
        
        // Get CHW info
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE);
        String chwName = prefs.getString("chw_name", "CHW User");
        String chwId = prefs.getString("chw_id", "chw_" + System.currentTimeMillis());
        
        // Get current date/time
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        String encounterDate = dateTimeFormat.format(new Date());
        String dateOfBirth = dateFormat.format(new Date()); // Approximate
        
<<<<<<< HEAD
        // Save to Supabase
        supabaseService.saveDiseaseReport(
=======
        // Save to FHIR
        fhirService.saveDiseaseReport(
>>>>>>> de27ee7 (Implement user authentication service and session management)
            "Patient", // Patient name
            patientGender != null ? patientGender : "unknown",
            dateOfBirth,
            patientAge,
            chwName,
            chwId,
            capturedLatitude,
            capturedLongitude,
            capturedAddress,
            encounterDate,
            "home", // Encounter type
            diseaseType,
            symptomsList,
            severity,
            notes, // Observation details
            notes, // Additional notes
<<<<<<< HEAD
            new SupabaseService.SaveCallback() {
                @Override
                public void onSuccess(String reportId, String locationId) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Case report submitted successfully!", Toast.LENGTH_SHORT).show();
=======
            new FHIRService.SaveCallback() {
                @Override
                public void onSuccess(String reportId, String locationId) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Case report submitted successfully!", Toast.LENGTH_SHORT).show();
                        
                        // Refresh map with new location if available
                        if (locationId != null && !locationId.isEmpty()) {
                            refreshMapWithNewLocation(locationId, diseaseType, severity);
                        }
                        
>>>>>>> de27ee7 (Implement user authentication service and session management)
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    });
                }
                
                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error submitting report: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        );
    }
    
    private void saveToLocalDatabase(String diseaseType, String symptoms, String severity, String notes) {
<<<<<<< HEAD
        // This method is no longer needed - data is saved directly to Supabase
=======
        // This method is no longer needed - data is saved directly to FHIR
>>>>>>> de27ee7 (Implement user authentication service and session management)
        // Keeping for compatibility but it does nothing
    }

    private String getSelectedSymptoms() {
        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < chipStepSymptoms.getChildCount(); i++) {
            Chip chip = (Chip) chipStepSymptoms.getChildAt(i);
            if (chip.isChecked()) {
                symptoms.add(chip.getText().toString());
            }
        }
        return String.join(", ", symptoms);
    }

    private String getSelectedSeverity() {
        int selectedId = radioStepSeverity.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_step_mild) return "mild";
        if (selectedId == R.id.radio_step_moderate) return "moderate";
        if (selectedId == R.id.radio_step_severe) return "severe";
        return null;
    }

<<<<<<< HEAD
=======
    /**
     * Refresh map with new location after successful submission
     */
    private void refreshMapWithNewLocation(String locationId, String diseaseType, String severity) {
        if (locationId == null || locationId.isEmpty()) {
            android.util.Log.w("StepByStepReportFragment", "Location ID is null, cannot refresh map");
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
                    androidx.navigation.fragment.NavHostFragment navHostFragment = 
                        (androidx.navigation.fragment.NavHostFragment) fragmentManager.findFragmentById(com.healthtracker.chw.R.id.nav_host_fragment);
                    
                    if (navHostFragment != null) {
                        // Get the child fragment manager from NavHostFragment
                        androidx.fragment.app.FragmentManager childFragmentManager = navHostFragment.getChildFragmentManager();
                        
                        // Try to find MapFragment in the navigation graph
                        com.healthtracker.chw.fragments.MapFragment mapFragment = 
                            (com.healthtracker.chw.fragments.MapFragment) childFragmentManager.findFragmentById(com.healthtracker.chw.R.id.mapFragment);
                        
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
                            android.util.Log.d("StepByStepReportFragment", "Found visible MapFragment, adding new marker for Location: " + locationId);
                            mapFragment.addLocationMarker(locationId, diseaseType, riskLevel);
                        } else {
                            // MapFragment exists but not visible - store location ID for later refresh
                            android.util.Log.d("StepByStepReportFragment", "MapFragment not visible, storing location ID for refresh when map is opened");
                            // Store in SharedPreferences so MapFragment can check on resume
                            android.content.SharedPreferences prefs = getContext().getSharedPreferences("map_refresh", android.content.Context.MODE_PRIVATE);
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
            android.util.Log.e("StepByStepReportFragment", "Error refreshing map", e);
            // Don't fail the submission if map refresh fails
        }
    }
    
>>>>>>> de27ee7 (Implement user authentication service and session management)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureGPSLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

