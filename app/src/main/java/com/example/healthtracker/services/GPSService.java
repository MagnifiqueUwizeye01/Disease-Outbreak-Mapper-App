package com.example.healthtracker.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GPSService {

    public interface Callback {
        void onLocation(String isoTimestamp, double lat, double lon, String address);
        void onError(String reason);
    }

    private final FusedLocationProviderClient fusedClient;
    private final Activity activity;
    private final int REQUEST_CODE = 1234;

    public GPSService(Activity activity) {
        this.activity = activity;
        fusedClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void requestLocation(Callback cb) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // ask for permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE);
            cb.onError("permission_required");
            return;
        }

        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                String iso = toIso8601(new Date());
                cb.onLocation(iso, location.getLatitude(), location.getLongitude(), null);
            } else {
                // fallback: request single update or notify error
                cb.onError("no_location");
            }
        }).addOnFailureListener(e -> cb.onError(e.getMessage()));
    }

    private String toIso8601(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        return sdf.format(d);
    }
}
