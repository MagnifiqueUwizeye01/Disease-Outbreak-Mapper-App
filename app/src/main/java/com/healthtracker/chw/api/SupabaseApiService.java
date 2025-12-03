package com.healthtracker.chw.api;

import com.healthtracker.chw.models.CHW;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.Encounter;
import com.healthtracker.chw.models.GPSLocation;
import com.healthtracker.chw.models.MeasureReport;
import com.healthtracker.chw.models.Observation;
import com.healthtracker.chw.models.Patient;
import com.healthtracker.chw.models.RiskAssessment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Supabase REST API service interface
 * Uses Supabase PostgREST API for database operations
 */
public interface SupabaseApiService {
    
    // CHW endpoints
    @POST("chw")
    Call<CHW> createCHW(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body CHW chw);
    
    @GET("chw")
    Call<List<CHW>> getCHWs(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    @GET("chw")
    Call<List<CHW>> getCHWById(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Query("id") String id);
    
    // Patient endpoints
    @POST("patient")
    Call<Patient> createPatient(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body Patient patient);
    
    @GET("patient")
    Call<List<Patient>> getPatients(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    @GET("patient")
    Call<List<Patient>> getPatientById(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Query("patient_id") String patientId);
    
    // Encounter endpoints
    @POST("encounter")
    Call<Encounter> createEncounter(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body Encounter encounter);
    
    @GET("encounter")
    Call<List<Encounter>> getEncounters(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    @GET("encounter")
    Call<List<Encounter>> getEncounterById(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Query("encounter_id") String encounterId);
    
    // GPS Location endpoints
    @POST("gps_location")
    Call<GPSLocation> createGPSLocation(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body GPSLocation location);
    
    @GET("gps_location")
    Call<List<GPSLocation>> getGPSLocations(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    // Observation endpoints
    @POST("observation")
    Call<Observation> createObservation(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body Observation observation);
    
    @GET("observation")
    Call<List<Observation>> getObservations(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    // Disease Report endpoints
    @POST("disease_report")
    Call<DiseaseReport> createDiseaseReport(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body DiseaseReport report);
    
    @GET("disease_report")
    Call<List<DiseaseReport>> getDiseaseReports(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    @GET("disease_report")
    Call<List<DiseaseReport>> getDiseaseReportById(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Query("report_id") String reportId);
    
    // Measure Report endpoints
    @POST("measure_report")
    Call<MeasureReport> createMeasureReport(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body MeasureReport report);
    
    @GET("measure_report")
    Call<List<MeasureReport>> getMeasureReports(@Header("apikey") String apiKey, @Header("Authorization") String auth);
    
    // Risk Assessment endpoints
    @POST("risk_assessment")
    Call<RiskAssessment> createRiskAssessment(@Header("apikey") String apiKey, @Header("Authorization") String auth, @Body RiskAssessment assessment);
    
    @GET("risk_assessment")
    Call<List<RiskAssessment>> getRiskAssessments(@Header("apikey") String apiKey, @Header("Authorization") String auth);
}

