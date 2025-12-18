package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;
import com.healthtracker.chw.data.local.AppDatabase;
import com.healthtracker.chw.data.local.UnsyncedReport;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.services.FHIRService;
import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIRObservation;
import com.healthtracker.chw.models.fhir.FHIRLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class AnalyticsFragment extends Fragment {

    private static final String TAG = "AnalyticsFragment";
    private FHIRService fhirService;
    private AppDatabase db;

    // UI References
    private TextView tvLowRiskCount, tvMediumRiskCount, tvHighRiskCount;
    private TextView tvTotalCases, tvActiveZones;
    private TextView[] weeklyCounts = new TextView[5];
    private View[] weeklyBars = new View[5];
    private View[] legendItems = new View[3];
    private TextView[] legendTexts = new TextView[3];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        fhirService = new FHIRService(requireContext());
        db = AppDatabase.getDatabase(requireContext());

        initializeViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Loading analytics data.");
        loadAnalyticsData();
    }

    private void initializeViews(View view) {
        // Stats
        tvLowRiskCount = view.findViewById(R.id.tv_low_risk_count);
        tvMediumRiskCount = view.findViewById(R.id.tv_medium_risk_count);
        tvHighRiskCount = view.findViewById(R.id.tv_high_risk_count);
        tvTotalCases = view.findViewById(R.id.tv_total_cases);
        tvActiveZones = view.findViewById(R.id.tv_active_zones);

        // Weekly Chart (Mon-Fri)
        weeklyCounts[0] = view.findViewById(R.id.text_mon_count);
        weeklyBars[0] = view.findViewById(R.id.bar_mon);
        weeklyCounts[1] = view.findViewById(R.id.text_tue_count);
        weeklyBars[1] = view.findViewById(R.id.bar_tue);
        weeklyCounts[2] = view.findViewById(R.id.text_wed_count);
        weeklyBars[2] = view.findViewById(R.id.bar_wed);
        weeklyCounts[3] = view.findViewById(R.id.text_thu_count);
        weeklyBars[3] = view.findViewById(R.id.bar_thu);
        weeklyCounts[4] = view.findViewById(R.id.text_fri_count);
        weeklyBars[4] = view.findViewById(R.id.bar_fri);

        // Legend
        legendItems[0] = view.findViewById(R.id.legend_item_1);
        legendTexts[0] = view.findViewById(R.id.legend_text_1);
        legendItems[1] = view.findViewById(R.id.legend_item_2);
        legendTexts[1] = view.findViewById(R.id.legend_text_2);
        legendItems[2] = view.findViewById(R.id.legend_item_3);
        legendTexts[2] = view.findViewById(R.id.legend_text_3);
    }

    private void loadAnalyticsData() {
        // 1. Fetch Server Observations
        fhirService.getAllObservations(new FHIRService.ObservationsCallback() {
            @Override
            public void onSuccess(FHIRBundle bundle) {
                // Once we have server data, fetch local data and merge
                fetchLocalAndMerge(bundle);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error fetching observations: " + error);
                // Even if server fails, show local
                fetchLocalAndMerge(null);
            }
        });

        // 2. Fetch Locations (Active Zones) - Server Only (Local reports don't have
        // location IDs yet)
        // Improvement: We COULD count local lat/lon as zones too.
        fhirService.getAllLocations(new FHIRService.LocationsCallback() {
            @Override
            public void onSuccess(FHIRBundle bundle) {
                processLocations(bundle);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error fetching locations: " + error);
            }
        });
    }

    private void fetchLocalAndMerge(FHIRBundle serverBundle) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch local reports
            List<UnsyncedReport> localReports = db.unsyncedReportDao().getAllReports();

            requireActivity().runOnUiThread(() -> {
                processMergedData(serverBundle, localReports);
            });
        });
    }

    private void processMergedData(FHIRBundle serverBundle, List<UnsyncedReport> localReports) {
        if (!isAdded())
            return;

        List<DiseaseReport> mergedReports = new ArrayList<>();
        Map<String, Integer> dCounts = new HashMap<>();

        int low = 0;
        int medium = 0;
        int high = 0;

        // Setup Zone Tracking for Local Data merging
        // We will pass this to a shared zone counter if we want to merge local zones
        // too
        Set<String> localZones = new HashSet<>();

        // -- PROCESS SERVER DATA --
        if (serverBundle != null && serverBundle.getEntry() != null) {
            for (FHIRBundle.Entry entry : serverBundle.getEntry()) {
                if (entry.getResource() instanceof FHIRObservation) {
                    FHIRObservation obs = (FHIRObservation) entry.getResource();

                    // Disease Count
                    String disease = "Unknown";
                    if (obs.getCode() != null && obs.getCode().getText() != null) {
                        disease = obs.getCode().getText();
                    }
                    dCounts.put(disease, dCounts.getOrDefault(disease, 0) + 1);

                    // Risk Count
                    String severity = "";
                    if (obs.getValueCodeableConcept() != null && obs.getValueCodeableConcept().getText() != null) {
                        severity = obs.getValueCodeableConcept().getText();
                    } else if (obs.getValueString() != null) {
                        severity = obs.getValueString();
                    }

                    String level = severity.toLowerCase();
                    if (level.contains("high") || level.contains("severe"))
                        high++;
                    else if (level.contains("medium") || level.contains("moderate"))
                        medium++;
                    else if (level.contains("low") || level.contains("mild"))
                        low++;
                    // else ignore

                    // Report List
                    DiseaseReport report = new DiseaseReport();
                    report.setDiseaseType(disease);
                    if (obs.getEffectiveDateTime() != null) {
                        report.setReportDate(new Date()); // Parsing todo
                    } else {
                        report.setReportDate(new Date());
                    }
                    mergedReports.add(report);
                }
            }
        }

        // -- PROCESS LOCAL DATA --
        if (localReports != null) {
            for (UnsyncedReport local : localReports) {
                // Disease Count
                String disease = local.diseaseType != null ? local.diseaseType : "Unknown";
                dCounts.put(disease, dCounts.getOrDefault(disease, 0) + 1);

                // Risk Count
                String severity = local.severity != null ? local.severity.toLowerCase() : "";
                if (severity.contains("high") || severity.contains("severe"))
                    high++;
                else if (severity.contains("medium") || severity.contains("moderate"))
                    medium++;
                else if (severity.contains("low") || severity.contains("mild"))
                    low++;

                // Zones (Local)
                if (local.latitude != null && local.longitude != null) {
                    String zoneKey = String.format(java.util.Locale.US, "%.2f,%.2f",
                            Math.round(local.latitude * 100.0) / 100.0,
                            Math.round(local.longitude * 100.0) / 100.0);
                    localZones.add(zoneKey);
                }

                // Report List
                DiseaseReport report = new DiseaseReport();
                report.setDiseaseType(disease);
                report.setReportDate(new Date(local.timestamp));
                mergedReports.add(report);
            }
        }

        // Update UI
        tvTotalCases.setText(String.valueOf(mergedReports.size()));
        tvLowRiskCount.setText(String.valueOf(low));
        tvMediumRiskCount.setText(String.valueOf(medium));
        tvHighRiskCount.setText(String.valueOf(high));

        android.widget.Toast.makeText(getContext(),
                "Analytics: " + mergedReports.size() + " total (S:" +
                        (serverBundle != null && serverBundle.getEntry() != null ? serverBundle.getEntry().size() : 0) +
                        " L:" + (localReports != null ? localReports.size() : 0) + ")",
                android.widget.Toast.LENGTH_LONG).show();

        updateWeeklyChart(mergedReports);

        updateDiseaseDistribution(dCounts);

        // Pass local zones to location processor if needed, but for now let's just
        // update Active Zones
        // by merging fetching server locations with these local ones.
        // Ideally we should have merged them in one go.
        // Let's store localZones for the processLocations callback to use if it comes
        // later,
        // OR better: trigger location processing passing these zones.
        // For simplicity in this fragment structure, we'll wait for processLocations to
        // be called by its callback.
        // But wait, they are async.
        // Hack: update Active Zones text view here adding what we have locally?
        // No, that doubles counting if we don't know overlaps.
        // Correct way: We need the server locations to merge.
        // Lets just save localZones to a member variable and update in
        // processLocations?
        this.pendingLocalZones = localZones;
        updateActiveZonesUI(); // Try update if server data already came
    }

    private Set<String> pendingLocalZones = new HashSet<>();
    private Set<String> serverZones = new HashSet<>();

    private void processLocations(FHIRBundle bundle) {
        if (!isAdded())
            return;

        serverZones.clear();
        if (bundle != null && bundle.getEntry() != null) {
            for (FHIRBundle.Entry entry : bundle.getEntry()) {
                if (entry.getResource() instanceof FHIRLocation) {
                    FHIRLocation loc = (FHIRLocation) entry.getResource();
                    if (loc.getPosition() != null) {
                        double lat = loc.getPosition().getLatitude();
                        double lon = loc.getPosition().getLongitude();
                        String zoneKey = String.format(java.util.Locale.US, "%.2f,%.2f",
                                Math.round(lat * 100.0) / 100.0,
                                Math.round(lon * 100.0) / 100.0);
                        serverZones.add(zoneKey);
                    }
                }
            }
        }

        requireActivity().runOnUiThread(this::updateActiveZonesUI);
    }

    private void updateActiveZonesUI() {
        Set<String> merged = new HashSet<>(serverZones);
        merged.addAll(pendingLocalZones);
        tvActiveZones.setText(String.valueOf(merged.size()));
    }

    // --- UI Update Helpers ---

    private void updateWeeklyChart(List<DiseaseReport> reports) {
        int total = reports.size();
        int[] daily = new int[5]; // M T W T F

        // Use current date as reference for week
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Calendar now = java.util.Calendar.getInstance();

        // Very basic simple "last 5 entries" or distribution if dates are not ideal
        // If we have dates, use them.
        for (DiseaseReport report : reports) {
            if (report.getReportDate() != null) {
                cal.setTime(report.getReportDate());
                // Only count current week? Or just day of week regardless?
                // Let's just do day of week distribution for visual feedback
                int day = cal.get(java.util.Calendar.DAY_OF_WEEK);
                int idx = -1;
                if (day == java.util.Calendar.MONDAY)
                    idx = 0;
                if (day == java.util.Calendar.TUESDAY)
                    idx = 1;
                if (day == java.util.Calendar.WEDNESDAY)
                    idx = 2;
                if (day == java.util.Calendar.THURSDAY)
                    idx = 3;
                if (day == java.util.Calendar.FRIDAY)
                    idx = 4;
                if (idx >= 0)
                    daily[idx]++;
            }
        }

        int max = 0;
        for (int c : daily)
            if (c > max)
                max = c;
        if (max == 0)
            max = 1;

        for (int i = 0; i < 5; i++) {
            if (weeklyCounts[i] != null)
                weeklyCounts[i].setText(String.valueOf(daily[i]));

            if (weeklyBars[i] != null) {
                // Scale height: 20dp to 100dp
                int heightDp = 20 + (daily[i] * 80 / max);
                float density = getResources().getDisplayMetrics().density;
                weeklyBars[i].getLayoutParams().height = (int) (heightDp * density);
                weeklyBars[i].requestLayout();
            }
        }
    }

    private void updateDiseaseDistribution(Map<String, Integer> distribution) {
        int total = 0;
        for (int c : distribution.values())
            total += c;
        if (total == 0) {
            // If 0, show placeholder or hide
            for (View v : legendItems)
                v.setVisibility(View.INVISIBLE);
            return;
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(distribution.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue())); // Descending

        for (int i = 0; i < 3; i++) {
            if (i < sorted.size()) {
                legendItems[i].setVisibility(View.VISIBLE);
                Map.Entry<String, Integer> entry = sorted.get(i);
                int pct = (int) (entry.getValue() * 100.0 / total);
                legendTexts[i].setText(entry.getKey() + " (" + pct + "%)");
            } else {
                legendItems[i].setVisibility(View.INVISIBLE);
            }
        }
    }
}
