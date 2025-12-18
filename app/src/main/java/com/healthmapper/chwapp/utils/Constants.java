package com.healthmapper.chwapp.utils;

public class Constants {

    // Disease Types
    public static final String[] DISEASE_TYPES = {
            "Cholera",
            "Malaria",
            "Tuberculosis",
            "Measles",
            "Dysentery",
            "Typhoid",
            "Hepatitis",
            "Meningitis",
            "Pneumonia",
            "COVID-19",
            "Other"
    };

    // Report Status
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUBMITTED = "SUBMITTED";

    // Default CHW Roles
    public static final String ROLE_CHW = "CHW";
    public static final String ROLE_SUPERVISOR = "SUPERVISOR";
    public static final String ROLE_ADMIN = "ADMIN";

    // Rwanda Districts (sample)
    public static final String[] RWANDA_DISTRICTS = {
            "Kigali",
            "Nyarugenge",
            "Gasabo",
            "Kicukiro",
            "Musanze",
            "Rubavu",
            "Nyagatare",
            "Bugesera",
            "Huye",
            "Muhanga"
    };

    // GPS Coordinates for Rwanda
    public static final double RWANDA_CENTER_LAT = -1.9403;
    public static final double RWANDA_CENTER_LNG = 29.8739;
    public static final double KIGALI_LAT = -1.9441;
    public static final double KIGALI_LNG = 30.0619;

    // API Endpoints (for future FHIR integration)
    public static final String BASE_URL = "https://api.healthmapper.rw/";
    public static final String ENDPOINT_LOGIN = "auth/login";
    public static final String ENDPOINT_REPORTS = "fhir/observations";
    public static final String ENDPOINT_CHW_PROFILE = "fhir/practitioners";

    // Date Formats
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String TIME_FORMAT_DISPLAY = "HH:mm";

    // Validation Rules
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_NOTES_LENGTH = 500;

    // Permissions
    public static final int PERMISSION_LOCATION = 1001;
    public static final int PERMISSION_STORAGE = 1002;

    // Intent Extras
    public static final String EXTRA_CHW_ID = "chw_id";
    public static final String EXTRA_REPORT_ID = "report_id";
    public static final String EXTRA_EDIT_MODE = "edit_mode";

    // SharedPreferences Keys
    public static final String PREF_FIRST_LAUNCH = "first_launch";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_SYNC_FREQUENCY = "sync_frequency";

    // Error Messages
    public static final String ERROR_NETWORK = "Network connection required";
    public static final String ERROR_LOCATION = "Location permission required";
    public static final String ERROR_VALIDATION = "Please check your input";
    public static final String ERROR_SYNC = "Sync failed. Data saved locally.";

    // Success Messages
    public static final String SUCCESS_REPORT_SAVED = "Report saved successfully";
    public static final String SUCCESS_PROFILE_UPDATED = "Profile updated successfully";
    public static final String SUCCESS_SYNC = "Data synced successfully";

    private Constants() {
        // Prevent instantiation
    }
}