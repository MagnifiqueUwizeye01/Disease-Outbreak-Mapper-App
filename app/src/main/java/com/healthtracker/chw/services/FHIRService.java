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
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.Patient;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.RiskAssessment;
import com.healthtracker.chw.models.Observation;
import com.healthtracker.chw.models.MeasureReport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList; // Added import
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
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {
        // Default: Do NOT force network (respect isNetworkAvailable)
        saveDiseaseReport(patientName, gender, dateOfBirth, patientAge, chwName, chwId, chwEmail,
                latitude, longitude, address, encounterDate, encounterType,
                diseaseType, symptoms, severity, observationDetails, notes,
                callback, false);
    }

    public void saveDiseaseReport(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
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
                    chwName, chwId, chwEmail, latitude, longitude, address,
                    encounterDate, encounterType, diseaseType, symptoms, severity,
                    observationDetails, notes, callback, reason, true); // true = schedule sync
            return;
        }

        submitReportInternal(patientName, gender, dateOfBirth, patientAge, chwName, chwId, chwEmail,
                latitude, longitude, address, encounterDate, encounterType,
                diseaseType, symptoms, severity, observationDetails, notes, callback, true); // true = allow fallback
    }

    public void submitReportOffline(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {

        saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                chwName, chwId, chwEmail, latitude, longitude, address,
                encounterDate, encounterType, diseaseType, symptoms, severity,
                observationDetails, notes, callback, "Manual Offline Mode", false); // false = DO NOT schedule sync
    }

    public void updateReportOffline(
            int existingId,
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {

        new Thread(() -> {
            try {
                UnsyncedReport report = new UnsyncedReport();
                report.id = existingId;
                report.patientName = patientName;
                report.gender = gender;
                report.dateOfBirth = dateOfBirth;
                report.patientAge = patientAge;
                report.chwName = chwName;
                report.chwId = chwId;
                report.chwEmail = chwEmail;
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
                report.timestamp = System.currentTimeMillis();

                unsyncedReportDao.update(report);

                if (callback != null)
                    callback.onSuccess("PENDING-UPDATE", "UPDATED");
            } catch (Exception e) {
                Log.e(TAG, "Error updating locally", e);
                if (callback != null)
                    callback.onError("Failed to update report: " + e.getMessage());
            }
        }).start();
    }

    /**
     * FORCE SUBMIT method for SyncWorker.
     * Does NOT save locally if it fails (because it's already local!).
     */
    public void submitReportForSync(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {

        // Even if network check fails, we try anyway because SyncWorker checks network
        // constraint.
        // Or we just proceed to internal logic which will fail gracefully via callback.
        submitReportInternal(patientName, gender, dateOfBirth, patientAge, chwName, chwId, chwEmail,
                latitude, longitude, address, encounterDate, encounterType, diseaseType, symptoms,
                severity, observationDetails, notes, callback, false); // false = NO fallback
    }

    private void submitReportInternal(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId, String chwEmail,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback, boolean allowFallback) {

        new Thread(() -> {
            try {
                if (patientName == null || diseaseType == null)
                    throw new IllegalArgumentException("Missing required fields");

                // Pass NULL as ID to let server assign it
                String patientId = null;
                String locationId = null;
                String encounterId = null;
                String observationId = null;
                String riskAssessmentId = null;

                String formattedBirthDate = formatDate(dateOfBirth);
                String formattedEncounterDate = formatDateTime(encounterDate);
                String formattedEffectiveDateTime = formatDateTimeISO(new Date());

                ErrorCallback errorHandler = (error) -> {
                    Log.e(TAG, "API Error: " + error);
                    if (allowFallback) {
                        Log.i(TAG, "Saving locally due to error.");
                        saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                                chwName, chwId, chwEmail, latitude, longitude, address,
                                encounterDate, encounterType, diseaseType, symptoms, severity,
                                observationDetails, notes, callback, "API Error: " + error, true);
                    } else {
                        if (callback != null)
                            callback.onError(error);
                    }
                };

                // Ensure gender is lowercase
                String normalizedGender = gender != null ? gender.toLowerCase(Locale.ROOT) : "unknown";

                FHIRPatient patient = createFHIRPatient(patientId, patientName, normalizedGender, formattedBirthDate);
                savePatient(patient, (savedPatient) -> {
                    // Use server-assigned ID
                    String finalPatientId = savedPatient.getId();
                    if (finalPatientId == null) {
                        errorHandler.onError("Server did not return Patient ID");
                        return;
                    }
                    String patientReference = "Patient/" + finalPatientId;

                    FHIRLocation location = createFHIRLocation(locationId, address, latitude, longitude);
                    saveLocation(location, (savedLocation) -> {
                        String finalLocationId = savedLocation.getId();
                        if (finalLocationId == null) {
                            // Ideally handled, but for location we can proceed or fail. Let's fail safe.
                            finalLocationId = "unknown";
                        }
                        String locationReference = "Location/" + finalLocationId;

                        FHIREncounter encounter = createFHIREncounter(encounterId, encounterType,
                                patientReference, locationReference, formattedEncounterDate);
                        saveEncounter(encounter, (savedEncounter) -> {
                            String finalEncounterId = savedEncounter.getId();
                            if (finalEncounterId == null) {
                                errorHandler.onError("Server did not return Encounter ID");
                                return;
                            }
                            String encounterReference = "Encounter/" + finalEncounterId;

                            FHIRObservation observation = createFHIRObservation(observationId, diseaseType,
                                    patientReference, encounterReference, formattedEffectiveDateTime,
                                    severity, symptoms, observationDetails, chwId, chwName);
                            saveObservation(observation, (savedObservation) -> {
                                String finalObservationId = savedObservation.getId();
                                if (finalObservationId == null)
                                    finalObservationId = "unknown";

                                String riskDescription = buildRiskDescription(severity, symptoms, notes);
                                FHIRRiskAssessment riskAssessment = createFHIRRiskAssessment(riskAssessmentId,
                                        patientReference, encounterReference, severity, riskDescription);

                                // Use final variables for callback
                                String callbackLocationId = locationReference; // Use reference as ID for now or just ID
                                String callbackReportId = finalObservationId;

                                saveRiskAssessment(riskAssessment, (savedRisk) -> {
                                    if (callback != null)
                                        callback.onSuccess(callbackReportId, callbackLocationId);
                                }, (e) -> {
                                    // Even if risk assessment fails, we might consider the report "saved" enough?
                                    // But let's stick to success path.
                                    if (callback != null)
                                        callback.onSuccess(callbackReportId, callbackLocationId);
                                });
                            }, errorHandler);
                        }, errorHandler);
                    }, errorHandler);
                }, errorHandler);

            } catch (Exception e) {
                Log.e(TAG, "Error submitting report", e);
                if (allowFallback) {
                    saveReportLocally(patientName, gender, dateOfBirth, patientAge,
                            chwName, chwId, chwEmail, latitude, longitude, address,
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
            String chwName, String chwId, String chwEmail, Double latitude, Double longitude, String address,
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
                report.chwEmail = chwEmail;
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
        if (d == null || d.trim().isEmpty()) {
            return null;
        }
        return d;
    }

    private String formatDateTime(String dt) {
        if (dt == null || dt.trim().isEmpty()) {
            return formatDateTimeISO(new Date());
        }
        return dt;
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
        // Birthdate should be null if not provided
        String validBirthDate = (birthDate != null && !birthDate.isEmpty()) ? birthDate : null;
        return new FHIRPatient(id, name, gender != null ? gender : "unknown", validBirthDate);
    }

    private FHIRLocation createFHIRLocation(String id, String address, Double lat, Double lon) {
        return new FHIRLocation(id, "GPS Location", address, lat, lon, address);
    }

    private FHIREncounter createFHIREncounter(String id, String type, String patRef, String locRef, String date) {
        return new FHIREncounter(id, "finished", type != null ? type : "home", patRef, locRef, date);
    }

    private FHIRObservation createFHIRObservation(String id, String disease, String patRef, String encRef, String date,
            String sev, List<String> sym, String det, String chwId, String chwName) {
        // Create performer list
        List<FHIRObservation.Reference> performers = new ArrayList<>();
        if (chwId != null) {
            FHIRObservation.Reference ref = new FHIRObservation.Reference();
            ref.setReference("Practitioner/" + chwId);
            ref.setDisplay(chwName != null ? chwName : "CHW");
            performers.add(ref);
        }
        FHIRObservation obs = new FHIRObservation(id, "final", disease, patRef, encRef, date, sev, sym, det);
        obs.setPerformer(performers);
        return obs;
    }

    private FHIRRiskAssessment createFHIRRiskAssessment(String id, String patRef, String encRef, String level,
            String expl) {
        return new FHIRRiskAssessment(id, "final", patRef, encRef, level, expl);
    }

    // --- INTERNAL SAVE WRAPPERS ---
    // --- INTERNAL SAVE WRAPPERS ---
    private void savePatient(FHIRPatient p, ResourceCallback<FHIRPatient> cb, ErrorCallback eb) {
        apiService.createPatient(p).enqueue(new Callback<FHIRPatient>() {
            public void onResponse(Call<FHIRPatient> c, Response<FHIRPatient> r) {
                if (r.isSuccessful())
                    cb.onSuccess(r.body());
                else {
                    try {
                        eb.onError("HTTP " + r.code() + ": " + r.errorBody().string());
                    } catch (Exception e) {
                        eb.onError("HTTP " + r.code());
                    }
                }
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
                else {
                    try {
                        eb.onError("HTTP " + r.code() + ": " + r.errorBody().string());
                    } catch (Exception e) {
                        eb.onError("HTTP " + r.code());
                    }
                }
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
                else {
                    try {
                        eb.onError("HTTP " + r.code() + ": " + r.errorBody().string());
                    } catch (Exception e) {
                        eb.onError("HTTP " + r.code());
                    }
                }
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
                else {
                    try {
                        eb.onError("HTTP " + r.code() + ": " + r.errorBody().string());
                    } catch (Exception e) {
                        eb.onError("HTTP " + r.code());
                    }
                }
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
                else {
                    try {
                        eb.onError("HTTP " + r.code() + ": " + r.errorBody().string());
                    } catch (Exception e) {
                        eb.onError("HTTP " + r.code());
                    }
                }
            }

            public void onFailure(Call<FHIRRiskAssessment> c, Throwable t) {
                eb.onError(t.getMessage());
            }
        });
    }

    // --- UNIFIED FETCH LOGIC ---

    public void getDiseaseReport(String id, DiseaseReportCallback callback) {
        if (id == null) {
            callback.onError("ID cannot be null");
            return;
        }

        if (id.startsWith("LOCAL-")) {
            // Fetch from local DB
            new Thread(() -> {
                try {
                    int localId = Integer.parseInt(id.replace("LOCAL-", ""));
                    UnsyncedReport localReport = null;
                    List<UnsyncedReport> reports = unsyncedReportDao.getAllReports();
                    for (UnsyncedReport r : reports) {
                        if (r.id == localId) {
                            localReport = r;
                            break;
                        }
                    }

                    if (localReport != null) {
                        DiseaseReport report = convertLocalReportToDiseaseReport(localReport);
                        if (callback != null) {
                            callback.onSuccess(report);
                        }
                    } else {
                        if (callback != null) {
                            callback.onError("Local report not found: " + id);
                        }
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onError("Error fetching local report: " + e.getMessage());
                    }
                }
            }).start();
        } else {
            // Fetch from FHIR Server
            getObservationById(id, new ObservationCallback() {
                @Override
                public void onSuccess(FHIRObservation observation) {
                    try {
                        DiseaseReport report = convertFHIRObservationToDiseaseReport(observation);
                        callback.onSuccess(report);
                    } catch (Exception e) {
                        callback.onError("Error validating FHIR data: " + e.getMessage());
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        }
    }

    private DiseaseReport convertLocalReportToDiseaseReport(UnsyncedReport local) {
        DiseaseReport report = new DiseaseReport();
        report.setReportId("LOCAL-" + local.id);
        report.setDiseaseType(local.diseaseType);
        report.setStatus("pending-sync");

        try {
            report.setReportDate(new Date(local.timestamp));
        } catch (Exception e) {
            report.setReportDate(new Date());
        }

        Encounter encounter = new Encounter();
        encounter.setEncounterId("LOCAL-ENC-" + local.id);
        encounter.setEncounterType(local.encounterType);

        Patient patient = new Patient();
        patient.setPatientId("LOCAL-PAT-" + local.id);
        patient.setName(local.patientName);
        patient.setGender(local.gender);
        if (local.dateOfBirth != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                patient.setDateOfBirth(format.parse(local.dateOfBirth));
            } catch (Exception e) {
            }
        }
        encounter.setPatient(patient);

        if (local.latitude != null && local.longitude != null) {
            GPSLocation location = new GPSLocation();
            location.setLatitude(local.latitude);
            location.setLongitude(local.longitude);
            location.setAddress(local.address);
            encounter.setGpsLocation(location);
        }

        if (local.observationDetails != null) {
            Observation obs = new Observation();
            obs.setDetails(local.observationDetails);
            obs.setTimestamp(report.getReportDate());
            encounter.addObservation(obs);
        }

        report.setEncounter(encounter);

        RiskAssessment risk = new RiskAssessment();
        risk.setLevel(local.severity);
        risk.setDescription("Severity: " + local.severity + ". Notes: " + local.notes);
        report.setRiskAssessment(risk);

        return report;
    }

    private DiseaseReport convertFHIRObservationToDiseaseReport(FHIRObservation observation) {
        DiseaseReport report = new DiseaseReport();
        report.setReportId(observation.getId());

        if (observation.getCode() != null && observation.getCode().getText() != null) {
            report.setDiseaseType(observation.getCode().getText());
        }

        if (observation.getEffectiveDateTime() != null) {
            try {
                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                        java.util.Locale.getDefault());
                report.setReportDate(format.parse(observation.getEffectiveDateTime()));
            } catch (Exception e) {
                report.setReportDate(new Date());
            }
        } else {
            report.setReportDate(new Date());
        }

        report.setStatus(observation.getStatus() != null ? observation.getStatus() : "pending");

        Encounter encounter = new Encounter();
        if (observation.getEncounter() != null && observation.getEncounter().getReference() != null) {
            encounter.setEncounterId(observation.getEncounter().getReference().replace("Encounter/", ""));
            if (observation.getEffectiveDateTime() != null) {
                try {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                            java.util.Locale.getDefault());
                    encounter.setEncounterDate(format.parse(observation.getEffectiveDateTime()));
                } catch (Exception e) {
                    encounter.setEncounterDate(new Date());
                }
            }
        }

        if (observation.getSubject() != null && observation.getSubject().getReference() != null) {
            Patient patient = new Patient();
            patient.setPatientId(observation.getSubject().getReference().replace("Patient/", ""));
            if (observation.getSubject().getDisplay() != null) {
                patient.setName(observation.getSubject().getDisplay());
            }
            encounter.setPatient(patient);
        }

        report.setEncounter(encounter);

        if (observation.getValueCodeableConcept() != null) {
            RiskAssessment risk = new RiskAssessment();
            risk.setLevel(observation.getValueCodeableConcept().getText());
            risk.setDescription("Risk level: " + observation.getValueCodeableConcept().getText());
            report.setRiskAssessment(risk);
        }

        if (observation.getValueString() != null && !observation.getValueString().isEmpty()) {
            Observation obs = new Observation();
            obs.setDetails(observation.getValueString());
            obs.setTimestamp(report.getReportDate());
            if (report.getEncounter() != null) {
                report.getEncounter().addObservation(obs);
            }
        }

        return report;
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

    public interface DiseaseReportCallback {
        void onSuccess(DiseaseReport report);

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
