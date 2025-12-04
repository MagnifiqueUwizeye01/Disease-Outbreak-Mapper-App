package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.healthtracker.chw.R;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.Patient;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.RiskAssessment;
import com.healthtracker.chw.services.SupabaseService;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CaseDetailsFragment extends Fragment {

    private static final String ARG_CASE_ID = "case_id";
    
    private SupabaseService supabaseService;
    
    // Views
    private TextView tvReportId;
    private TextView tvDiseaseType;
    private TextView tvReportDate;
    private Chip chipStatus;
    private TextView tvPatientId;
    private TextView tvPatientDemographics;
    private TextView tvDateOfBirth;
    private TextView tvGpsCoordinates;
    private TextView tvAddress;
    private TextView tvEncounterDate;
    private TextView tvEncounterType;
    private Chip chipRiskLevel;
    private TextView tvObservationDetails;
    private TextView tvObservationTimestamp;

    public static CaseDetailsFragment newInstance(String reportId) {
        CaseDetailsFragment fragment = new CaseDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CASE_ID, reportId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case_details, container, false);
        
        // Initialize Supabase service
        supabaseService = new SupabaseService(requireContext());
        
        // Initialize views
        initializeViews(view);
        
        // Load case data
        loadCaseData();
        
        return view;
    }

    private void initializeViews(View view) {
        tvReportId = view.findViewById(R.id.tv_report_id);
        tvDiseaseType = view.findViewById(R.id.tv_disease_type);
        tvReportDate = view.findViewById(R.id.tv_report_date);
        chipStatus = view.findViewById(R.id.chip_status);
        tvPatientId = view.findViewById(R.id.tv_patient_id);
        tvPatientDemographics = view.findViewById(R.id.tv_patient_demographics);
        tvDateOfBirth = view.findViewById(R.id.tv_date_of_birth);
        tvGpsCoordinates = view.findViewById(R.id.tv_gps_coordinates);
        tvAddress = view.findViewById(R.id.tv_address);
        tvEncounterDate = view.findViewById(R.id.tv_encounter_date);
        tvEncounterType = view.findViewById(R.id.tv_encounter_type);
        chipRiskLevel = view.findViewById(R.id.chip_risk_level);
        tvObservationDetails = view.findViewById(R.id.tv_observation_details);
        tvObservationTimestamp = view.findViewById(R.id.tv_observation_timestamp);
    }

    private void loadCaseData() {
        Bundle args = getArguments();
        String reportId = null;
        
        if (args != null && args.containsKey(ARG_CASE_ID)) {
            reportId = args.getString(ARG_CASE_ID);
        } else if (args != null) {
            // Try alternative key
            reportId = args.getString("caseId");
            if (reportId == null) {
                reportId = args.getString("reportId");
            }
        }
        
        if (reportId == null || reportId.isEmpty()) {
            showError("Case ID not provided");
            return;
        }
        
        loadCaseById(reportId);
    }

    private void loadCaseById(String reportId) {
        supabaseService.getDiseaseReportById(reportId, new SupabaseService.ReportCallback() {
            @Override
            public void onSuccess(DiseaseReport report) {
                requireActivity().runOnUiThread(() -> {
                    displayCaseData(report);
                });
            }
            
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    showError("Case not found: " + error);
                });
            }
        });
    }

    private void displayCaseData(DiseaseReport report) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        
        // Get related data
        Encounter encounter = report.getEncounter();
        Patient patient = encounter != null ? encounter.getPatient() : null;
        GPSLocation location = encounter != null ? encounter.getGpsLocation() : null;
        RiskAssessment riskAssessment = report.getRiskAssessment();
        
        // Report Information
        if (tvReportId != null) {
            tvReportId.setText(report.getReportId() != null ? report.getReportId() : "N/A");
        }
        
        if (tvDiseaseType != null) {
            tvDiseaseType.setText(report.getDiseaseType() != null ? report.getDiseaseType() : "Unknown");
        }
        
        if (tvReportDate != null) {
            tvReportDate.setText(report.getReportDate() != null ? dateFormat.format(report.getReportDate()) : "N/A");
        }
        
        if (chipStatus != null) {
            chipStatus.setText(report.getStatus() != null ? report.getStatus() : "Pending");
        }
        
        // Patient Information
        if (tvPatientId != null) {
            String patientId = patient != null && patient.getPatientId() != null ? patient.getPatientId() : "N/A";
            tvPatientId.setText(patientId);
        }
        
        if (tvPatientDemographics != null) {
            String name = patient != null && patient.getName() != null ? patient.getName() : "Unknown";
            String gender = patient != null && patient.getGender() != null ? patient.getGender() : "Unknown";
            tvPatientDemographics.setText(String.format(Locale.getDefault(), "%s, %s", name, gender));
        }
        
        if (tvDateOfBirth != null) {
            if (patient != null && patient.getDateOfBirth() != null) {
                tvDateOfBirth.setText(dateFormat.format(patient.getDateOfBirth()));
            } else {
                tvDateOfBirth.setText("N/A");
            }
        }
        
        // Location & Encounter
        if (tvGpsCoordinates != null) {
            if (location != null && location.getLatitude() != null && location.getLongitude() != null) {
                tvGpsCoordinates.setText(String.format(Locale.getDefault(), "%.4f, %.4f", 
                    location.getLatitude(), location.getLongitude()));
            } else {
                tvGpsCoordinates.setText("N/A");
            }
        }
        
        if (tvAddress != null) {
            String address = location != null && location.getAddress() != null ? location.getAddress() : "N/A";
            tvAddress.setText(address);
        }
        
        if (tvEncounterDate != null) {
            if (encounter != null && encounter.getEncounterDate() != null) {
                tvEncounterDate.setText(dateTimeFormat.format(encounter.getEncounterDate()));
            } else {
                tvEncounterDate.setText("N/A");
            }
        }
        
        if (tvEncounterType != null) {
            String encounterType = encounter != null && encounter.getEncounterType() != null ? 
                encounter.getEncounterType() : "Home Visit";
            tvEncounterType.setText(encounterType);
        }
        
        // Risk Assessment
        if (chipRiskLevel != null) {
            String riskLevel = "LOW";
            int colorRes = R.color.medical_green_primary;
            
            if (riskAssessment != null && riskAssessment.getLevel() != null) {
                String level = riskAssessment.getLevel().toLowerCase();
                if (level.contains("high") || level.contains("severe")) {
                    riskLevel = "HIGH";
                    colorRes = R.color.error;
                } else if (level.contains("medium") || level.contains("moderate")) {
                    riskLevel = "MEDIUM";
                    colorRes = R.color.medical_teal_primary;
                } else {
                    riskLevel = "LOW";
                    colorRes = R.color.medical_green_primary;
                }
            }
            
            chipRiskLevel.setText(riskLevel);
            chipRiskLevel.setChipBackgroundColorResource(colorRes);
        }
        
        // Clinical Observations
        if (tvObservationDetails != null) {
            String observations = "No observations recorded";
            if (encounter != null && encounter.getObservations() != null && !encounter.getObservations().isEmpty()) {
                StringBuilder obsText = new StringBuilder();
                for (com.healthtracker.chw.models.Observation obs : encounter.getObservations()) {
                    if (obs.getDetails() != null && !obs.getDetails().isEmpty()) {
                        if (obsText.length() > 0) obsText.append("\n");
                        obsText.append(obs.getDetails());
                    }
                }
                if (obsText.length() > 0) {
                    observations = obsText.toString();
                }
            }
            tvObservationDetails.setText(observations);
        }
        
        if (tvObservationTimestamp != null) {
            if (encounter != null && encounter.getObservations() != null && !encounter.getObservations().isEmpty()) {
                com.healthtracker.chw.models.Observation firstObs = encounter.getObservations().get(0);
                if (firstObs.getTimestamp() != null) {
                    tvObservationTimestamp.setText(dateTimeFormat.format(firstObs.getTimestamp()));
                } else {
                    tvObservationTimestamp.setText("N/A");
                }
            } else {
                tvObservationTimestamp.setText("N/A");
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
