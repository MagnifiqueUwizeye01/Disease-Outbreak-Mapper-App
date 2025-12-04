package com.healthtracker.chw.services;

import android.content.Context;
import android.util.Log;

import com.healthtracker.chw.api.SupabaseApiService;
import com.healthtracker.chw.api.SupabaseClient;
import com.healthtracker.chw.models.CHW;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.Observation;
import com.healthtracker.chw.models.Patient;
import com.healthtracker.chw.models.RiskAssessment;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service for interacting with Supabase database
 * Handles saving and retrieving data using the model classes
 */
public class SupabaseService {
    private static final String TAG = "SupabaseService";
    private SupabaseApiService apiService;
    private Context context;
    private SaveCallback currentCallback; // Store callback for error handling
    
    public SupabaseService(Context context) {
        this.context = context;
        try {
            if (context == null) {
                Log.e(TAG, "Context is null, cannot initialize SupabaseService");
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.apiService = SupabaseClient.getApiService(context);
            if (this.apiService == null) {
                Log.e(TAG, "Failed to initialize Supabase API service");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SupabaseService", e);
            throw new RuntimeException("Failed to initialize Supabase service: " + e.getMessage(), e);
        }
    }
    
    /**
     * Save complete disease report with all related entities
     */
    public void saveDiseaseReport(
            String patientName, String gender, String dateOfBirth, Integer patientAge,
            String chwName, String chwId,
            Double latitude, Double longitude, String address,
            String encounterDate, String encounterType,
            String diseaseType, List<String> symptoms, String severity,
            String observationDetails, String notes,
            SaveCallback callback) {
        
        this.currentCallback = callback; // Store callback for error handling
        
        // Check if API service is initialized
        if (apiService == null) {
            Log.e(TAG, "API service is null, cannot save report");
            if (callback != null) {
                callback.onError("API service not initialized. Please check your internet connection and try again.");
            }
            return;
        }
        
        new Thread(() -> {
            try {
                // Validate required parameters
                if (patientName == null || patientName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Patient name is required");
                }
                if (diseaseType == null || diseaseType.trim().isEmpty()) {
                    throw new IllegalArgumentException("Disease type is required");
                }
                if (latitude == null || longitude == null) {
                    throw new IllegalArgumentException("GPS coordinates are required");
                }
                
                // Make variables effectively final for lambda
                final String finalChwName = (chwName == null || chwName.trim().isEmpty()) ? "Unknown CHW" : chwName;
                final String finalChwId = (chwId == null || chwId.trim().isEmpty()) ? 
                    "chw_" + UUID.randomUUID().toString() : chwId;
                
                // Generate IDs
                String patientId = "patient_" + UUID.randomUUID().toString();
                String encounterId = "encounter_" + UUID.randomUUID().toString();
                String locationId = "location_" + UUID.randomUUID().toString();
                String observationId = "observation_" + UUID.randomUUID().toString();
                String reportId = "report_" + UUID.randomUUID().toString();
                String riskId = "risk_" + UUID.randomUUID().toString();
                
                // Parse dates with null safety
                Date dob = parseDate(dateOfBirth);
                Date encDate = parseDateTime(encounterDate);
                Date reportDate = new Date();
                
                // 1. Create or get CHW
                CHW chw = new CHW(finalChwId, finalChwName, "Community Health Worker");
                
                // 2. Create Patient
                Patient patient = new Patient(patientId, patientName, dob, gender != null ? gender : "unknown", null);
                
                // 3. Create GPS Location
                GPSLocation gpsLocation = new GPSLocation(locationId, latitude, longitude, address);
                
                // 4. Create Encounter
                Encounter encounter = new Encounter(encounterId, encDate != null ? encDate : new Date(), 
                    encounterType != null ? encounterType : "home");
                encounter.setChw(chw);
                encounter.setPatient(patient);
                encounter.setGpsLocation(gpsLocation);
                
                // 5. Create Observation
                Observation observation = new Observation(observationId, 
                    observationDetails != null ? observationDetails : "", new Date());
                observation.setEncounter(encounter);
                encounter.addObservation(observation);
                
                // 6. Create Disease Report
                DiseaseReport diseaseReport = new DiseaseReport(reportId, diseaseType, reportDate, "pending");
                diseaseReport.setEncounter(encounter);
                
                // 7. Create Risk Assessment (linked to Disease Report)
                String riskDescription = "Risk level: " + (severity != null ? severity : "unknown");
                if (notes != null && !notes.isEmpty()) {
                    riskDescription += ". " + notes;
                }
                RiskAssessment riskAssessment = new RiskAssessment(riskId, 
                    severity != null ? severity : "low", riskDescription);
                riskAssessment.setDiseaseReport(diseaseReport);
                diseaseReport.setRiskAssessment(riskAssessment);
                
                // Save in order: CHW -> Patient -> Location -> Encounter -> Observation -> Disease Report -> Risk Assessment
                // Note: Risk Assessment needs Disease Report ID, so save Disease Report first
                saveCHW(chw, () -> {
                    savePatient(patient, () -> {
                        saveGPSLocation(gpsLocation, () -> {
                            saveEncounter(encounter, () -> {
                                saveObservation(observation, () -> {
                                    saveDiseaseReport(diseaseReport, () -> {
                                        // After Disease Report is saved, update Risk Assessment with report ID
                                        riskAssessment.setDiseaseReport(diseaseReport);
                                        saveRiskAssessment(riskAssessment, () -> {
                                            if (callback != null) {
                                                callback.onSuccess(reportId);
                                            }
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving disease report", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }
    
    private void saveCHW(CHW chw, Runnable onSuccess) {
        apiService.createCHW("", "", chw).enqueue(new Callback<CHW>() {
            @Override
            public void onResponse(Call<CHW> call, Response<CHW> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "CHW saved successfully");
                    onSuccess.run();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    Log.e(TAG, "Failed to save CHW: " + response.code() + " - " + errorBody);
                    // Try to continue anyway (might already exist or will be created later)
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<CHW> call, Throwable t) {
                Log.e(TAG, "Error saving CHW", t);
                // Try to continue anyway - CHW might already exist
                onSuccess.run();
            }
        });
    }
    
    private void savePatient(Patient patient, Runnable onSuccess) {
        apiService.createPatient("", "", patient).enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Patient saved successfully");
                    onSuccess.run();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                    Log.e(TAG, "Failed to save Patient: " + response.code() + " - " + errorBody);
                    // Continue - patient might already exist
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                Log.e(TAG, "Error saving Patient", t);
                // Continue - might be network issue but we'll catch it at DiseaseReport level
                onSuccess.run();
            }
        });
    }
    
    private void saveGPSLocation(GPSLocation location, Runnable onSuccess) {
        apiService.createGPSLocation("", "", location).enqueue(new Callback<GPSLocation>() {
            @Override
            public void onResponse(Call<GPSLocation> call, Response<GPSLocation> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "GPS Location saved successfully");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Failed to save GPS Location: " + response.code());
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<GPSLocation> call, Throwable t) {
                Log.e(TAG, "Error saving GPS Location", t);
                onSuccess.run();
            }
        });
    }
    
    private void saveEncounter(Encounter encounter, Runnable onSuccess) {
        apiService.createEncounter("", "", encounter).enqueue(new Callback<Encounter>() {
            @Override
            public void onResponse(Call<Encounter> call, Response<Encounter> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Encounter saved successfully");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Failed to save Encounter: " + response.code());
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<Encounter> call, Throwable t) {
                Log.e(TAG, "Error saving Encounter", t);
                onSuccess.run();
            }
        });
    }
    
    private void saveObservation(Observation observation, Runnable onSuccess) {
        apiService.createObservation("", "", observation).enqueue(new Callback<Observation>() {
            @Override
            public void onResponse(Call<Observation> call, Response<Observation> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Observation saved successfully");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Failed to save Observation: " + response.code());
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<Observation> call, Throwable t) {
                Log.e(TAG, "Error saving Observation", t);
                onSuccess.run();
            }
        });
    }
    
    private void saveDiseaseReport(DiseaseReport report, Runnable onSuccess) {
        apiService.createDiseaseReport("", "", report).enqueue(new Callback<DiseaseReport>() {
            @Override
            public void onResponse(Call<DiseaseReport> call, Response<DiseaseReport> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Disease Report saved successfully");
                    onSuccess.run();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    String errorMsg = "HTTP " + response.code() + ": " + errorBody;
                    Log.e(TAG, "Failed to save Disease Report: " + errorMsg);
                    // Don't continue on error - this is the main entity
                    if (currentCallback != null) {
                        currentCallback.onError(errorMsg);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<DiseaseReport> call, Throwable t) {
                String errorMsg = t.getMessage() != null ? t.getMessage() : "Network error";
                if (t instanceof java.net.UnknownHostException || t instanceof java.net.ConnectException) {
                    errorMsg = "Network error: Unable to connect to server. Please check your internet connection.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Network error: Connection timeout. Please try again.";
                }
                Log.e(TAG, "Error saving Disease Report", t);
                if (currentCallback != null) {
                    currentCallback.onError(errorMsg);
                }
            }
        });
    }
    
    private void saveRiskAssessment(RiskAssessment assessment, Runnable onSuccess) {
        apiService.createRiskAssessment("", "", assessment).enqueue(new Callback<RiskAssessment>() {
            @Override
            public void onResponse(Call<RiskAssessment> call, Response<RiskAssessment> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Risk Assessment saved successfully");
                    onSuccess.run();
                } else {
                    Log.e(TAG, "Failed to save Risk Assessment: " + response.code());
                    onSuccess.run();
                }
            }
            
            @Override
            public void onFailure(Call<RiskAssessment> call, Throwable t) {
                Log.e(TAG, "Error saving Risk Assessment", t);
                onSuccess.run();
            }
        });
    }
    
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return new Date();
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            return format.parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }
    
    private Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return new Date();
        try {
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            return format.parse(dateTimeStr);
        } catch (Exception e) {
            return new Date();
        }
    }
    
    /**
     * Fetch all disease reports with related data
     */
    public void getAllDiseaseReports(ReportsCallback callback) {
        apiService.getDiseaseReports("", "").enqueue(new Callback<List<DiseaseReport>>() {
            @Override
            public void onResponse(Call<List<DiseaseReport>> call, Response<List<DiseaseReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Fetched " + response.body().size() + " disease reports");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed to fetch disease reports: " + response.code());
                    callback.onError("Failed to fetch reports: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<List<DiseaseReport>> call, Throwable t) {
                Log.e(TAG, "Error fetching disease reports", t);
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Fetch disease report by ID with related data
     */
    public void getDiseaseReportById(String reportId, ReportCallback callback) {
        apiService.getDiseaseReportById("", "", reportId).enqueue(new Callback<List<DiseaseReport>>() {
            @Override
            public void onResponse(Call<List<DiseaseReport>> call, Response<List<DiseaseReport>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d(TAG, "Fetched disease report: " + reportId);
                    callback.onSuccess(response.body().get(0));
                } else {
                    Log.e(TAG, "Failed to fetch disease report: " + response.code());
                    callback.onError("Report not found");
                }
            }
            
            @Override
            public void onFailure(Call<List<DiseaseReport>> call, Throwable t) {
                Log.e(TAG, "Error fetching disease report", t);
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Fetch encounters with related data for a report
     */
    public void getEncounterForReport(String encounterId, EncounterCallback callback) {
        apiService.getEncounterById("", "", encounterId).enqueue(new Callback<List<Encounter>>() {
            @Override
            public void onResponse(Call<List<Encounter>> call, Response<List<Encounter>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Encounter not found");
                }
            }
            
            @Override
            public void onFailure(Call<List<Encounter>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    /**
     * Fetch risk assessments for reports
     */
    public void getRiskAssessments(RiskAssessmentsCallback callback) {
        apiService.getRiskAssessments("", "").enqueue(new Callback<List<RiskAssessment>>() {
            @Override
            public void onResponse(Call<List<RiskAssessment>> call, Response<List<RiskAssessment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch risk assessments");
                }
            }
            
            @Override
            public void onFailure(Call<List<RiskAssessment>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    
    public interface SaveCallback {
        void onSuccess(String reportId);
        void onError(String error);
    }
    
    public interface ReportsCallback {
        void onSuccess(List<DiseaseReport> reports);
        void onError(String error);
    }
    
    public interface ReportCallback {
        void onSuccess(DiseaseReport report);
        void onError(String error);
    }
    
    public interface EncounterCallback {
        void onSuccess(Encounter encounter);
        void onError(String error);
    }
    
    public interface RiskAssessmentsCallback {
        void onSuccess(List<RiskAssessment> assessments);
        void onError(String error);
    }
}

