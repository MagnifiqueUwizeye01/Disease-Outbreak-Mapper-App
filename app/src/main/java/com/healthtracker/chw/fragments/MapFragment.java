package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.healthtracker.chw.R;
import com.healthtracker.chw.map.MapModule;

/**
 * Map fragment displaying outbreak data with clustering and filtering
 */
public class MapFragment extends Fragment {
    private MapModule mapModule;
    private SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        
        // Initialize map fragment
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commitNow();
        }
        
        // Wait for view to be ready before initializing map module
        view.post(() -> {
            if (getView() != null && mapFragment != null) {
                initializeMapModule();
            }
        });

        return view;
    }

    private void initializeMapModule() {
        if (mapFragment == null || getView() == null) return;

        // Initialize MapModule
        mapModule = new MapModule(requireContext(), mapFragment, new MapModule.MapCallback() {
            @Override
            public void onMarkerClick(MapModule.CaseMarker marker) {
                // Handle marker click - could show details dialog
            }

            @Override
            public void onMapReady() {
                // Map is ready
            }
        });

        // Setup filter chips
        setupFilterChips(getView());

        // Setup FABs
        setupFABs(getView());
    }

    private void setupFilterChips(View view) {
        Chip chipHighRisk = view.findViewById(R.id.chip_high_risk);
        Chip chipMediumRisk = view.findViewById(R.id.chip_medium_risk);
        Chip chipLowRisk = view.findViewById(R.id.chip_low_risk);

        if (chipHighRisk != null) {
            chipHighRisk.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mapModule != null) {
                    // Filter by risk level
                    // Implementation depends on backend API
                }
            });
        }

        if (chipMediumRisk != null) {
            chipMediumRisk.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mapModule != null) {
                    // Filter by risk level
                }
            });
        }

        if (chipLowRisk != null) {
            chipLowRisk.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (mapModule != null) {
                    // Filter by risk level
                }
            });
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
}
