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
import com.healthtracker.chw.services.FHIRService;
import com.healthtracker.chw.utils.SessionManager;

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
    };

    // UI Components - Patient Information
    private TextInputEditText etPatientName;
    private TextInputEditText etDateOfBirth;
    private TextInputEditText etPatientAge;
    private ChipGroup chipGender;

    // UI Components - Encounter Information
    private TextInputEditText etEncounterDate;
    private ChipGroup chipEncounterType;

    // UI Components - Disease Report
    private android.widget.AutoCompleteTextView etDiseaseType;
    private TextInputEditText etReportDate;
    private ChipGroup chipSymptoms;
    private android.widget.RadioGroup radioSeverity;
    private TextInputEditText etObservationDetails;

    // UI Components - Location
    private View cardLocation;
    private android.widget.TextView tvLocationStatus;
    private android.widget.TextView tvLocationCoordinates;
    private android.widget.TextView tvLocationAddress;

    // UI Components - Additional
    private TextInputEditText etNotes;
    private MaterialButton btnSubmitCase;

    // Data
    private Double capturedLatitude;
    private Double capturedLongitude;
    private String capturedAddress;
    private GPSService gpsService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_case, container, false);

        initializeViews(view);
        setupDiseaseDropdown();
        setupLocationCapture();
        setupSubmitButton();

        if (getArguments() != null && getArguments().getBoolean("is_edit_mode", false)) {
            String reportId = getArguments().getString("report_id");
            if (reportId != null) {
                loadReportData(reportId);
            }
        }

        // Enable options menu for back button handling
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Force back arrow
        if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
            androidx.appcompat.app.ActionBar actionBar = ((androidx.appcompat.app.AppCompatActivity) getActivity())
                    .getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String editingReportId;

    private void loadReportData(String reportId) {
        try {
            if (reportId.startsWith("LOCAL-")) {
                int id = Integer.parseInt(reportId.replace("LOCAL-", ""));
                editingReportId = reportId; // Store full ID

                new Thread(() -> {
                    com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                            .getDatabase(requireContext()).unsyncedReportDao();

                    // Ideally we should have getById in DAO
                    java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> all = dao.getAllReports();
                    com.healthtracker.chw.data.local.UnsyncedReport target = null;
                    for (com.healthtracker.chw.data.local.UnsyncedReport r : all) {
                        if (r.id == id) {
                            target = r;
                            break;
                        }
                    }

                    if (target != null) {
                        final com.healthtracker.chw.data.local.UnsyncedReport data = target;
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> preFillForm(data));
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            android.util.Log.e("ReportCaseFragment", "Error loading report", e);
        }
    }

    private void preFillForm(com.healthtracker.chw.data.local.UnsyncedReport data) {
        if (btnSubmitCase != null)
            btnSubmitCase.setText("Update Case Report");

        etPatientName.setText(data.patientName);
        etDateOfBirth.setText(data.dateOfBirth);
        if (data.patientAge != null)
            etPatientAge.setText(String.valueOf(data.patientAge));

        if ("male".equalsIgnoreCase(data.gender))
            chipGender.check(R.id.chip_male);
        else if ("female".equalsIgnoreCase(data.gender))
            chipGender.check(R.id.chip_female);
        else if ("other".equalsIgnoreCase(data.gender))
            chipGender.check(R.id.chip_other);

        etEncounterDate.setText(data.encounterDate);

        if ("home".equalsIgnoreCase(data.encounterType))
            chipEncounterType.check(R.id.chip_encounter_home);
        else if ("clinic".equalsIgnoreCase(data.encounterType))
            chipEncounterType.check(R.id.chip_encounter_clinic);
        else if ("emergency".equalsIgnoreCase(data.encounterType))
            chipEncounterType.check(R.id.chip_encounter_emergency);

        etDiseaseType.setText(data.diseaseType);
        // We'd need to dismiss dropdown if it shows

        // Severity
        if ("mild".equalsIgnoreCase(data.severity))
            radioSeverity.check(R.id.radio_mild);
        else if ("moderate".equalsIgnoreCase(data.severity))
            radioSeverity.check(R.id.radio_moderate);
        else if ("severe".equalsIgnoreCase(data.severity))
            radioSeverity.check(R.id.radio_severe);

        etObservationDetails.setText(data.observationDetails);
        etNotes.setText(data.notes);

        // Location
        this.capturedLatitude = data.latitude;
        this.capturedLongitude = data.longitude;
        this.capturedAddress = data.address;

        if (capturedLatitude != null && capturedLongitude != null) {
            tvLocationStatus.setText("Location captured (Saved)");
            tvLocationCoordinates.setText(String.format("%.6f, %.6f", capturedLatitude, capturedLongitude));
            if (capturedAddress != null)
                tvLocationAddress.setText(capturedAddress);
        }

        // Symptoms check
        try {
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<java.util.List<String>>() {
            }.getType();
            List<String> symptoms = new com.google.gson.Gson().fromJson(data.symptomsJson, listType);
            if (symptoms != null) {
                // We need to match chip text.
                for (int i = 0; i < chipSymptoms.getChildCount(); i++) {
                    View child = chipSymptoms.getChildAt(i);
                    if (child instanceof Chip) {
                        Chip chip = (Chip) child;
                        if (symptoms.contains(chip.getText().toString())) {
                            chip.setChecked(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void initializeViews(View view) {
        // Toggle Mode

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
        etReportDate = view.findViewById(R.id.et_report_date);
        chipSymptoms = view.findViewById(R.id.chip_symptoms);
        radioSeverity = view.findViewById(R.id.radio_severity);
        etObservationDetails = view.findViewById(R.id.et_observation_details);

        // Location
        cardLocation = view.findViewById(R.id.card_location);
        tvLocationStatus = view.findViewById(R.id.tv_location_status);
        tvLocationCoordinates = view.findViewById(R.id.tv_location_coordinates);
        tvLocationAddress = view.findViewById(R.id.tv_location_address);

        // Additional
        etNotes = view.findViewById(R.id.et_notes);
        btnSubmitCase = view.findViewById(R.id.btn_submit_case);

        gpsService = new GPSService(requireContext());

        // Set current date/time for encounter and report date
        setupDateFields();

        // Setup chip selection listeners for visual feedback
        setupChipSelections();
    }

    /**
     * Setup chip selection listeners to show visual feedback
     */
    private void setupChipSelections() {
        // Gender chips - already handled by ChipGroup with singleSelection
        chipGender.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Choice style
        });

        // Encounter type chips - already handled by ChipGroup with singleSelection
        chipEncounterType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Choice style
        });

        // Symptoms chips - multiple selection with Filter style
        chipSymptoms.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Visual feedback is automatic with Filter style
            // Selected chips will show checked state
        });
    }

    private void setupDateFields() {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                java.util.Locale.getDefault());
        java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault());

        // Set current date for report date
        etReportDate.setText(dateFormat.format(new Date()));
        // Report date cannot be in the future
        etReportDate.setOnClickListener(v -> showDatePicker(etReportDate, dateFormat, System.currentTimeMillis()));

        // Set current date/time for encounter date
        etEncounterDate.setText(dateTimeFormat.format(new Date()));
        // Encounter date is locked to "now" - read only
        etEncounterDate.setFocusable(false);
        etEncounterDate.setClickable(false);
        etEncounterDate.setLongClickable(false);

        // Setup date of birth picker
        // Birthday cannot be in the future
        etDateOfBirth.setOnClickListener(v -> showDatePicker(etDateOfBirth, dateFormat, System.currentTimeMillis()));

        // Add listener to auto-calculate age when DOB changes
        etDateOfBirth.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateAndSetAge(s.toString());
            }
        });
    }

    private void calculateAndSetAge(String dateOfBirthStr) {
        if (TextUtils.isEmpty(dateOfBirthStr))
            return;

        try {
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                    java.util.Locale.getDefault());
            Date dob = dateFormat.parse(dateOfBirthStr);
            if (dob != null) {
                java.util.Calendar dobCal = java.util.Calendar.getInstance();
                dobCal.setTime(dob);

                java.util.Calendar nowCal = java.util.Calendar.getInstance();

                int age = nowCal.get(java.util.Calendar.YEAR) - dobCal.get(java.util.Calendar.YEAR);
                if (nowCal.get(java.util.Calendar.DAY_OF_YEAR) < dobCal.get(java.util.Calendar.DAY_OF_YEAR)) {
                    age--;
                }

                if (age < 0)
                    age = 0; // Handle edge case if future date somehow slipped through

                etPatientAge.setText(String.valueOf(age));
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
    }

    private void showDatePicker(TextInputEditText editText, java.text.SimpleDateFormat format, long maxDate) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try {
            if (!TextUtils.isEmpty(editText.getText())) {
                calendar.setTime(format.parse(editText.getText().toString()));
            }
        } catch (Exception e) {
            // Use current date
        }

        android.app.DatePickerDialog dialog = new android.app.DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(format.format(calendar.getTime()));
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH));

        if (maxDate > 0) {
            dialog.getDatePicker().setMaxDate(maxDate);
        }

        dialog.show();
    }

    private void showDateTimePicker(TextInputEditText editText, java.text.SimpleDateFormat format, long maxDate) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try {
            if (!TextUtils.isEmpty(editText.getText())) {
                calendar.setTime(format.parse(editText.getText().toString()));
            }
        } catch (Exception e) {
            // Use current date/time
        }

        android.app.DatePickerDialog dateDialog = new android.app.DatePickerDialog(requireContext(),
                (dateView, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    new android.app.TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(java.util.Calendar.MINUTE, minute);

                        // Validate if selected time is in future relative to now (if date is today)
                        if (maxDate > 0 && calendar.getTimeInMillis() > maxDate) {
                            Toast.makeText(requireContext(), "Future time not allowed", Toast.LENGTH_SHORT).show();
                            // Reset to now or just don't set? prefer showing user feedback
                            // For now, let's just clamp it or leave it, but best to warn
                            calendar.setTimeInMillis(maxDate); // Clamp to max
                        }

                        editText.setText(format.format(calendar.getTime()));
                    }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true)
                            .show();

                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH));

        if (maxDate > 0) {
            dateDialog.getDatePicker().setMaxDate(maxDate);
        }

        dateDialog.show();
    }

    /**
     * Setup disease dropdown (FR001)
     * Creates an AutoCompleteTextView dropdown with disease types
     */
    private void setupDiseaseDropdown() {
        // Create adapter for dropdown
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                DISEASE_TYPES);

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
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
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
                            tvLocationStatus.setText("Location captured");
                            tvLocationCoordinates.setText(String.format("%.6f, %.6f", latitude, longitude));
                            if (tvLocationAddress != null && address != null) {
                                tvLocationAddress.setText(address);
                            }
                        }
                    });
                }
            }

            @Override
            public void onLocationError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (isAdded() && getContext() != null) {
                            if (tvLocationStatus != null) {
                                tvLocationStatus.setText("Location capture failed");
                            }
                            if (tvLocationCoordinates != null) {
                                tvLocationCoordinates.setText(error);
                            }
                            Toast.makeText(getContext(), "Location error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
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

            // Get Encounter Information
            String encounterDate = etEncounterDate.getText().toString().trim();
            String encounterType = getSelectedEncounterType();

            // Get Disease Report Information
            String diseaseType = etDiseaseType.getText().toString().trim();
            String reportDate = etReportDate.getText().toString().trim();
            String symptoms = getSelectedSymptoms();
            String severity = getSelectedSeverity();
            String observationDetails = etObservationDetails.getText().toString().trim();

            // Get Additional Notes
            String notes = etNotes.getText().toString().trim();

            // Get CHW information
            String[] chwInfo = getCHWInfo();
            String chwName = chwInfo[0];
            String chwId = chwInfo[1];
            String chwEmail = chwInfo[2];

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

            if (diseaseType == null || diseaseType.isEmpty()) {
                Toast.makeText(requireContext(), "Disease type is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (capturedLatitude == null || capturedLongitude == null) {
                Toast.makeText(requireContext(), "GPS location is required. Please capture location first.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            // Disable submit button to prevent multiple submissions
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(false);
                btnSubmitCase.setText("Submitting...");
            }

            // Show loading message
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Saving report to FHIR server...", Toast.LENGTH_SHORT).show();
            }

            saveToFHIR(patientName, gender, dateOfBirth, patientAge, chwName, chwId, chwEmail,
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
        if (selectedId == R.id.chip_male)
            return "male";
        if (selectedId == R.id.chip_female)
            return "female";
        if (selectedId == R.id.chip_other)
            return "other";
        return null;
    }

    /**
     * Get selected encounter type
     */
    private String getSelectedEncounterType() {
        int selectedId = chipEncounterType.getCheckedChipId();
        if (selectedId == R.id.chip_encounter_home)
            return "home";
        if (selectedId == R.id.chip_encounter_clinic)
            return "clinic";
        if (selectedId == R.id.chip_encounter_emergency)
            return "emergency";
        return "home"; // Default
    }

    /**
     * Get selected symptoms as List
     */
    private List<String> getSelectedSymptomsList() {
        List<String> symptoms = new ArrayList<>();
        for (int i = 0; i < chipSymptoms.getChildCount(); i++) {
            View child = chipSymptoms.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    symptoms.add(chip.getText().toString());
                }
            }
        }
        return symptoms;
    }

    /**
     * Get selected symptoms as String
     */
    private String getSelectedSymptoms() {
        List<String> symptoms = getSelectedSymptomsList();
        return symptoms.isEmpty() ? "" : String.join(", ", symptoms);
    }

    /**
     * Get CHW information from SharedPreferences or use defaults
     */
    private String[] getCHWInfo() {
        if (sessionManager == null) {
            sessionManager = new SessionManager(requireContext());
        }

        String chwName = sessionManager.getUserName();
        if (chwName == null)
            chwName = "CHW User";

        String chwId = sessionManager.getUserId();
        if (chwId == null)
            chwId = "chw_" + System.currentTimeMillis();

        String chwEmail = sessionManager.getUserEmail();
        if (chwEmail == null)
            chwEmail = "unknown@example.com";

        return new String[] { chwName, chwId, chwEmail };
    }

    /**
     * Get selected severity
     */
    private String getSelectedSeverity() {
        int selectedId = radioSeverity.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_mild)
            return "mild";
        if (selectedId == R.id.radio_moderate)
            return "moderate";
        if (selectedId == R.id.radio_severe)
            return "severe";
        return null;
    }

    /**
     * Save data to FHIR server using FHIR-compliant resources
     */
    private void saveToFHIR(String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes) {
        // Check if fragment is still attached
        if (!isAdded() || getContext() == null) {
            android.util.Log.w("ReportCaseFragment", "Fragment not attached, cannot save to FHIR");
            if (btnSubmitCase != null) {
                btnSubmitCase.setEnabled(true);
                btnSubmitCase.setText("Submit Case Report");
            }
            return;
        }

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

        // Ensure we're not on main thread for service initialization
        // Use ExecutorService for better thread management
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Initialize FHIR service on background thread
                FHIRService fhirService = new FHIRService(context);

                FHIRService.SaveCallback callback = new FHIRService.SaveCallback() {
                    @Override
                    public void onSuccess(String reportId, String locationId) {
                        mainHandler.post(() -> {
                            if (!isAdded() || getActivity() == null || getContext() == null) {
                                return;
                            }
                            try {
                                boolean isPending = reportId.startsWith("PENDING-SYNC");
                                boolean isUpdate = reportId.startsWith("PENDING-UPDATE");
                                String successMsg;
                                if (isUpdate) {
                                    successMsg = "✅ Report Updated Successfully (Offline Mode)";
                                } else if (isPending) {
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
                                if (btnSubmitCase != null) {
                                    btnSubmitCase.setEnabled(true);
                                    btnSubmitCase.setText("Submit Case Report");
                                }
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
                if (editingReportId != null && editingReportId.startsWith("LOCAL-")) {
                    int id = Integer.parseInt(editingReportId.replace("LOCAL-", ""));
                    fhirService.updateReportOffline(
                            id,
                            patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId, chwEmail,
                            latitude, longitude, address,
                            encounterDate, encounterType,
                            diseaseType, symptoms, severity,
                            observationDetails, notes,
                            callback);
                } else {
                    // Standard Submission - Try Online, Fallback handled by service/callback
                    fhirService.saveDiseaseReport(
                            patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId, chwEmail,
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
                });
            }
        });
    }

    /**
     * Format error message for user-friendly display
     */
    private String formatErrorMessage(String error) {
        if (error == null || error.isEmpty()) {
            return "An unknown error occurred while saving the report. Please try again.";
        }

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

        // Clear symptoms chips
        for (int i = 0; i < chipSymptoms.getChildCount(); i++) {
            View child = chipSymptoms.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setChecked(false);
            }
        }

        radioSeverity.clearCheck();
        etObservationDetails.setText("");

        // Location
        capturedLatitude = null;
        capturedLongitude = null;
        capturedAddress = null;
        tvLocationStatus.setText("Tap to capture GPS location");
        tvLocationCoordinates.setText("Latitude, Longitude");
        if (tvLocationAddress != null) {
            tvLocationAddress.setText("");
        }

        // Additional Notes
        etNotes.setText("");
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gpsService != null) {
            gpsService.stopLocationUpdates();
        }
    }
}
