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
        adapter = new CaseHistoryAdapter(reportId -> {
            // Navigate to case details
            NavController navController = Navigation.findNavController(view);
            Bundle args = new Bundle();
            args.putString("case_id", reportId);
            navController.navigate(R.id.action_case_history_to_details, args);
        });

        recyclerCaseHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCaseHistory.setAdapter(adapter);

        // Load case history
        loadCaseHistory();
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
                final int onlineCount = reports != null ? reports.size() : 0;

                // 2. Fetch Offline Data
                com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                        .getDatabase(requireContext()).unsyncedReportDao();
                List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = dao.getAllReports();
                final int pendingCount = localReports != null ? localReports.size() : 0;

                // 3. Convert Offline Data and Add
                if (localReports != null) {
                    for (com.healthtracker.chw.data.local.UnsyncedReport local : localReports) {
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

                        // Fake patient info for display
                        com.healthtracker.chw.models.Encounter encounter = new com.healthtracker.chw.models.Encounter();
                        com.healthtracker.chw.models.Patient patient = new com.healthtracker.chw.models.Patient();
                        patient.setGender("Patient"); // Placeholder
                        // We could store patient name in UnsyncedReport if we wanted better UI
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

                        if (reports.isEmpty()) {
                            if (recyclerCaseHistory != null)
                                recyclerCaseHistory.setVisibility(View.GONE);
                            if (emptyState != null)
                                emptyState.setVisibility(View.VISIBLE);
                        } else {
                            if (recyclerCaseHistory != null)
                                recyclerCaseHistory.setVisibility(View.VISIBLE);
                            if (emptyState != null)
                                emptyState.setVisibility(View.GONE);
                            adapter.setCases(reports);
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

        for (FHIRBundle.Entry entry : bundle.getEntry()) {
            if (entry.getResource() instanceof FHIRObservation) {
                FHIRObservation obs = (FHIRObservation) entry.getResource();
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
