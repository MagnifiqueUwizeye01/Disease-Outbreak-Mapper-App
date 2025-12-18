package com.healthtracker.chw.services;

import android.content.Context;
import android.util.Log;

import com.healthtracker.chw.api.FHIRApiService;
import com.healthtracker.chw.api.FHIRClient;
import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIREncounter;
import com.healthtracker.chw.models.fhir.FHIRLocation;
import com.healthtracker.chw.models.fhir.FHIRObservation;
import com.healthtracker.chw.models.fhir.FHIRPatient;
import com.healthtracker.chw.models.fhir.FHIRRiskAssessment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.healthtracker.chw.data.local.AppDatabase;
import com.healthtracker.chw.data.local.UnsyncedReport;
import com.healthtracker.chw.data.local.UnsyncedReportDao;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service for interacting with FHIR server
 */
public class FHIRService {
    private static final String TAG = "FHIRService";
    private FHIRApiService apiService;
    private Context context;
    private UnsyncedReportDao unsyncedReportDao;

    public FHIRService(Context context) {
        this.context = context;
        try {
            if (context == null)
                throw new IllegalArgumentException("Context cannot be null");
            this.apiService = FHIRClient.getApiService(context);
            this.unsyncedReportDao = AppDatabase.getDatabase(context).unsyncedReportDao();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing FHIRService", e);
        }
    }

    private boolean isNetworkAvailable() {
        android.net.ConnectivityManager connectivityManager = (android.net.ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.NetworkCapabilities capabilities = connectivityManager
                    .getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities == null) {
                Log.d(TAG, "isNetworkAvailable: No active network capabilities");
                return false;
            }

            boolean hasInternet = capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET);
            boolean hasWifi = capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI);
            boolean hasCellular = capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR);
            boolean hasEthernet = capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET);

            Log.d(TAG, "isNetworkAvailable: Internet=" + hasInternet + " Wifi=" + hasWifi + " Cell=" + hasCellular
                    + " Eth=" + hasEthernet);

            return hasInternet;
        }
        return false;
    }

    // --- SAVE LOGIC ---

    public void saveDiseaseReport(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {
        // Default: Do NOT force network (respect isNetworkAvailable)
        saveDiseaseReport(patientName, gender, dateOfBirth, patientAge, chwName, chwId,
                latitude, longitude, address, encounterDate, encounterType,
                diseaseType, symptoms, severity, observationDetails, notes,
                callback, false);
    }

    public void saveDiseaseReport(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback,
            boolean forceNetworkAttempt) { // New parameter

        // If forceNetworkAttempt is TRUE, we skip the network check (unless apiService
        // is null)
        boolean skipCheck = forceNetworkAttempt;

        if ((!skipCheck && !isNetworkAvailable()) || apiService == null) {
            String reason = apiService == null ? "API Service Init Failed" : "No Internet";
            saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                    chwName, chwId, latitude, longitude, address,
                    encounterDate, encounterType, diseaseType, symptoms, severity,
                    observationDetails, notes, callback, reason, true); // true = schedule sync
            return;
        }

        submitReportInternal(patientName, gender, dateOfBirth, patientAge, chwName, chwId,
                latitude, longitude, address, encounterDate, encounterType,
                diseaseType, symptoms, severity, observationDetails, notes, callback, true); // true = allow fallback
    }

    public void submitReportOffline(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {

        saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                chwName, chwId, latitude, longitude, address,
                encounterDate, encounterType, diseaseType, symptoms, severity,
                observationDetails, notes, callback, "Manual Offline Mode", false); // false = DO NOT schedule sync
    }

    /**
     * FORCE SUBMIT method for SyncWorker.
     * Does NOT save locally if it fails (because it's already local!).
     */
    public void submitReportForSync(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {

        // Even if network check fails, we try anyway because SyncWorker checks network
        // constraint.
        // Or we just proceed to internal logic which will fail gracefully via callback.
        submitReportInternal(patientName, gender, dateOfBirth, patientAge, chwName, chwId,
                latitude, longitude, address, encounterDate, encounterType, diseaseType, symptoms,
                severity, observationDetails, notes, callback, false); // false = NO fallback
    }

    private void submitReportInternal(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback, boolean allowFallback) {

        new Thread(() -> {
            try {
                if (patientName == null || diseaseType == null)
                    throw new IllegalArgumentException("Missing required fields");

                String patientId = "patient-" + UUID.randomUUID().toString();
                String locationId = "location-" + UUID.randomUUID().toString();
                String encounterId = "encounter-" + UUID.randomUUID().toString();
                String observationId = "observation-" + UUID.randomUUID().toString();
                String riskAssessmentId = "riskassessment-" + UUID.randomUUID().toString();

                String formattedBirthDate = formatDate(dateOfBirth);
                String formattedEncounterDate = formatDateTime(encounterDate);
                String formattedEffectiveDateTime = formatDateTimeISO(new Date());

                ErrorCallback errorHandler = (error) -> {
                    Log.e(TAG, "API Error: " + error);
                    if (allowFallback) {
                        Log.i(TAG, "Saving locally due to error.");
                        saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                                chwName, chwId, latitude, longitude, address,
                                encounterDate, encounterType, diseaseType, symptoms, severity,
                                observationDetails, notes, callback, "API Error: " + error, true);
                    } else {
                        if (callback != null)
                            callback.onError(error);
                    }
                };

                FHIRPatient patient = createFHIRPatient(patientId, patientName, gender, formattedBirthDate);
                savePatient(patient, (savedPatient) -> {
                    String finalPatientId = savedPatient.getId() != null ? savedPatient.getId() : patientId;
                    String patientReference = "Patient/" + finalPatientId;

                    FHIRLocation location = createFHIRLocation(locationId, address, latitude, longitude);
                    saveLocation(location, (savedLocation) -> {
                        String finalLocationId = savedLocation.getId() != null ? savedLocation.getId() : locationId;
                        String locationReference = "Location/" + finalLocationId;

                        FHIREncounter encounter = createFHIREncounter(encounterId, encounterType,
                                patientReference, locationReference, formattedEncounterDate);
                        saveEncounter(encounter, (savedEncounter) -> {
                            String finalEncounterId = savedEncounter.getId() != null ? savedEncounter.getId()
                                    : encounterId;
                            String encounterReference = "Encounter/" + finalEncounterId;

                            FHIRObservation observation = createFHIRObservation(observationId, diseaseType,
                                    patientReference, encounterReference, formattedEffectiveDateTime,
                                    severity, symptoms, observationDetails);
                            saveObservation(observation, (savedObservation) -> {
                                String finalObservationId = savedObservation.getId() != null ? savedObservation.getId()
                                        : observationId;

                                String riskDescription = buildRiskDescription(severity, symptoms, notes);
                                FHIRRiskAssessment riskAssessment = createFHIRRiskAssessment(riskAssessmentId,
                                        patientReference, encounterReference, severity, riskDescription);

                                saveRiskAssessment(riskAssessment, (savedRisk) -> {
                                    if (callback != null)
                                        callback.onSuccess(finalObservationId, finalLocationId);
                                }, (e) -> {
                                    if (callback != null)
                                        callback.onSuccess(finalObservationId, finalLocationId);
                                });
                            }, errorHandler);
                        }, errorHandler);
                    }, errorHandler);
                }, errorHandler);

            } catch (Exception e) {
                Log.e(TAG, "Error submitting report", e);
                if (allowFallback) {
                    saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId, latitude, longitude, address,
                            encounterDate, encounterType, diseaseType, symptoms, severity,
                            observationDetails, notes, callback, "Exception: " + e.getMessage(), true);
                } else {
                    if (callback != null)
                        callback.onError(e.getMessage());
                }
            }
        }).start();
    }

    private void saveReportLocally(String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, Double latitude, Double longitude, String address,
            String encounterDate, String encounterType, String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes, SaveCallback callback, String reason, boolean scheduleSync) { // Added
                                                                                                                   // scheduleSync

        new Thread(() -> {
            try {
                UnsyncedReport report = new UnsyncedReport();
                report.patientName = patientName;
                report.gender = gender;
                report.dateOfBirth = dateOfBirth;
                report.patientAge = patientAge;
                report.chwName = chwName;
                report.chwId = chwId;
                report.latitude = latitude;
                report.longitude = longitude;
                report.address = address;
                report.encounterDate = encounterDate;
                report.encounterType = encounterType;
                report.diseaseType = diseaseType;
                report.symptomsJson = new Gson().toJson(symptoms);
                report.severity = severity;
                report.observationDetails = observationDetails;
                report.notes = notes;

                unsyncedReportDao.insert(report);

                // Only enqueue if scheduleSync is true
                if (scheduleSync) {
                    OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                            .setConstraints(new androidx.work.Constraints.Builder()
                                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED).build())
                            .build();
                    WorkManager.getInstance(context).enqueue(syncRequest);
                }

                if (callback != null)
                    callback.onSuccess("PENDING-SYNC:" + reason, "PENDING-LOC"); // Pass reason
            } catch (Exception e) {
                Log.e(TAG, "Error saving locally", e);
                if (callback != null)
                    callback.onError("Failed to save report locally: " + e.getMessage());
            }
        }).start();
    }

    // --- READ LOGIC (Restored) ---

    // Copied from previous state...
    public void getAllObservations(ObservationsCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.searchObservations(null, null, null, 1000).enqueue(new Callback<FHIRBundle>() {
            public void onResponse(Call<FHIRBundle> call, Response<FHIRBundle> response) {
                if (response.isSuccessful())
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            public void onFailure(Call<FHIRBundle> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllLocations(LocationsCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.searchLocations(1000).enqueue(new Callback<FHIRBundle>() {
            public void onResponse(Call<FHIRBundle> call, Response<FHIRBundle> response) {
                if (response.isSuccessful())
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            public void onFailure(Call<FHIRBundle> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getRiskAssessments(RiskAssessmentsCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.searchRiskAssessments(null, null, 1000).enqueue(new Callback<FHIRBundle>() {
            public void onResponse(Call<FHIRBundle> call, Response<FHIRBundle> response) {
                if (response.isSuccessful())
                    callback.onSuccess(response.body());
                else
                    callback.onError("Error: " + response.code());
            }

            public void onFailure(Call<FHIRBundle> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getLocationById(String id, LocationCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.getLocation(id).enqueue(new Callback<FHIRLocation>() {
            public void onResponse(Call<FHIRLocation> c, Response<FHIRLocation> r) {
                if (r.isSuccessful())
                    callback.onSuccess(r.body());
                else
                    callback.onError("Error: " + r.code());
            }

            public void onFailure(Call<FHIRLocation> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getEncounterById(String id, EncounterCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.getEncounter(id).enqueue(new Callback<FHIREncounter>() {
            public void onResponse(Call<FHIREncounter> c, Response<FHIREncounter> r) {
                if (r.isSuccessful())
                    callback.onSuccess(r.body());
                else
                    callback.onError("Error: " + r.code());
            }

            public void onFailure(Call<FHIREncounter> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getObservationById(String id, ObservationCallback callback) {
        if (apiService == null) {
            callback.onError("API service not initialized");
            return;
        }
        apiService.getObservation(id).enqueue(new Callback<FHIRObservation>() {
            public void onResponse(Call<FHIRObservation> c, Response<FHIRObservation> r) {
                if (r.isSuccessful())
                    callback.onSuccess(r.body());
                else
                    callback.onError("Error: " + r.code());
            }

            public void onFailure(Call<FHIRObservation> c, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // --- HELPER METHODS & CLASSES ---

    private String formatDate(String d) {
        return d;
    }

    private String formatDateTime(String dt) {
        return dt != null ? dt : formatDateTimeISO(new Date());
    }

    private String formatDateTimeISO(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(d);
    }

    private String buildRiskDescription(String severity, List<String> symptoms, String notes) {
        return "Severity: " + severity + ". Notes: " + notes;
    }

    private FHIRPatient createFHIRPatient(String id, String name, String gender, String birthDate) {
        return new FHIRPatient(id, name, gender != null ? gender : "unknown", birthDate);
    }

    private FHIRLocation createFHIRLocation(String id, String address, Double lat, Double lon) {
        return new FHIRLocation(id, "GPS Location", address, lat, lon, address);
    }

    private FHIREncounter createFHIREncounter(String id, String type, String patRef, String locRef, String date) {
        return new FHIREncounter(id, "finished", type != null ? type : "home", patRef, locRef, date);
    }

    private FHIRObservation createFHIRObservation(String id, String disease, String patRef, String encRef, String date,
            String sev, List<String> sym, String det) {
        return new FHIRObservation(id, "final", disease, patRef, encRef, date, sev, sym, det);
    }

    private FHIRRiskAssessment createFHIRRiskAssessment(String id, String patRef, String encRef, String level,
            String expl) {
        return new FHIRRiskAssessment(id, "final", patRef, encRef, level, expl);
    }

    // --- INTERNAL SAVE WRAPPERS ---
    private void savePatient(FHIRPatient p, ResourceCallback<FHIRPatient> cb, ErrorCallback eb) {
        apiService.createPatient(p).enqueue(new Callback<FHIRPatient>() {
            public void onResponse(Call<FHIRPatient> c, Response<FHIRPatient> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else
                    eb.onError("HTTP " + r.code());
            }

            public void onFailure(Call<FHIRPatient> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    private void saveLocation(FHIRLocation l, ResourceCallback<FHIRLocation> cb, ErrorCallback eb) {
        apiService.createLocation(l).enqueue(new Callback<FHIRLocation>() {
            public void onResponse(Call<FHIRLocation> c, Response<FHIRLocation> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else
                    eb.onError("HTTP " + r.code());
            }

            public void onFailure(Call<FHIRLocation> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    private void saveEncounter(FHIREncounter e, ResourceCallback<FHIREncounter> cb, ErrorCallback eb) {
        apiService.createEncounter(e).enqueue(new Callback<FHIREncounter>() {
            public void onResponse(Call<FHIREncounter> c, Response<FHIREncounter> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else
                    eb.onError("HTTP " + r.code());
            }

            public void onFailure(Call<FHIREncounter> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    private void saveObservation(FHIRObservation o, ResourceCallback<FHIRObservation> cb, ErrorCallback eb) {
        apiService.createObservation(o).enqueue(new Callback<FHIRObservation>() {
            public void onResponse(Call<FHIRObservation> c, Response<FHIRObservation> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else
                    eb.onError("HTTP " + r.code());
            }

            public void onFailure(Call<FHIRObservation> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    private void saveRiskAssessment(FHIRRiskAssessment ra, ResourceCallback<FHIRRiskAssessment> cb, ErrorCallback eb) {
        apiService.createRiskAssessment(ra).enqueue(new Callback<FHIRRiskAssessment>() {
            public void onResponse(Call<FHIRRiskAssessment> c, Response<FHIRRiskAssessment> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else
                    eb.onError("HTTP " + r.code());
            }

            public void onFailure(Call<FHIRRiskAssessment> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    // --- INTERFACES ---

    public interface SaveCallback {
        void onSuccess(String reportId, String locationId);

        void onError(String error);
    }

    public interface ObservationsCallback {
        void onSuccess(FHIRBundle bundle);

        void onError(String error);
    }

    public interface LocationsCallback {
        void onSuccess(FHIRBundle bundle);

        void onError(String error);
    }

    public interface RiskAssessmentsCallback {
        void onSuccess(FHIRBundle bundle);

        void onError(String error);
    }

    public interface EncounterCallback {
        void onSuccess(FHIREncounter encounter);

        void onError(String error);
    }

    public interface LocationCallback {
        void onSuccess(FHIRLocation location);

        void onError(String error);
    }

    public interface ObservationCallback {
        void onSuccess(FHIRObservation observation);

        void onError(String error);
    }

    // Internal functional interfaces
    private interface ResourceCallback<T> {
        void onSuccess(T resource);
    }

    private interface ErrorCallback {
        void onError(String error);
    }
}
