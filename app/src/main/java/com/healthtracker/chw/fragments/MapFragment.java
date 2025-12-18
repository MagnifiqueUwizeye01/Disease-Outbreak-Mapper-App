package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.healthtracker.chw.R;
import com.healthtracker.chw.map.MapModule;
import com.healthtracker.chw.services.FHIRService;
import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIRObservation;

import java.util.HashSet;
import java.util.Set;

/**
 * Map fragment displaying outbreak data with clustering and filtering
 */
public class MapFragment extends Fragment {
    private MapModule mapModule;
    private SupportMapFragment mapFragment;
    private FHIRService fhirService;

    // Overview card views
    private TextView tvActiveCases;
    private TextView tvHighRiskCount;
    private TextView tvZonesCount;

    // Risk legend cards
    private MaterialCardView cardHighRisk;
    private MaterialCardView cardMediumRisk;
    private MaterialCardView cardLowRisk;
    private ImageView ivHighRiskCheck;
    private ImageView ivMediumRiskCheck;
    private ImageView ivLowRiskCheck;

    // Risk filter states
    private boolean highRiskEnabled = true;
    private boolean mediumRiskEnabled = true;
    private boolean lowRiskEnabled = true;

    // Map fallback
    private LinearLayout mapFallbackMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize FHIR service
        fhirService = new FHIRService(requireContext());

        // Initialize overview card views
        tvActiveCases = view.findViewById(R.id.tv_active_cases);
        tvHighRiskCount = view.findViewById(R.id.tv_high_risk_count);
        tvZonesCount = view.findViewById(R.id.tv_zones_count);

        // Initialize risk legend cards
        cardHighRisk = view.findViewById(R.id.card_high_risk);
        cardMediumRisk = view.findViewById(R.id.card_medium_risk);
        cardLowRisk = view.findViewById(R.id.card_low_risk);
        ivHighRiskCheck = view.findViewById(R.id.iv_high_risk_check);
        ivMediumRiskCheck = view.findViewById(R.id.iv_medium_risk_check);
        ivLowRiskCheck = view.findViewById(R.id.iv_low_risk_check);

        // Initialize map fallback
        mapFallbackMessage = view.findViewById(R.id.map_fallback_message);

        // Check for filters from arguments
        if (getArguments() != null) {
            String filter = getArguments().getString("risk_filter");
            if (filter != null) {
                switch (filter) {
                    case "HIGH":
                        highRiskEnabled = true;
                        mediumRiskEnabled = false;
                        lowRiskEnabled = false;
                        break;
                    case "MEDIUM":
                        highRiskEnabled = false;
                        mediumRiskEnabled = true;
                        lowRiskEnabled = false;
                        break;
                    case "LOW":
                        highRiskEnabled = false;
                        mediumRiskEnabled = false;
                        lowRiskEnabled = true;
                        break;
                }
            }
        }

        // Update Filter UI based on initial state
        updateRiskCardState(cardHighRisk, ivHighRiskCheck, highRiskEnabled);
        updateRiskCardState(cardMediumRisk, ivMediumRiskCheck, mediumRiskEnabled);
        updateRiskCardState(cardLowRisk, ivLowRiskCheck, lowRiskEnabled);

        // Initialize map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commitNow();
        }

        // Load overview statistics
        loadOverviewStatistics();

        // Wait for view to be ready before initializing map module
        // Use the view parameter instead of getView() to avoid null reference
        if (view != null) {
            view.post(() -> {
                View currentView = getView();
                if (currentView != null && mapFragment != null && isAdded()) {
                    try {
                        initializeMapModule();
                    } catch (Exception e) {
                        android.util.Log.e("MapFragment", "Error initializing map", e);
                        // Don't crash if map initialization fails (e.g., no API key)
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check for pending location to add (from recent submission)
        checkForPendingLocation();

        // Refresh map data to ensure latest reports are shown
        if (mapModule != null) {
            mapModule.refreshMapData();
        }
    }

    /**
     * Check for pending location ID stored by ReportCaseFragment
     * and add it to the map if found
     */
    private void checkForPendingLocation() {
        try {
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("map_refresh",
                    android.content.Context.MODE_PRIVATE);
            String pendingLocationId = prefs.getString("pending_location_id", null);
            String pendingDiseaseType = prefs.getString("pending_disease_type", null);
            String pendingRiskLevel = prefs.getString("pending_risk_level", null);

            if (pendingLocationId != null && !pendingLocationId.isEmpty()) {
                android.util.Log.d("MapFragment",
                        "Found pending location ID: " + pendingLocationId + ", adding to map");

                // Clear the pending values
                prefs.edit()
                        .remove("pending_location_id")
                        .remove("pending_disease_type")
                        .remove("pending_risk_level")
                        .apply();

                // Add the marker
                if (mapModule != null) {
                    mapModule.loadLocationById(pendingLocationId, pendingDiseaseType, pendingRiskLevel);
                } else {
                    // MapModule not ready yet, store again to retry later
                    android.util.Log.w("MapFragment", "MapModule not ready, will retry when map is initialized");
                    prefs.edit()
                            .putString("pending_location_id", pendingLocationId)
                            .putString("pending_disease_type", pendingDiseaseType)
                            .putString("pending_risk_level", pendingRiskLevel)
                            .apply();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("MapFragment", "Error checking for pending location", e);
        }
    }

    /**
     * Load and display overview statistics
     */
    private void loadOverviewStatistics() {
        // 1. Fetch Online Stats
        fhirService.getAllObservations(new FHIRService.ObservationsCallback() {
            @Override
            public void onSuccess(FHIRBundle bundle) {
                // 2. Fetch Offline Stats and Merge
                new Thread(() -> {
                    try {
                        com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                                .getDatabase(requireContext()).unsyncedReportDao();
                        java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = dao
                                .getAllReports();

                        if (!isAdded() || getActivity() == null)
                            return;

                        getActivity().runOnUiThread(() -> {
                            calculateAndDisplayStatistics(bundle, localReports);
                        });
                    } catch (Exception e) {
                        android.util.Log.e("MapFragment", "Error loading local stats", e);
                        // Fallback: Show online only
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> calculateAndDisplayStatistics(bundle, null));
                        }
                    }
                }).start();
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("MapFragment", "Error loading overview statistics: " + error);

                // Try to load at least local stats
                new Thread(() -> {
                    try {
                        com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                                .getDatabase(requireContext()).unsyncedReportDao();
                        java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = dao
                                .getAllReports();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                calculateAndDisplayStatistics(null, localReports);
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (tvActiveCases != null)
                                    tvActiveCases.setText("0");
                                if (tvHighRiskCount != null)
                                    tvHighRiskCount.setText("0");
                                if (tvZonesCount != null)
                                    tvZonesCount.setText("0");
                            });
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * Calculate statistics from FHIR observations + Local Reports
     */
    private void calculateAndDisplayStatistics(FHIRBundle bundle,
            java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports) {
        int totalCases = 0;
        int highRiskCount = 0;
        Set<String> uniqueZones = new HashSet<>();

        // 1. Process Online Data
        if (bundle != null && bundle.getEntry() != null) {
            for (FHIRBundle.Entry entry : bundle.getEntry()) {
                if (entry.getResource() instanceof FHIRObservation) {
                    FHIRObservation obs = (FHIRObservation) entry.getResource();

                    // Filter: Skip lab results
                    boolean isLabResult = false;
                    if (obs.getCode() != null && obs.getCode().getCoding() != null) {
                        for (FHIRObservation.Coding coding : obs.getCode().getCoding()) {
                            if (coding.getSystem() != null && coding.getSystem().contains("loinc.org")) {
                                isLabResult = true;
                                break;
                            }
                        }
                    }
                    if (isLabResult)
                        continue;

                    // Check valid encounter
                    boolean hasValidEncounter = false;
                    if (obs.getEncounter() != null && obs.getEncounter().getReference() != null) {
                        String ref = obs.getEncounter().getReference();
                        if (ref.startsWith("Encounter/") || (ref.startsWith("Encounter") && !ref.startsWith("urn:"))) {
                            hasValidEncounter = true;
                            uniqueZones.add(ref);
                        }
                    }

                    if (hasValidEncounter) {
                        totalCases++;
                        if (obs.getValueCodeableConcept() != null && obs.getValueCodeableConcept().getText() != null) {
                            String severity = obs.getValueCodeableConcept().getText().toLowerCase();
                            if (severity.contains("severe") || severity.contains("high")) {
                                highRiskCount++;
                            }
                        }
                    }
                }
            }
        }

        // 2. Process Local Data
        if (localReports != null) {
            for (com.healthtracker.chw.data.local.UnsyncedReport report : localReports) {
                totalCases++;

                if (report.severity != null) {
                    String sev = report.severity.toLowerCase();
                    if (sev.contains("severe") || sev.contains("high")) {
                        highRiskCount++;
                    }
                }

                // Use coordinates as "Zone" proxy (not perfect but counts distinct locations)
                if (report.latitude != null && report.longitude != null) {
                    uniqueZones.add(report.latitude + "," + report.longitude);
                }
            }
        }

        // Update UI
        if (tvActiveCases != null) {
            tvActiveCases.setText(String.valueOf(totalCases));
        }
        if (tvHighRiskCount != null) {
            tvHighRiskCount.setText(String.valueOf(highRiskCount));
        }
        if (tvZonesCount != null) {
            tvZonesCount.setText(String.valueOf(uniqueZones.size()));
        }
    }

    private void initializeMapModule() {
        if (mapFragment == null || getView() == null || !isAdded()) {
            android.util.Log.w("MapFragment", "Cannot initialize map: fragment not ready");
            return;
        }

        try {
            // Initialize MapModule
            mapModule = new MapModule(requireContext(), mapFragment, new MapModule.MapCallback() {
                @Override
                public void onMarkerClick(MapModule.CaseMarker marker) {
                    // Handle marker click - could show details dialog
                }

                @Override
                public void onMapReady() {
                    // Map is ready - hide fallback message
                    if (mapFallbackMessage != null) {
                        mapFallbackMessage.setVisibility(android.view.View.GONE);
                    }
                }
            });

            // Setup risk legend filters
            View currentView = getView();
            if (currentView != null) {
                setupRiskLegendFilters();
                setupFABs(currentView);
                // Set initial filter state
                if (mapModule != null) {
                    mapModule.setRiskFilters(highRiskEnabled, mediumRiskEnabled, lowRiskEnabled);
                }
            }
        } catch (IllegalStateException e) {
            // Handle case where Google Maps API key is missing or invalid
            android.util.Log.e("MapFragment", "Map initialization failed - check API key", e);
            showMapFallback();
        } catch (Exception e) {
            android.util.Log.e("MapFragment", "Unexpected error initializing map", e);
            showMapFallback();
        }
    }

    /**
     * Show fallback message when map can't be loaded
     */
    private void showMapFallback() {
        if (mapFallbackMessage != null && isAdded()) {
            mapFallbackMessage.setVisibility(android.view.View.VISIBLE);
        }
    }

    /**
     * Setup modern risk legend filter cards with animations
     */
    private void setupRiskLegendFilters() {
        if (cardHighRisk != null) {
            cardHighRisk.setOnClickListener(v -> {
                highRiskEnabled = !highRiskEnabled;
                updateRiskCardState(cardHighRisk, ivHighRiskCheck, highRiskEnabled);
                if (mapModule != null) {
                    mapModule.setRiskFilters(highRiskEnabled, mediumRiskEnabled, lowRiskEnabled);
                    showFilterToast("High Risk", highRiskEnabled);
                }
            });
            // Initial state
            updateRiskCardState(cardHighRisk, ivHighRiskCheck, highRiskEnabled);
        }

        if (cardMediumRisk != null) {
            cardMediumRisk.setOnClickListener(v -> {
                mediumRiskEnabled = !mediumRiskEnabled;
                updateRiskCardState(cardMediumRisk, ivMediumRiskCheck, mediumRiskEnabled);
                if (mapModule != null) {
                    mapModule.setRiskFilters(highRiskEnabled, mediumRiskEnabled, lowRiskEnabled);
                    showFilterToast("Medium Risk", mediumRiskEnabled);
                }
            });
            // Initial state
            updateRiskCardState(cardMediumRisk, ivMediumRiskCheck, mediumRiskEnabled);
        }

        if (cardLowRisk != null) {
            cardLowRisk.setOnClickListener(v -> {
                lowRiskEnabled = !lowRiskEnabled;
                updateRiskCardState(cardLowRisk, ivLowRiskCheck, lowRiskEnabled);
                if (mapModule != null) {
                    mapModule.setRiskFilters(highRiskEnabled, mediumRiskEnabled, lowRiskEnabled);
                    showFilterToast("Low Risk", lowRiskEnabled);
                }
            });
            // Initial state
            updateRiskCardState(cardLowRisk, ivLowRiskCheck, lowRiskEnabled);
        }
    }

    private void showFilterToast(String riskType, boolean enabled) {
        String status = enabled ? "Shown" : "Hidden";
        android.widget.Toast
                .makeText(requireContext(), riskType + " Zones " + status, android.widget.Toast.LENGTH_SHORT).show();
    }

    /**
     * Update risk card visual state with smooth animation
     */
    private void updateRiskCardState(MaterialCardView card, ImageView checkIcon, boolean enabled) {
        if (card == null || checkIcon == null)
            return;

        // Animate check icon
        if (enabled) {
            checkIcon.setVisibility(android.view.View.VISIBLE);
            Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in);
            checkIcon.startAnimation(fadeIn);
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    checkIcon.setVisibility(android.view.View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            checkIcon.startAnimation(fadeOut);
        }

        // Update card elevation for visual feedback
        if (enabled) {
            card.setCardElevation(6f);
            card.setAlpha(1.0f);
        } else {
            card.setCardElevation(2f);
            card.setAlpha(0.6f);
        }
    }

    private void setupFABs(View view) {
        FloatingActionButton fabRefresh = view.findViewById(R.id.fab_refresh_map);
        FloatingActionButton fabMyLocation = view.findViewById(R.id.fab_my_location);

        if (fabRefresh != null) {
            fabRefresh.setOnClickListener(v -> {
                if (mapModule != null) {
                    mapModule.loadMapData();
                }
            });
        }

        if (fabMyLocation != null) {
            fabMyLocation.setOnClickListener(v -> {
                // Center map on user's location
                // Implementation requires location permission
            });
        }
    }

    /**
     * Public method to add a new marker from a Location ID
     * Called after a new report is submitted
     */
    public void addLocationMarker(String locationId, String diseaseType, String riskLevel) {
        if (mapModule != null && locationId != null && !locationId.isEmpty()) {
            mapModule.loadLocationById(locationId, diseaseType, riskLevel);
        } else {
            android.util.Log.w("MapFragment", "Cannot add marker: mapModule is null or locationId is empty");
        }
    }

    /**
     * Public method to refresh the entire map
     * Called after new submissions to reload all data
     */
    public void refreshMap() {
        if (mapModule != null) {
            mapModule.refreshMapData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cleanup map module resources
        if (mapModule != null) {
            mapModule.cleanup();
        }
    }
}
