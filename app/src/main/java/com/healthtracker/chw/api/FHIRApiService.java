package com.healthtracker.chw.api;

import com.healthtracker.chw.models.fhir.FHIRBundle;
import com.healthtracker.chw.models.fhir.FHIREncounter;
import com.healthtracker.chw.models.fhir.FHIRLocation;
import com.healthtracker.chw.models.fhir.FHIRObservation;
import com.healthtracker.chw.models.fhir.FHIRPatient;
import com.healthtracker.chw.models.fhir.FHIRRiskAssessment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * FHIR REST API service interface
 * Follows official FHIR REST API conventions
 * https://www.hl7.org/fhir/http.html
 */
public interface FHIRApiService {
    
    // Patient endpoints
    @POST("Patient")
    Call<FHIRPatient> createPatient(@Body FHIRPatient patient);
    
    @GET("Patient/{id}")
    Call<FHIRPatient> getPatient(@Path("id") String id);
    
    @PUT("Patient/{id}")
    Call<FHIRPatient> updatePatient(@Path("id") String id, @Body FHIRPatient patient);
    
    // Encounter endpoints
    @POST("Encounter")
    Call<FHIREncounter> createEncounter(@Body FHIREncounter encounter);
    
    @GET("Encounter/{id}")
    Call<FHIREncounter> getEncounter(@Path("id") String id);
    
    @PUT("Encounter/{id}")
    Call<FHIREncounter> updateEncounter(@Path("id") String id, @Body FHIREncounter encounter);
    
    // Observation endpoints
    @POST("Observation")
    Call<FHIRObservation> createObservation(@Body FHIRObservation observation);
    
    @GET("Observation/{id}")
    Call<FHIRObservation> getObservation(@Path("id") String id);
    
    @GET("Observation")
    Call<FHIRBundle> searchObservations(@Query("code") String code, 
                                        @Query("subject") String subject,
                                        @Query("encounter") String encounter,
                                        @Query("_count") Integer count);
    
    @PUT("Observation/{id}")
    Call<FHIRObservation> updateObservation(@Path("id") String id, @Body FHIRObservation observation);
    
    // Location endpoints
    @POST("Location")
    Call<FHIRLocation> createLocation(@Body FHIRLocation location);
    
    @GET("Location/{id}")
    Call<FHIRLocation> getLocation(@Path("id") String id);
    
    @GET("Location")
    Call<FHIRBundle> searchLocations(@Query("_count") Integer count);
    
    @PUT("Location/{id}")
    Call<FHIRLocation> updateLocation(@Path("id") String id, @Body FHIRLocation location);
    
    // RiskAssessment endpoints
    @POST("RiskAssessment")
    Call<FHIRRiskAssessment> createRiskAssessment(@Body FHIRRiskAssessment riskAssessment);
    
    @GET("RiskAssessment/{id}")
    Call<FHIRRiskAssessment> getRiskAssessment(@Path("id") String id);
    
    @GET("RiskAssessment")
    Call<FHIRBundle> searchRiskAssessments(@Query("subject") String subject,
                                            @Query("encounter") String encounter,
                                            @Query("_count") Integer count);
    
    @PUT("RiskAssessment/{id}")
    Call<FHIRRiskAssessment> updateRiskAssessment(@Path("id") String id, @Body FHIRRiskAssessment riskAssessment);
    
    // MeasureReport endpoints (optional, for dashboard summaries)
    @GET("MeasureReport")
    Call<FHIRBundle> getMeasureReports(@Query("measure") String measure,
                                        @Query("period") String period,
                                        @Query("_count") Integer count);
}

