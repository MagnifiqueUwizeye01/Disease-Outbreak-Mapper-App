package com.healthtracker.chw.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.android.gms.maps.model.Marker;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.RiskAssessment;
import com.healthtracker.chw.services.FHIRService;
import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIRObservation;
import com.healthtracker.chw.models.fhir.FHIREncounter;
import com.healthtracker.chw.models.fhir.FHIRLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/**
 * Module for managing Google Maps with disease outbreak data
 */
public class MapModule {
    private static final String TAG = "MapModule";
    private Context context;
    private GoogleMap googleMap;
    private ClusterManager<CaseMarker> clusterManager;
    private MapCallback callback;
    private FHIRService fhirService;

    // Risk level filter states
    private boolean highRiskEnabled = true;
    private boolean mediumRiskEnabled = true;
    private boolean lowRiskEnabled = true;

    // Store all markers for filtering
    private List<CaseMarker> allMarkers = new ArrayList<>();

    // Threading
    private ExecutorService executorService;
    private Handler mainHandler;

    public MapModule(Context context, SupportMapFragment mapFragment, MapCallback callback) {
        this.context = context;
        this.callback = callback;
        this.fhirService = new FHIRService(context);
        this.executorService = Executors.newFixedThreadPool(5); // Limit concurrent requests
        this.mainHandler = new Handler(Looper.getMainLooper());

        try {
            mapFragment.getMapAsync(googleMap -> {
                if (googleMap != null) {
                    this.googleMap = googleMap;
                    initializeMap();
                    if (callback != null) {
                        callback.onMapReady();
                    }
                    // Check for pending locations after map is ready
                    checkForPendingLocations();
                } else {
                    Log.e(TAG, "GoogleMap is null - API key may be invalid");
                    if (callback != null) {
                        callback.onMapReady(); // Still notify, but map will be null
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to get map async", e);
            if (callback != null) {
                callback.onMapReady(); // Notify even on error
            }
        }
    }

    private void initializeMap() {
        if (googleMap == null)
            return;

        // Setup cluster manager
        clusterManager = new ClusterManager<>(context, googleMap);

        // Set custom renderer for colored markers based on risk level
        clusterManager.setRenderer(new DefaultClusterRenderer<CaseMarker>(context, googleMap, clusterManager) {
            @Override
            protected void onBeforeClusterItemRendered(CaseMarker item, MarkerOptions markerOptions) {
                super.onBeforeClusterItemRendered(item, markerOptions);

                // Set marker color based on risk level
                float hue = BitmapDescriptorFactory.HUE_GREEN; // Default: low risk
                if (item.getRiskLevel() != null) {
                    String risk = item.getRiskLevel().toLowerCase();
                    if (risk.contains("high") || risk.contains("severe")) {
                        hue = BitmapDescriptorFactory.HUE_RED; // High risk = red
                    } else if (risk.contains("medium") || risk.contains("moderate")) {
                        hue = BitmapDescriptorFactory.HUE_ORANGE; // Medium risk = orange
                    } else {
                        hue = BitmapDescriptorFactory.HUE_GREEN; // Low risk = green
                    }
                }
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hue));
                markerOptions.title(item.getTitle());
                markerOptions.snippet(item.getSnippet());
            }
        });

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterItemClickListener(item -> {
            if (callback != null) {
                callback.onMarkerClick(item);
            }
            return false;
        });

        // Set default location (Rwanda)
        LatLng rwanda = new LatLng(-1.9403, 29.8739);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rwanda, 7f));

        // Enable map controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // We have custom FAB

        // Load map data
        loadMapData();

        if (callback != null) {
            callback.onMapReady();
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Set risk level filters
     */
    public void setRiskFilters(boolean highRisk, boolean mediumRisk, boolean lowRisk) {
        this.highRiskEnabled = highRisk;
        this.mediumRiskEnabled = mediumRisk;
        this.lowRiskEnabled = lowRisk;
        applyFilters();
    }

    /**
     * Apply current filters to markers
     */
    private void applyFilters() {
        if (clusterManager == null || googleMap == null)
            return;

        mainHandler.post(() -> {
            // Clear current markers
            clusterManager.clearItems();

            // Add markers that match current filters
            int visibleCount = 0;
            for (CaseMarker marker : allMarkers) {
                if (shouldShowMarker(marker)) {
                    clusterManager.addItem(marker);
                    visibleCount++;
                }
            }

            // Update clusters
            clusterManager.cluster();
            Log.d(TAG, "Applied filters - showing " + visibleCount + " of " + allMarkers.size() + " markers");
        });
    }

    /**
     * Check if marker should be shown based on current filters
     */
    private boolean shouldShowMarker(CaseMarker marker) {
        if (marker == null || marker.getRiskLevel() == null) {
            return lowRiskEnabled; // Default to low risk if unknown
        }

        String risk = marker.getRiskLevel().toLowerCase();
        if (risk.contains("high") || risk.contains("severe")) {
            return highRiskEnabled;
        } else if (risk.contains("medium") || risk.contains("moderate")) {
            return mediumRiskEnabled;
        } else {
            return lowRiskEnabled;
        }
    }

    /**
     * Get count of visible markers
     */
    private int getVisibleMarkerCount() {
        int count = 0;
        for (CaseMarker marker : allMarkers) {
            if (shouldShowMarker(marker)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Debug method to check marker status
     */
    public void debugMarkerStatus() {
        Log.d(TAG, "=== Marker Debug Info ===");
        Log.d(TAG, "Total markers stored: " + allMarkers.size());
        Log.d(TAG, "High risk enabled: " + highRiskEnabled);
        Log.d(TAG, "Medium risk enabled: " + mediumRiskEnabled);
        Log.d(TAG, "Low risk enabled: " + lowRiskEnabled);
        Log.d(TAG, "Visible markers: " + getVisibleMarkerCount());
        Log.d(TAG, "ClusterManager null: " + (clusterManager == null));
        Log.d(TAG, "GoogleMap null: " + (googleMap == null));

        for (int i = 0; i < Math.min(allMarkers.size(), 5); i++) {
            CaseMarker marker = allMarkers.get(i);
            Log.d(TAG, "Marker " + i + ": " + marker.getTitle() + " at " + marker.getPosition() + " risk: "
                    + marker.getRiskLevel());
        }
    }

    public void loadMapData() {
        if (fhirService == null) {
            Log.e(TAG, "FHIRService is null");
            return;
        }

        // Ensure map is ready before loading data
        if (googleMap == null || clusterManager == null) {
            Log.w(TAG, "Map not ready yet, will retry when map is initialized");
            // Retry after a short delay
            mainHandler.postDelayed(() -> {
                if (googleMap != null && clusterManager != null) {
                    loadMapData();
                }
            }, 500);
            return;
        }

        // Clear existing markers
        allMarkers.clear();
        mainHandler.post(() -> {
            if (clusterManager != null) {
                clusterManager.clearItems();
            }
        });

        // Load Offline Data First
        loadOfflineMarkers();

        Log.d(TAG, "Starting to load map data - using Observation-based method for Risk accuracy...");

        // REVERTED NEW APPROACH: The direct Location fetch doesn't get Risk Level data.
        // We must use the Observation -> Encounter -> Location chain to correctly
        // color-code markers.
        loadMapDataFromObservations();

        /*
         * // Logic below disabled to ensure filters work correctly
         * executorService.execute(() -> {
         * fhirService.getAllLocations(new FHIRService.LocationsCallback() {
         * ...
         * });
         * });
         */
    }

    /**
     * Fallback method: Load map data through Observations → Encounters → Locations
     * This is the original method, kept as fallback
     */
    private void loadMapDataFromObservations() {
        if (fhirService == null) {
            Log.e(TAG, "FHIRService is null");
            return;
        }

        // Step 1: Fetch observations from FHIR (on background thread)
        executorService.execute(() -> {
            fhirService.getAllObservations(new FHIRService.ObservationsCallback() {
                @Override
                public void onSuccess(FHIRBundle bundle) {
                    if (googleMap == null || clusterManager == null) {
                        Log.w(TAG, "Map not ready when observations arrived, aborting");
                        return;
                    }

                    if (bundle == null || bundle.getEntry() == null || bundle.getEntry().isEmpty()) {
                        Log.w(TAG,
                                "No observations found in FHIR server. Make sure you have submitted disease reports.");
                        mainHandler.post(() -> {
                            // Show user-friendly message if possible
                            Log.i(TAG, "No outbreak data available. Submit reports to see them on the map.");
                        });
                        return;
                    }

                    Log.d(TAG, "Found " + bundle.getEntry().size() + " total entries in bundle");

                    // Extract observations and their encounter references
                    // Filter to only include disease reports (not lab results or test data)
                    List<ObservationData> observationDataList = new ArrayList<>();
                    int skippedNoEncounter = 0;
                    int skippedUuidRef = 0;
                    int skippedLabResult = 0;

                    for (FHIRBundle.Entry entry : bundle.getEntry()) {
                        if (entry.getResource() instanceof FHIRObservation) {
                            FHIRObservation obs = (FHIRObservation) entry.getResource();

                            // Filter: Skip lab results (LOINC codes) - we only want disease reports
                            boolean isLabResult = false;
                            if (obs.getCode() != null && obs.getCode().getCoding() != null) {
                                for (FHIRObservation.Coding coding : obs.getCode().getCoding()) {
                                    if (coding.getSystem() != null && coding.getSystem().contains("loinc.org")) {
                                        isLabResult = true;
                                        skippedLabResult++;
                                        break;
                                    }
                                }
                            }
                            if (isLabResult) {
                                continue; // Skip lab results
                            }

                            // Extract encounter reference
                            String encounterRef = null;
                            if (obs.getEncounter() != null && obs.getEncounter().getReference() != null) {
                                String ref = obs.getEncounter().getReference();

                                // Skip urn:uuid: references - these are from test data and can't be fetched via
                                // GET /Encounter/{id}
                                if (ref.startsWith("urn:uuid:")) {
                                    skippedUuidRef++;
                                    continue;
                                }

                                // Only process Encounter/... format references
                                if (ref.startsWith("Encounter/")) {
                                    encounterRef = ref.substring("Encounter/".length());
                                } else if (ref.startsWith("Encounter")) {
                                    // Handle case where it's just "Encounter" followed by ID
                                    encounterRef = ref.replace("Encounter", "").replace("/", "").trim();
                                } else {
                                    // Unknown format, skip
                                    skippedUuidRef++;
                                    continue;
                                }
                            }

                            if (encounterRef == null || encounterRef.isEmpty()) {
                                skippedNoEncounter++;
                                continue;
                            }

                            // Extract disease type
                            String diseaseType = "Unknown";
                            if (obs.getCode() != null && obs.getCode().getText() != null) {
                                diseaseType = obs.getCode().getText();
                            } else if (obs.getCode() != null && obs.getCode().getCoding() != null
                                    && !obs.getCode().getCoding().isEmpty()) {
                                // Fallback to display name from coding
                                diseaseType = obs.getCode().getCoding().get(0).getDisplay();
                                if (diseaseType == null || diseaseType.isEmpty()) {
                                    diseaseType = obs.getCode().getCoding().get(0).getCode();
                                }
                            }

                            // Extract risk level from observation value
                            String riskLevel = "low";
                            String severity = "";

                            if (obs.getValueCodeableConcept() != null
                                    && obs.getValueCodeableConcept().getText() != null) {
                                severity = obs.getValueCodeableConcept().getText().toLowerCase();
                            } else if (obs.getValueString() != null) {
                                severity = obs.getValueString().toLowerCase();
                            }

                            if (severity.contains("severe") || severity.contains("high")) {
                                riskLevel = "high";
                            } else if (severity.contains("moderate") || severity.contains("medium")) {
                                riskLevel = "medium";
                            } else {
                                riskLevel = "low";
                            }

                            observationDataList
                                    .add(new ObservationData(obs.getId(), encounterRef, diseaseType, riskLevel));
                        }
                    }

                    Log.d(TAG, "Filtered observations: " + observationDataList.size() + " valid disease reports");
                    Log.d(TAG, "  - Skipped (no encounter): " + skippedNoEncounter);
                    Log.d(TAG, "  - Skipped (urn:uuid refs): " + skippedUuidRef);
                    Log.d(TAG, "  - Skipped (lab results): " + skippedLabResult);

                    if (observationDataList.isEmpty()) {
                        Log.w(TAG, "No observations have encounter references. Reports need to include location data.");
                        return;
                    }

                    // Step 2: Fetch encounters and locations in parallel (not recursive)
                    fetchEncountersAndLocationsParallel(observationDataList);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error loading observations: " + error);
                }
            });
        });
    }

    /**
     * Helper class to store observation data
     */
    private static class ObservationData {
        String observationId;
        String encounterRef;
        String diseaseType;
        String riskLevel;

        ObservationData(String observationId, String encounterRef, String diseaseType, String riskLevel) {
            this.observationId = observationId;
            this.encounterRef = encounterRef;
            this.diseaseType = diseaseType;
            this.riskLevel = riskLevel;
        }
    }

    /**
     * Fetch encounters and locations in parallel (non-blocking)
     */
    private void fetchEncountersAndLocationsParallel(List<ObservationData> observationDataList) {
        if (observationDataList == null || observationDataList.isEmpty()) {
            Log.d(TAG, "No observations to process");
            return;
        }

        final int[] completedCount = { 0 };
        final int totalCount = observationDataList.size();

        for (ObservationData obsData : observationDataList) {
            executorService.execute(() -> {
                try {
                    // Fetch encounter
                    fhirService.getEncounterById(obsData.encounterRef, new FHIRService.EncounterCallback() {
                        @Override
                        public void onSuccess(FHIREncounter encounter) {
                            // Extract location reference from encounter
                            String locationRef = null;
                            if (encounter.getLocation() != null && !encounter.getLocation().isEmpty()) {
                                FHIREncounter.EncounterLocation encLocation = encounter.getLocation().get(0);
                                if (encLocation.getLocation() != null
                                        && encLocation.getLocation().getReference() != null) {
                                    String ref = encLocation.getLocation().getReference();
                                    // Remove "Location/" prefix if present
                                    if (ref.startsWith("Location/")) {
                                        locationRef = ref.substring("Location/".length());
                                    } else {
                                        locationRef = ref;
                                    }
                                }
                            }

                            final String finalLocationRef = locationRef;

                            if (finalLocationRef != null && !finalLocationRef.isEmpty()) {
                                // Fetch location
                                fhirService.getLocationById(finalLocationRef, new FHIRService.LocationCallback() {
                                    @Override
                                    public void onSuccess(FHIRLocation location) {
                                        // Extract coordinates
                                        if (location.getPosition() != null &&
                                                location.getPosition().getLatitude() != null &&
                                                location.getPosition().getLongitude() != null) {

                                            double lat = location.getPosition().getLatitude();
                                            double lng = location.getPosition().getLongitude();

                                            // Validate coordinates
                                            if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                                                // Create marker
                                                CaseMarker marker = new CaseMarker(
                                                        lat,
                                                        lng,
                                                        obsData.diseaseType,
                                                        obsData.observationId,
                                                        obsData.riskLevel);

                                                // Add to all markers list
                                                synchronized (allMarkers) {
                                                    allMarkers.add(marker);
                                                }

                                                // Add to cluster manager on main thread if it matches filter
                                                mainHandler.post(() -> {
                                                    if (clusterManager != null && googleMap != null
                                                            && shouldShowMarker(marker)) {
                                                        clusterManager.addItem(marker);
                                                        Log.d(TAG, "✓ Added marker for " + obsData.diseaseType + " ("
                                                                + obsData.riskLevel + ") at " + lat + ", " + lng);
                                                    } else {
                                                        Log.d(TAG, "✗ Marker filtered out: " + obsData.diseaseType
                                                                + " (" + obsData.riskLevel + ")");
                                                    }

                                                    // Check if all done
                                                    synchronized (completedCount) {
                                                        completedCount[0]++;
                                                        Log.d(TAG, "Progress: " + completedCount[0] + "/" + totalCount
                                                                + " observations processed");
                                                        if (completedCount[0] >= totalCount) {
                                                            // All done, cluster markers
                                                            if (clusterManager != null && googleMap != null) {
                                                                clusterManager.cluster();
                                                                Log.d(TAG,
                                                                        "✓ Finished processing all " + totalCount
                                                                                + " observations. Total markers: "
                                                                                + allMarkers.size() + ", Visible: "
                                                                                + getVisibleMarkerCount());

                                                                // Force map update
                                                                googleMap.setOnCameraIdleListener(clusterManager);

                                                                // Debug info
                                                                debugMarkerStatus();
                                                            }
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.w(TAG, "Invalid coordinates: lat=" + lat + ", lng=" + lng);
                                                synchronized (completedCount) {
                                                    completedCount[0]++;
                                                }
                                            }
                                        } else {
                                            Log.w(TAG, "Location " + finalLocationRef + " has no position data");
                                            synchronized (completedCount) {
                                                completedCount[0]++;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.w(TAG, "Failed to fetch location " + finalLocationRef + ": " + error);
                                        synchronized (completedCount) {
                                            completedCount[0]++;
                                            if (completedCount[0] >= totalCount) {
                                                mainHandler.post(() -> {
                                                    if (clusterManager != null) {
                                                        clusterManager.cluster();
                                                        Log.d(TAG, "Finished processing (with errors). Total markers: "
                                                                + allMarkers.size());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            } else {
                                Log.w(TAG, "No location reference found in encounter " + obsData.encounterRef);
                                synchronized (completedCount) {
                                    completedCount[0]++;
                                    if (completedCount[0] >= totalCount) {
                                        mainHandler.post(() -> {
                                            if (clusterManager != null) {
                                                clusterManager.cluster();
                                                Log.d(TAG,
                                                        "Finished processing (some missing locations). Total markers: "
                                                                + allMarkers.size());
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.w(TAG, "Failed to fetch encounter " + obsData.encounterRef + ": " + error);
                            synchronized (completedCount) {
                                completedCount[0]++;
                                if (completedCount[0] >= totalCount) {
                                    mainHandler.post(() -> {
                                        if (clusterManager != null) {
                                            clusterManager.cluster();
                                            Log.d(TAG, "Finished processing (with encounter errors). Total markers: "
                                                    + allMarkers.size());
                                        }
                                    });
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error processing observation " + obsData.encounterRef, e);
                    synchronized (completedCount) {
                        completedCount[0]++;
                    }
                }
            });
        }
    }

    private String getRiskLevel(DiseaseReport report) {
        if (report.getRiskAssessment() != null && report.getRiskAssessment().getLevel() != null) {
            return report.getRiskAssessment().getLevel();
        }
        return "low";
    }

    public static class CaseMarker implements com.google.maps.android.clustering.ClusterItem {
        private final LatLng position;
        private final String title;
        private final String snippet;
        private final String riskLevel;

        public CaseMarker(double latitude, double longitude, String diseaseType, String reportId, String riskLevel) {
            this.position = new LatLng(latitude, longitude);
            this.title = diseaseType != null ? diseaseType : "Disease Case";
            this.snippet = reportId != null ? "Report: " + reportId : "";
            this.riskLevel = riskLevel != null ? riskLevel : "low";
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSnippet() {
            return snippet;
        }

        public String getRiskLevel() {
            return riskLevel;
        }

        @Override
        public Float getZIndex() {
            return null;
        }
    }

    /**
     * Load a single Location by ID and add marker to map
     * This is called after a new Location is saved to immediately show it on the
     * map
     */
    public void loadLocationById(String locationId, String diseaseType, String riskLevel) {
        if (fhirService == null || locationId == null || locationId.isEmpty()) {
            Log.e(TAG, "Cannot load location: fhirService is null or locationId is empty");
            return;
        }

        // Ensure map is ready - if not, retry after delay
        if (googleMap == null || clusterManager == null) {
            Log.w(TAG, "Map not ready yet, will retry after map is initialized");
            mainHandler.postDelayed(() -> {
                if (googleMap != null && clusterManager != null) {
                    loadLocationById(locationId, diseaseType, riskLevel);
                }
            }, 500);
            return;
        }

        // Special handling for Offline Reports
        if ("PENDING-LOC".equals(locationId)) {
            Log.d(TAG, "Loading pending local location...");
            executorService.execute(() -> {
                try {
                    com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                            .getDatabase(context).unsyncedReportDao();
                    com.healthtracker.chw.data.local.UnsyncedReport report = dao.getLatestReport();

                    if (report != null && report.latitude != null && report.longitude != null) {
                        mainHandler.post(() -> {
                            addMarkerAndZoom(report.latitude, report.longitude, report.diseaseType + " (Offline)",
                                    "PENDING", report.severity);
                        });
                    } else {
                        Log.w(TAG, "No pending local report found with valid location");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading pending location", e);
                }
            });
            return;
        }

        executorService.execute(() -> {
            fhirService.getLocationById(locationId, new FHIRService.LocationCallback() {
                @Override
                public void onSuccess(FHIRLocation location) {
                    if (location == null) {
                        Log.w(TAG, "Location " + locationId + " is null");
                        return;
                    }

                    // Parse position coordinates
                    if (location.getPosition() != null &&
                            location.getPosition().getLatitude() != null &&
                            location.getPosition().getLongitude() != null) {

                        double lat = location.getPosition().getLatitude();
                        double lng = location.getPosition().getLongitude();

                        // Validate coordinates
                        if (lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
                            String markerDiseaseType = diseaseType != null ? diseaseType : "Disease Case";
                            String markerRiskLevel = riskLevel != null ? riskLevel : "low";

                            mainHandler.post(() -> {
                                addMarkerAndZoom(lat, lng, markerDiseaseType, locationId, markerRiskLevel);
                            });
                        } else {
                            Log.w(TAG,
                                    "Invalid coordinates for location " + locationId + ": lat=" + lat + ", lng=" + lng);
                        }
                    } else {
                        Log.w(TAG, "Location " + locationId + " has no position data");
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to fetch location " + locationId + ": " + error);
                }
            });
        });
    }

    private void addMarkerAndZoom(double lat, double lng, String title, String snippet, String riskLevel) {
        if (googleMap == null || clusterManager == null)
            return;

        CaseMarker marker = new CaseMarker(lat, lng, title, snippet, riskLevel);

        // Add to all markers list to persistence
        synchronized (allMarkers) {
            boolean exists = false;
            for (CaseMarker existing : allMarkers) {
                if (existing.getPosition().latitude == lat &&
                        existing.getPosition().longitude == lng &&
                        existing.getTitle().equals(title)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                allMarkers.add(marker);
            }
        }

        if (shouldShowMarker(marker)) {
            clusterManager.addItem(marker);
            clusterManager.cluster();
            Log.d(TAG, "✓ Added new marker & Zooming to " + lat + ", " + lng);

            // ZOOM LOGIC
            try {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15f));
            } catch (Exception e) {
                Log.e(TAG, "Error animating camera", e);
            }
        }
    }

    /**
     * Load Offline Reports from Room Database and add to map
     */
    private void loadOfflineMarkers() {
        executorService.execute(() -> {
            try {
                com.healthtracker.chw.data.local.UnsyncedReportDao dao = com.healthtracker.chw.data.local.AppDatabase
                        .getDatabase(context).unsyncedReportDao();
                java.util.List<com.healthtracker.chw.data.local.UnsyncedReport> localReports = dao.getAllReports();

                if (localReports != null && !localReports.isEmpty()) {
                    Log.d(TAG, "Found " + localReports.size() + " offline reports to add to map");

                    for (com.healthtracker.chw.data.local.UnsyncedReport report : localReports) {
                        if (report.latitude != null && report.longitude != null) {
                            CaseMarker marker = new CaseMarker(
                                    report.latitude,
                                    report.longitude,
                                    report.diseaseType + " (Offline)",
                                    "PENDING",
                                    report.severity);

                            synchronized (allMarkers) {
                                allMarkers.add(marker);
                            }

                            mainHandler.post(() -> {
                                if (clusterManager != null && googleMap != null) {
                                    if (shouldShowMarker(marker)) {
                                        clusterManager.addItem(marker);
                                    }
                                }
                            });
                        }
                    }

                    mainHandler.post(() -> {
                        if (clusterManager != null)
                            clusterManager.cluster();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading offline markers", e);
            }
        });
    }

    /**
     * Reload all map data (useful after new submissions)
     */
    public void refreshMapData() {
        loadMapData();
    }

    /**
     * Check for pending locations stored by MapFragment
     */
    private void checkForPendingLocations() {
        try {
            android.content.SharedPreferences prefs = context.getSharedPreferences("map_refresh",
                    android.content.Context.MODE_PRIVATE);
            String pendingLocationId = prefs.getString("pending_location_id", null);
            String pendingDiseaseType = prefs.getString("pending_disease_type", null);
            String pendingRiskLevel = prefs.getString("pending_risk_level", null);

            if (pendingLocationId != null && !pendingLocationId.isEmpty() && googleMap != null
                    && clusterManager != null) {
                Log.d(TAG, "Found pending location after map ready: " + pendingLocationId);
                loadLocationById(pendingLocationId, pendingDiseaseType, pendingRiskLevel);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking for pending locations", e);
        }
    }

    public interface MapCallback {
        void onMarkerClick(CaseMarker marker);

        void onMapReady();
    }
}
