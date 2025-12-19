package com.healthtracker.chw.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.healthtracker.chw.R;
import com.healthtracker.chw.services.FHIRService;
import com.healthtracker.chw.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

public class DashboardFragment extends Fragment {

    private TextView tvPendingReports;
    private TextView tvHighRiskCount;
    private TextView tvMediumRiskCount;
    private TextView tvLowRiskCount;
    private FHIRService fhirService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize services
        fhirService = new FHIRService(requireContext());

        // Find views
        tvPendingReports = view.findViewById(R.id.tv_pending_reports);
        tvHighRiskCount = view.findViewById(R.id.tv_high_risk_count);
        tvMediumRiskCount = view.findViewById(R.id.tv_medium_risk_count);
        tvLowRiskCount = view.findViewById(R.id.tv_low_risk_count);

        // Update welcome message with actual user name
        updateWelcomeMessage(view);

        // Load dashboard stats
        loadDashboardStats();

        final NavController navController = Navigation.findNavController(view);

        // Quick action chips
        View chipHighRisk = view.findViewById(R.id.chip_high_risk);
        View chipSyncPending = view.findViewById(R.id.chip_sync_pending);

        if (chipHighRisk != null) {
            chipHighRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "HIGH");
                navController.navigate(R.id.mapFragment, args);
            });
        }

        if (chipSyncPending != null) {
            chipSyncPending.setOnClickListener(v -> navController.navigate(R.id.caseHistoryFragment));
        }

        View chipMediumRisk = view.findViewById(R.id.chip_medium_risk);
        if (chipMediumRisk != null) {
            chipMediumRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "MEDIUM");
                navController.navigate(R.id.mapFragment, args);
            });
        }

        View chipLowRisk = view.findViewById(R.id.chip_low_risk);
        if (chipLowRisk != null) {
            chipLowRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "LOW");
                navController.navigate(R.id.mapFragment, args);
            });
        }

        // Main cards
        View cardReportCase = view.findViewById(R.id.card_report_case);
        View cardOutbreakMap = view.findViewById(R.id.card_outbreak_map);
        View cardTrends = view.findViewById(R.id.card_trends);
        View cardHistory = view.findViewById(R.id.card_history);

        if (cardReportCase != null) {
            cardReportCase.setOnClickListener(v -> navController.navigate(R.id.reportCaseFragment));
        }

        if (cardOutbreakMap != null) {
            cardOutbreakMap.setOnClickListener(v -> navController.navigate(R.id.mapFragment));
        }

        if (cardTrends != null) {
            cardTrends.setOnClickListener(v -> navController.navigate(R.id.analyticsFragment));
        }

        if (cardHistory != null) {
            cardHistory.setOnClickListener(v -> navController.navigate(R.id.caseHistoryFragment));
        }

        // Stats cards
        View cardPendingReports = view.findViewById(R.id.card_pending_reports);
        View cardHighRisk = view.findViewById(R.id.card_high_risk);
        View cardMediumRisk = view.findViewById(R.id.card_medium_risk);
        View cardLowRisk = view.findViewById(R.id.card_low_risk);

        if (cardPendingReports != null) {
            cardPendingReports.setOnClickListener(v -> navController.navigate(R.id.caseHistoryFragment));
        }

        if (cardHighRisk != null) {
            cardHighRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "HIGH");
                navController.navigate(R.id.mapFragment, args);
            });
        }

        if (cardMediumRisk != null) {
            cardMediumRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "MEDIUM");
                navController.navigate(R.id.mapFragment, args);
            });
        }

        if (cardLowRisk != null) {
            cardLowRisk.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("risk_filter", "LOW");
                navController.navigate(R.id.mapFragment, args);
            });
        }
    }

    private void updateWelcomeMessage(View view) {
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        if (tvGreeting != null) {
            SessionManager sessionManager = new SessionManager(requireContext());
            String userName = sessionManager.getUserName();
            if (userName != null && !userName.trim().isEmpty()) {
                tvGreeting.setText("Welcome, " + userName);
            } else {
                // Fallback to email if name not available
                String userEmail = sessionManager.getUserEmail();
                if (userEmail != null && !userEmail.trim().isEmpty()) {
                    // Extract name from email (part before @)
                    String nameFromEmail = userEmail.split("@")[0];
                    tvGreeting.setText("Welcome, " + nameFromEmail);
                } else {
                    tvGreeting.setText("Welcome, CHW");
                }
            }
        }

    }

    /**
     * Load and update dashboard statistics
     */
    private void loadDashboardStats() {
        // Load pending reports count (offline pending cases)
        loadPendingReportsCount();

        // Load risk counts
        loadRiskCounts();
    }

    /**
     * Load pending reports count from Room Database (offline cases)
     */
    private void loadPendingReportsCount() {
        new Thread(() -> {
            try {
                // Get current CHW ID
                // Get current CHW ID
                com.healthtracker.chw.utils.SessionManager sessionManager = new com.healthtracker.chw.utils.SessionManager(
                        requireContext());
                String currentChwId = sessionManager.getUserId();

                com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                        .getDatabase(requireContext()).unsyncedReportDao();

                int count = 0;
                // Get current Email
                String currentChwEmail = sessionManager.getUserEmail();

                if (currentChwId != null || currentChwEmail != null) {
                    String filterId = currentChwId != null ? currentChwId : "___dummy_id___";
                    String filterEmail = currentChwEmail != null ? currentChwEmail : "___dummy_email___";
                    // We need a count query that supports OR.
                    // Since getCountByChwId only checked ID, let's fetch list size or add a new
                    // count query.
                    // For efficiency, let's use the list size of the new query or add a dedicated
                    // count query.
                    // Let's add getCountByChwIdOrEmail to DAO to be efficient.
                    // Wait, I didn't add count query to DAO yet. Let's just use list size for now
                    // or add it.
                    // Actually, let's use list size for now as it's safer than modifying DAO again
                    // and again.
                    // Or better, let's modify DAO to add a count method.
                    // But I can't modify DAO and this file in parallel safely if I want to use it
                    // immediately.
                    // I already added getReportsByChwIdOrEmail. I can use .size() on that.
                    count = dao.getReportsByChwIdOrEmail(filterId, filterEmail).size();
                }

                // If no user ID, count remains 0 (or could show all, but isolation implies 0)

                int finalCount = count;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (tvPendingReports != null) {
                            tvPendingReports.setText(String.valueOf(finalCount));
                        }
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("DashboardFragment", "Error loading pending count", e);
            }
        }).start();
    }

    /**
     * Load risk counts from FHIR observations + Local Offline Data
     */
    private void loadRiskCounts() {
        // 1. Fetch Online Data
        fhirService.getAllObservations(new FHIRService.ObservationsCallback() {
            @Override
            public void onSuccess(com.healthtracker.chw.models.fhir.FHIRBundle bundle) {
                // Use final array to allow modification inside lambda
                final int[] riskCounts = { 0, 0, 0 }; // High, Medium, Low

                // Get current CHW ID
                // Get current CHW ID
                com.healthtracker.chw.utils.SessionManager sessionManager = new com.healthtracker.chw.utils.SessionManager(
                        requireContext());
                String currentChwId = sessionManager.getUserId();

                // Count Online Risks
                if (bundle != null && bundle.getEntry() != null) {
                    for (com.healthtracker.chw.models.fhir.FHIRBundle.Entry entry : bundle.getEntry()) {
                        if (entry.getResource() instanceof com.healthtracker.chw.models.fhir.FHIRObservation) {
                            com.healthtracker.chw.models.fhir.FHIRObservation obs = (com.healthtracker.chw.models.fhir.FHIRObservation) entry
                                    .getResource();

                            // Filter by Performer (CHW ID)
                            if (currentChwId != null) {
                                boolean isMyReport = false;
                                if (obs.getPerformer() != null) {
                                    for (com.healthtracker.chw.models.fhir.FHIRObservation.Reference ref : obs
                                            .getPerformer()) {
                                        if (ref.getReference() != null && ref.getReference().contains(currentChwId)) {
                                            isMyReport = true;
                                            break;
                                        }
                                    }
                                }
                                if (!isMyReport)
                                    continue;
                            }

                            String severity = "";
                            if (obs.getValueCodeableConcept() != null
                                    && obs.getValueCodeableConcept().getText() != null) {
                                severity = obs.getValueCodeableConcept().getText().toLowerCase();
                            } else if (obs.getValueString() != null) {
                                severity = obs.getValueString().toLowerCase();
                            }

                            if (severity.contains("severe") || severity.contains("high")) {
                                riskCounts[0]++;
                            } else if (severity.contains("moderate") || severity.contains("medium")) {
                                riskCounts[1]++;
                            } else if (severity.contains("mild") || severity.contains("low")) {
                                riskCounts[2]++;
                            }
                        }
                    }
                }

                // 2. Fetch Local Offline Data and Merge
                new Thread(() -> {
                    try {
                        com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                                .getDatabase(requireContext()).unsyncedReportDao();
                        java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = new java.util.ArrayList<>();

                        if (currentChwId != null || currentChwId == null) { // Logic above handles nulls but we need
                                                                            // email here too
                            SharedPreferences appPrefs = requireContext().getSharedPreferences("app_prefs",
                                    android.content.Context.MODE_PRIVATE);
                            String currentChwEmail = appPrefs.getString("user_email", null);

                            String filterId = currentChwId != null ? currentChwId : "___dummy_id___";
                            String filterEmail = currentChwEmail != null ? currentChwEmail : "___dummy_email___";
                            localReports = dao.getReportsByChwIdOrEmail(filterId, filterEmail);
                        }

                        for (com.healthtracker.chw.data.local.UnsyncedReport report : localReports) {
                            // No manual filtering needed

                            if (report.severity != null) {
                                String sev = report.severity.toLowerCase();
                                if (sev.contains("severe") || sev.contains("high")) {
                                    riskCounts[0]++;
                                } else if (sev.contains("moderate") || sev.contains("medium")) {
                                    riskCounts[1]++;
                                } else if (sev.contains("mild") || sev.contains("low")) {
                                    riskCounts[2]++;
                                }
                            }
                        }

                        // Update UI
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (tvHighRiskCount != null) {
                                    tvHighRiskCount.setText(String.valueOf(riskCounts[0]));
                                }
                                if (tvMediumRiskCount != null) {
                                    tvMediumRiskCount.setText(String.valueOf(riskCounts[1]));
                                }
                                if (tvLowRiskCount != null) {
                                    tvLowRiskCount.setText(String.valueOf(riskCounts[2]));
                                }
                            });
                        }

                    } catch (Exception e) {
                        android.util.Log.e("DashboardFragment", "Error merging local data", e);
                    }
                }).start();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("DashboardFragment", "Error loading risk counts: " + error);

                // Even if online fails, try to show local count
                new Thread(() -> {
                    try {
                        // Get current CHW ID
                        SharedPreferences prefs = requireContext().getSharedPreferences("app_prefs",
                                android.content.Context.MODE_PRIVATE);
                        String currentChwId = prefs.getString("chw_id", null);

                        com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                                .getDatabase(requireContext()).unsyncedReportDao();
                        java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = dao
                                .getAllReports();

                        int[] localRisks = { 0, 0, 0 };
                        for (com.healthtracker.chw.data.local.UnsyncedReport report : localReports) {
                            // Filter local reports
                            if (currentChwId != null && !currentChwId.equals(report.chwId)) {
                                continue;
                            }

                            if (report.severity != null) {
                                String sev = report.severity.toLowerCase();
                                if (sev.contains("severe") || sev.contains("high")) {
                                    localRisks[0]++;
                                } else if (sev.contains("moderate") || sev.contains("medium")) {
                                    localRisks[1]++;
                                } else if (sev.contains("mild") || sev.contains("low")) {
                                    localRisks[2]++;
                                }
                            }
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (tvHighRiskCount != null)
                                    tvHighRiskCount.setText(String.valueOf(localRisks[0]));
                                if (tvMediumRiskCount != null)
                                    tvMediumRiskCount.setText(String.valueOf(localRisks[1]));
                                if (tvLowRiskCount != null)
                                    tvLowRiskCount.setText(String.valueOf(localRisks[2]));
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh stats when fragment becomes visible
        loadDashboardStats();
    }
}