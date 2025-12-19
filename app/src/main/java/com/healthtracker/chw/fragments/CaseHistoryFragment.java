package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.healthtracker.chw.R;
import com.healthtracker.chw.adapters.CaseHistoryAdapter;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIRObservation;
import com.healthtracker.chw.services.FHIRService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaseHistoryFragment extends Fragment {

    private RecyclerView recyclerCaseHistory;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private CaseHistoryAdapter adapter;
    private FHIRService fhirService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_case_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerCaseHistory = view.findViewById(R.id.recycler_case_history);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);

        // Initialize services
        fhirService = new FHIRService(requireContext());

        // Setup RecyclerView
        adapter = new CaseHistoryAdapter(new CaseHistoryAdapter.OnCaseActionListener() {
            @Override
            public void onCaseClick(String reportId) {
                // Navigate to case details
                NavController navController = Navigation.findNavController(view);
                Bundle args = new Bundle();
                args.putString("case_id", reportId);
                navController.navigate(R.id.action_case_history_to_details, args);
            }

            @Override
            public void onEditClick(String reportId) {
                handleEditReport(reportId);
            }

            @Override
            public void onDeleteClick(String reportId) {
                handleDeleteReport(reportId);
            }
        });

        recyclerCaseHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCaseHistory.setAdapter(adapter);

        // Load case history
        loadCaseHistory();
    }

    private void handleEditReport(String reportId) {
        if (reportId == null)
            return;

        if (reportId.startsWith("LOCAL-")) {
            // Navigate to report form in edit mode
            NavController navController = Navigation.findNavController(requireView());
            Bundle args = new Bundle();
            args.putString("report_id", reportId);
            args.putBoolean("is_edit_mode", true);
            navController.navigate(R.id.action_case_history_to_report_case, args);
        } else {
            android.widget.Toast.makeText(getContext(), "Only offline/pending reports can be edited",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteReport(String reportId) {
        if (reportId == null)
            return;

        if (reportId.startsWith("LOCAL-")) {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Report")
                    .setMessage("Are you sure you want to delete this pending report?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteLocalReport(reportId);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            android.widget.Toast.makeText(getContext(), "Cannot delete synced reports",
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteLocalReport(String reportId) {
        try {
            int id = Integer.parseInt(reportId.replace("LOCAL-", ""));
            new Thread(() -> {
                com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                        .getDatabase(requireContext()).unsyncedReportDao();

                List<com.healthtracker.chw.data.local.UnsyncedReport> all = dao.getAllReports();
                for (com.healthtracker.chw.data.local.UnsyncedReport r : all) {
                    if (r.id == id) {
                        dao.delete(r);
                        break;
                    }
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        android.widget.Toast.makeText(getContext(), "Report deleted", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        loadCaseHistory();
                    });
                }
            }).start();
        } catch (Exception e) {
            android.util.Log.e("CaseHistoryFragment", "Error deleting", e);
        }
    }

    private void loadCaseHistory() {
        // Show loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (recyclerCaseHistory != null) {
            recyclerCaseHistory.setVisibility(View.GONE);
        }
        if (emptyState != null) {
            emptyState.setVisibility(View.GONE);
        }

        // 1. Fetch Online Data
        fhirService.getAllObservations(new FHIRService.ObservationsCallback() {
            @Override
            public void onSuccess(FHIRBundle bundle) {
                processMergedHistory(bundle);
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("CaseHistoryFragment", "Error loading online history: " + error);
                // Try to load at least local history
                processMergedHistory(null);
            }
        });
    }

    private void processMergedHistory(FHIRBundle onlineBundle) {
        new Thread(() -> {
            try {
                // 1. Convert Online Data
                List<DiseaseReport> reports = convertFHIRBundleToDiseaseReports(onlineBundle);

                // Get current CHW ID and Email for filtering
                com.healthtracker.chw.utils.SessionManager sessionManager = new com.healthtracker.chw.utils.SessionManager(
                        requireContext());
                String currentChwId = sessionManager.getUserId();
                String currentChwEmail = sessionManager.getUserEmail();

                android.util.Log.d("CaseHistoryFragment",
                        "Filtering history for ID: " + currentChwId + ", Email: " + currentChwEmail);

                // Filter online reports if CHW ID is available
                // Note: Offline reports filtering logic is also needed

                final int onlineCount = reports != null ? reports.size() : 0;

                // 2. Fetch Offline Data
                com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                        .getDatabase(requireContext()).unsyncedReportDao();

                List<com.healthtracker.chw.data.local.UnsyncedReport> myLocalReports;
                if (currentChwId != null || currentChwEmail != null) {
                    // Use new method to filter by either ID or Email
                    // Make sure we handle nulls by passing empty strings if needed,
                    // preventing match-all if both null (but we checked if block)
                    String filterId = currentChwId != null ? currentChwId : "___dummy_id___";
                    String filterEmail = currentChwEmail != null ? currentChwEmail : "___dummy_email___";
                    myLocalReports = dao.getReportsByChwIdOrEmail(filterId, filterEmail);
                } else {
                    myLocalReports = new ArrayList<>();
                }

                final int pendingCount = myLocalReports.size();

                // 3. Convert Offline Data and Add
                if (!myLocalReports.isEmpty()) {
                    for (com.healthtracker.chw.data.local.UnsyncedReport local : myLocalReports) {
                        DiseaseReport report = new DiseaseReport();
                        report.setReportId("LOCAL-" + local.id); // Temporary ID
                        report.setDiseaseType(local.diseaseType);

                        try {
                            if (local.timestamp > 0) {
                                report.setReportDate(new Date(local.timestamp));
                            } else {
                                report.setReportDate(new Date());
                            }
                        } catch (Exception e) {
                            report.setReportDate(new Date());
                        }

                        report.setStatus("PENDING"); // Distinct status

                        // Risk
                        if (local.severity != null) {
                            com.healthtracker.chw.models.RiskAssessment risk = new com.healthtracker.chw.models.RiskAssessment();
                            risk.setLevel(local.severity);
                            report.setRiskAssessment(risk);
                        }

                        // Patient info
                        com.healthtracker.chw.models.Encounter encounter = new com.healthtracker.chw.models.Encounter();
                        com.healthtracker.chw.models.Patient patient = new com.healthtracker.chw.models.Patient();

                        // Use actual data if available
                        String gender = local.gender != null ? local.gender : "Patient";
                        patient.setGender(gender);

                        // Store name in gender field temporarily or we need to update Patient model
                        // Since CaseHistoryAdapter uses patient.getGender() to display text if dob is
                        // missing,
                        // And constructs a string.
                        // Let's rely on the Adapter which checks age and gender.
                        // But Adapter doesn't show Name!
                        // Adapter logic: "32y, Male"

                        if (local.dateOfBirth != null) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                patient.setDateOfBirth(sdf.parse(local.dateOfBirth));
                            } catch (Exception e) {
                                // Ignore
                            }
                        }

                        // If we have age but not DOB
                        if (patient.getDateOfBirth() == null && local.patientAge != null) {
                            // Approximate DOB
                            java.util.Calendar cal = java.util.Calendar.getInstance();
                            cal.add(java.util.Calendar.YEAR, -local.patientAge);
                            patient.setDateOfBirth(cal.getTime());
                        }

                        encounter.setPatient(patient);

                        // Location
                        if (local.latitude != null && local.longitude != null) {
                            com.healthtracker.chw.models.GPSLocation loc = new com.healthtracker.chw.models.GPSLocation();
                            loc.setLatitude(local.latitude);
                            loc.setLongitude(local.longitude);
                            encounter.setGpsLocation(loc);
                        }

                        report.setEncounter(encounter);

                        reports.add(report);
                    }
                }

                // 4. Sort by Date Descending
                java.util.Collections.sort(reports, (r1, r2) -> {
                    Date d1 = r1.getReportDate();
                    Date d2 = r2.getReportDate();
                    if (d1 == null)
                        return 1;
                    if (d2 == null)
                        return -1;
                    return d2.compareTo(d1); // Descending
                });

                // 5. Update UI
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        android.widget.Toast.makeText(getContext(),
                                "Loaded " + onlineCount + " Synced, " + pendingCount + " Pending",
                                android.widget.Toast.LENGTH_LONG).show();

                        // Diagnostic Toast
                        if (reports.isEmpty()) {
                            if (recyclerCaseHistory != null)
                                recyclerCaseHistory.setVisibility(View.GONE);
                            if (emptyState != null)
                                emptyState.setVisibility(View.VISIBLE);

                            // Check total count to distinguish between "empty DB" and "filter mismatch"
                            new Thread(() -> {
                                int totalReports = dao.getRecordCount(); // We assume this method exists or we use
                                                                         // getAllReports().size()
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        android.widget.Toast.makeText(getContext(),
                                                "No reports for " + currentChwEmail + "\n(Device has " + totalReports
                                                        + " total reports)",
                                                android.widget.Toast.LENGTH_LONG).show();
                                    });
                                }
                            }).start();
                        } else {
                            if (recyclerCaseHistory != null)
                                recyclerCaseHistory.setVisibility(View.VISIBLE);
                            if (emptyState != null)
                                emptyState.setVisibility(View.GONE);
                            adapter.setCases(reports);

                            android.widget.Toast.makeText(getContext(),
                                    "Loaded " + onlineCount + " Synced, " + pendingCount + " Pending",
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e) {
                android.util.Log.e("CaseHistoryFragment", "Error processing history", e);
            }
        }).start();
    }

    /**
     * Convert FHIR Bundle to DiseaseReport list for UI compatibility
     */
    private List<DiseaseReport> convertFHIRBundleToDiseaseReports(FHIRBundle bundle) {
        List<DiseaseReport> reports = new ArrayList<>();
        if (bundle == null || bundle.getEntry() == null) {
            return reports;
        }

        // Get current CHW ID
        // Get current CHW ID
        com.healthtracker.chw.utils.SessionManager sessionManager = new com.healthtracker.chw.utils.SessionManager(
                requireContext());
        String currentChwId = sessionManager.getUserId();

        for (FHIRBundle.Entry entry : bundle.getEntry()) {
            if (entry.getResource() instanceof FHIRObservation) {
                FHIRObservation obs = (FHIRObservation) entry.getResource();

                // Filter by Performer (CHW ID)
                if (currentChwId != null) {
                    boolean isMyReport = false;
                    if (obs.getPerformer() != null) {
                        for (com.healthtracker.chw.models.fhir.FHIRObservation.Reference ref : obs.getPerformer()) {
                            if (ref.getReference() != null && ref.getReference().contains(currentChwId)) {
                                isMyReport = true;
                                break;
                            }
                        }
                    } else {
                        // If no performer, assume not ours (or handle legacy data)
                        // For strictness, let's show only if we are sure
                    }

                    if (!isMyReport)
                        continue; // Skip if not performed by current user
                }

                DiseaseReport report = new DiseaseReport();

                // Set report ID
                report.setReportId(obs.getId());

                // Set disease type
                if (obs.getCode() != null && obs.getCode().getText() != null) {
                    report.setDiseaseType(obs.getCode().getText());
                }

                // Set report date
                if (obs.getEffectiveDateTime() != null) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        report.setReportDate(format.parse(obs.getEffectiveDateTime()));
                    } catch (Exception e) {
                        report.setReportDate(new Date());
                    }
                } else {
                    report.setReportDate(new Date());
                }

                // Set status
                report.setStatus("SYNCED");

                // Create RiskAssessment from observation value
                if (obs.getValueCodeableConcept() != null) {
                    com.healthtracker.chw.models.RiskAssessment risk = new com.healthtracker.chw.models.RiskAssessment();
                    risk.setLevel(obs.getValueCodeableConcept().getText());
                    risk.setDescription("Risk level: " + obs.getValueCodeableConcept().getText());
                    report.setRiskAssessment(risk);
                }

                // Note: Patient info would need to be fetched separately from Patient resource
                // For now, we'll leave it null and it will show "N/A" in the UI

                reports.add(report);
            }
        }

        return reports;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        loadCaseHistory();
    }
}
