package com.healthtracker.chw.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.healthtracker.chw.config.SupabaseConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Supabase API client
 * Creates Retrofit service for Supabase REST API
 */
public class SupabaseClient {
    private static SupabaseApiService apiService;
    private static final String PREFS_NAME = "supabase_prefs";
    private static final String KEY_API_KEY = "supabase_api_key";
    
    /**
     * Get Supabase API service instance
     */
    public static SupabaseApiService getApiService(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        
        if (apiService == null) {
            try {
                // Get API key from SharedPreferences or use default
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String apiKey = prefs.getString(KEY_API_KEY, SupabaseConfig.API_KEY);
                
                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your-supabase-anon-key")) {
                    android.util.Log.w("SupabaseClient", "Using default API key. Please set your actual API key in SupabaseConfig.");
                }
            
            // Configure Gson with Date adapter
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .registerTypeAdapter(Date.class, new com.google.gson.JsonSerializer<Date>() {
                        @Override
                        public com.google.gson.JsonElement serialize(Date src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                            return new com.google.gson.JsonPrimitive(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US).format(src));
                        }
                    })
                    .create();
            
            // Configure OkHttp with logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request.Builder requestBuilder = original.newBuilder()
                                .header("apikey", apiKey)
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .header("Prefer", "return=representation");
                        return chain.proceed(requestBuilder.build());
                    })
                    .build();
            
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(SupabaseConfig.REST_API_URL + "/")
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                
                apiService = retrofit.create(SupabaseApiService.class);
            } catch (Exception e) {
                android.util.Log.e("SupabaseClient", "Error creating Retrofit service", e);
                throw new RuntimeException("Failed to initialize Supabase API client: " + e.getMessage(), e);
            }
        }
        return apiService;
    }
    
    /**
     * Set API key for Supabase
     */
    public static void setApiKey(Context context, String apiKey) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
        // Reset service to use new key
        apiService = null;
    }
}

