package com.healthtracker.chw.api;

import android.content.Context;
import android.util.Log;

import com.healthtracker.chw.config.FHIRConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * FHIR API client
 * Creates Retrofit service for FHIR REST API
 * Follows FHIR REST API conventions with proper headers
 */
public class FHIRClient {
    private static final String TAG = "FHIRClient";
    private static FHIRApiService apiService;
    private static String currentBaseUrl;
    
    /**
     * Get FHIR API service instance
     */
    public static FHIRApiService getApiService(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        
        String baseUrl = FHIRConfig.getBaseUrl(context);
        
        // Recreate service if base URL changed
        if (apiService == null || !baseUrl.equals(currentBaseUrl)) {
            try {
                currentBaseUrl = baseUrl;
                
                // Ensure base URL ends with /
                if (!baseUrl.endsWith("/")) {
                    baseUrl = baseUrl + "/";
                }
                
                // Configure Gson with Date adapter for FHIR date formats
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                        .registerTypeAdapter(Date.class, new com.google.gson.JsonSerializer<Date>() {
                            @Override
                            public com.google.gson.JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                                java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.US);
                                format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                                return new com.google.gson.JsonPrimitive(format.format(src));
                            }
                        })
                        .create();
                
                // Configure OkHttp with logging and FHIR headers
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                
                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .addInterceptor(chain -> {
                            okhttp3.Request original = chain.request();
                            okhttp3.Request.Builder requestBuilder = original.newBuilder()
                                    // FHIR standard headers
                                    .header("Content-Type", "application/fhir+json")
                                    .header("Accept", "application/fhir+json")
                                    // Optional: Add authorization header if needed
                                    // .header("Authorization", "Bearer " + token)
                                    ;
                            return chain.proceed(requestBuilder.build());
                        })
                        .build();
                
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                
                apiService = retrofit.create(FHIRApiService.class);
                Log.d(TAG, "FHIR API service initialized with base URL: " + baseUrl);
            } catch (Exception e) {
                Log.e(TAG, "Error creating FHIR Retrofit service", e);
                throw new RuntimeException("Failed to initialize FHIR API client: " + e.getMessage(), e);
            }
        }
        return apiService;
    }
    
    /**
     * Reset API service (useful when base URL changes)
     */
    public static void resetApiService() {
        apiService = null;
        currentBaseUrl = null;
    }
}

