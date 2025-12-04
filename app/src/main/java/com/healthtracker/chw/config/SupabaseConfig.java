package com.healthtracker.chw.config;

/**
 * Supabase configuration
 * Contains database connection details and API endpoints
 */
public class SupabaseConfig {
    // Supabase project URL (extracted from connection string)
    public static final String SUPABASE_URL = "https://pbmtudumjersmvpnnujj.supabase.co";
    
    // Supabase REST API base path
    public static final String REST_API_PATH = "/rest/v1";
    
    // Full REST API URL
    public static final String REST_API_URL = SUPABASE_URL + REST_API_PATH;
    
    // Database connection details (for reference)
    public static final String DB_HOST = "db.pbmtudumjersmvpnnujj.supabase.co";
    public static final int DB_PORT = 5432;
    public static final String DB_NAME = "postgres";
    public static final String DB_USER = "postgres";
    // Password stored securely - should be in environment or secure storage
    public static final String DB_PASSWORD = "REDACTED_DB_PASSWORD";
    
    // API Key for Supabase REST API (you'll need to get this from Supabase dashboard)
    // For now, using service_role key pattern - replace with your actual key
    public static final String API_KEY = "REDACTED_SUPABASE_KEY"; // Replace with actual key
    
    // Table names
    public static final String TABLE_CHW = "chw";
    public static final String TABLE_PATIENT = "patient";
    public static final String TABLE_ENCOUNTER = "encounter";
    public static final String TABLE_GPS_LOCATION = "gps_location";
    public static final String TABLE_OBSERVATION = "observation";
    public static final String TABLE_DISEASE_REPORT = "disease_report";
    public static final String TABLE_MEASURE_REPORT = "measure_report";
    public static final String TABLE_RISK_ASSESSMENT = "risk_assessment";
}

