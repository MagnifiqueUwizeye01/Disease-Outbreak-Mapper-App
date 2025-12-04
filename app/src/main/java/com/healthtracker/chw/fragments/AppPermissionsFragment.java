package com.healthtracker.chw.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.healthtracker.chw.R;

public class AppPermissionsFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 102;

    private MaterialCardView locationCard;
    private MaterialCardView cameraCard;
    private MaterialCardView storageCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_permissions, container, false);
        
        // Find permission cards
        ViewGroup scrollView = (ViewGroup) view;
        ViewGroup root = (ViewGroup) scrollView.getChildAt(0);
        if (root instanceof LinearLayout) {
            LinearLayout linearRoot = (LinearLayout) root;
            // Location card is at index 2 (after header text views)
            if (linearRoot.getChildCount() > 2) {
                locationCard = (MaterialCardView) linearRoot.getChildAt(2);
            }
            // Camera card is at index 3
            if (linearRoot.getChildCount() > 3) {
                cameraCard = (MaterialCardView) linearRoot.getChildAt(3);
            }
            // Storage card is at index 4
            if (linearRoot.getChildCount() > 4) {
                storageCard = (MaterialCardView) linearRoot.getChildAt(4);
            }
        }
        
        // Set click listeners
        if (locationCard != null) {
            locationCard.setOnClickListener(v -> requestLocationPermission());
        }
        if (cameraCard != null) {
            cameraCard.setOnClickListener(v -> requestCameraPermission());
        }
        if (storageCard != null) {
            storageCard.setOnClickListener(v -> requestStoragePermission());
        }
        
        // Update permission status
        updatePermissionStatus();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePermissionStatus();
    }

    private void updatePermissionStatus() {
        updateLocationPermissionStatus();
        updateCameraPermissionStatus();
        updateStoragePermissionStatus();
    }

    private void updateLocationPermissionStatus() {
        if (locationCard == null) return;
        
        boolean granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        
        updateCardStatus(locationCard, granted);
    }

    private void updateCameraPermissionStatus() {
        if (cameraCard == null) return;
        
        boolean granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        
        updateCardStatus(cameraCard, granted);
    }

    private void updateStoragePermissionStatus() {
        if (storageCard == null) return;
        
        boolean granted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses different storage permissions
            granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        
        updateCardStatus(storageCard, granted);
    }

    private void updateCardStatus(MaterialCardView card, boolean granted) {
        if (card == null) return;
        
        // Change card appearance based on permission status
        if (granted) {
            card.setAlpha(1.0f);
            // You could add a checkmark icon or change color here
        } else {
            card.setAlpha(0.7f);
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location permission already granted", Toast.LENGTH_SHORT).show();
            return;
        }
        
        requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Camera permission already granted", Toast.LENGTH_SHORT).show();
            return;
        }
        
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Storage permission already granted", Toast.LENGTH_SHORT).show();
                return;
            }
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            // Android 12 and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Storage permission already granted", Toast.LENGTH_SHORT).show();
                return;
            }
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
                    updateLocationPermissionStatus();
                    break;
                case CAMERA_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show();
                    updateCameraPermissionStatus();
                    break;
                case STORAGE_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Storage permission granted", Toast.LENGTH_SHORT).show();
                    updateStoragePermissionStatus();
                    break;
            }
        } else {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                    break;
                case CAMERA_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    break;
                case STORAGE_PERMISSION_REQUEST_CODE:
                    Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
