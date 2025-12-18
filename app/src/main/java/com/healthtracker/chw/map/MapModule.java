package com.healthtracker.chw.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.RiskAssessment;
import com.healthtracker.chw.services.SupabaseService;

import java.util.List;

/**
 * Module for managing Google Maps with disease outbreak data
 */
public class MapModule {
    private static final String TAG = "MapModule";
    private Context context;
    private GoogleMap googleMap;
    private ClusterManager<CaseMarker> clusterManager;
    private MapCallback callback;
    private SupabaseService supabaseService;
    
    public MapModule(Context context, SupportMapFragment mapFragment, MapCallback callback) {
        this.context = context;
        this.callback = callback;
        this.supabaseService = new SupabaseService(context);
        
        mapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            initializeMap();
        });
    }
    
    private void initializeMap() {
        if (googleMap == null) return;
        
        // Setup cluster manager
        clusterManager = new ClusterManager<>(context, googleMap);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        
        clusterManager.setOnClusterItemClickListener(item -> {
            if (callback != null) {
                callback.onMarkerClick(item);
            }
            return false;
        });
        
        // Set default location (you can change this)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 2));
        
        // Load map data
        loadMapData();
        
        if (callback != null) {
            callback.onMapReady();
        }
    }
    
    public void loadMapData() {
        if (supabaseService == null) {
            Log.e(TAG, "SupabaseService is null");
            return;
        }
        
        supabaseService.getAllDiseaseReports(new SupabaseService.ReportsCallback() {
            @Override
            public void onSuccess(List<DiseaseReport> reports) {
                if (googleMap == null || clusterManager == null) return;
                
                // Clear existing markers
                clusterManager.clearItems();
                
                // Add markers for each report
                for (DiseaseReport report : reports) {
                    if (report.getEncounter() != null && report.getEncounter().getGpsLocation() != null) {
                        GPSLocation location = report.getEncounter().getGpsLocation();
                        if (location.getLatitude() != null && location.getLongitude() != null) {
                            CaseMarker marker = new CaseMarker(
                                location.getLatitude(),
                                location.getLongitude(),
                                report.getDiseaseType(),
                                report.getReportId(),
                                getRiskLevel(report)
                            );
                            clusterManager.addItem(marker);
                        }
                    }
                }
                
                clusterManager.cluster();
                Log.d(TAG, "Loaded " + reports.size() + " disease reports on map");
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading map data: " + error);
            }
        });
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
    
    public interface MapCallback {
        void onMarkerClick(CaseMarker marker);
        void onMapReady();
    }
}

