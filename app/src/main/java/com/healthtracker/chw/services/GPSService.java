package com.healthtracker.chw.services;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Service for capturing GPS location
 */
public class GPSService {
    private static final String TAG = "GPSService";
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationCallback callback;
    
    public GPSService(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public interface LocationCallback {
        void onLocationCaptured(double latitude, double longitude, String address, long timestamp);
        void onLocationError(String error);
    }
    
    public void captureLocation(LocationCallback callback) {
        this.callback = callback;
        
        if (locationManager == null) {
            if (callback != null) {
                callback.onLocationError("Location manager not available");
            }
            return;
        }
        
        try {
            // Check if GPS is enabled
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            
            if (!isGPSEnabled && !isNetworkEnabled) {
                if (callback != null) {
                    callback.onLocationError("GPS and Network location are disabled");
                }
                return;
            }
            
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null && GPSService.this.callback != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        long timestamp = location.getTime();
                        
                        // Get address from coordinates on background thread
                        new Thread(() -> {
                            String address = getAddressFromCoordinates(latitude, longitude);
                            GPSService.this.callback.onLocationCaptured(latitude, longitude, address, timestamp);
                        }).start();
                        
                        // Stop listening after getting location
                        stopLocationUpdates();
                    }
                }
                
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                
                @Override
                public void onProviderEnabled(String provider) {}
                
                @Override
                public void onProviderDisabled(String provider) {
                    if (GPSService.this.callback != null) {
                        GPSService.this.callback.onLocationError("Location provider disabled");
                    }
                    stopLocationUpdates();
                }
            };
            
            // Request location updates
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
            
            // Try to get last known location immediately
            Location lastKnownLocation = null;
            if (isGPSEnabled) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (lastKnownLocation == null && isNetworkEnabled) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            
            if (lastKnownLocation != null && callback != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                long timestamp = lastKnownLocation.getTime();
                
                // Get address from coordinates on background thread
                new Thread(() -> {
                    String address = getAddressFromCoordinates(latitude, longitude);
                    callback.onLocationCaptured(latitude, longitude, address, timestamp);
                }).start();
                
                stopLocationUpdates();
            }
            
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission not granted", e);
            if (callback != null) {
                callback.onLocationError("Location permission not granted");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error capturing location", e);
            if (callback != null) {
                callback.onLocationError("Error: " + e.getMessage());
            }
        }
    }
    
    public void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                Log.e(TAG, "Error stopping location updates", e);
            }
            locationListener = null;
        }
    }
    
    private String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();
                
                // Build address string
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    if (i > 0) addressString.append(", ");
                    addressString.append(address.getAddressLine(i));
                }
                
                return addressString.toString();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting address", e);
        }
        
        return String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
    }
}

