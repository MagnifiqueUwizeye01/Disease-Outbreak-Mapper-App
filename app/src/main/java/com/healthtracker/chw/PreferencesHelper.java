package com.healthtracker.chw;

import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class PreferencesHelper {

    private static final String PREF_NAME = "HealthTrackerPrefs";
    private static final String KEY_IS_REGISTERED = "is_registered";

    // Pre-approved CHW codes
    private static final Set<String> APPROVED_CHW_CODES = new HashSet<String>() {{
        add("CHW001");
        add("CHW002");
        add("CHW003");
        add("CHW004");
        add("CHW005");
    }};

    // Save registration data
    public static boolean saveRegistrationData(SharedPreferences sharedPreferences,
                                               String fullName, String email, String password, String chwCode) {
        // Validate CHW code
        if (!isValidCHWCode(chwCode)) {
            return false;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store user credentials
        editor.putString("user_fullname", fullName);
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        editor.putString("user_chw_code", chwCode);
        editor.putBoolean(KEY_IS_REGISTERED, true);
        editor.apply();

        return true;
    }

    // Check if CHW code is valid
    public static boolean isValidCHWCode(String chwCode) {
        return APPROVED_CHW_CODES.contains(chwCode);
    }

    // Check if user is registered
    public static boolean isUserRegistered(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(KEY_IS_REGISTERED, false);
    }

    // Validate login credentials - accept either email OR full name
    public static boolean isValidCredentials(SharedPreferences sharedPreferences, String username, String password) {
        String storedEmail = sharedPreferences.getString("user_email", "");
        String storedFullName = sharedPreferences.getString("user_fullname", "");
        String storedPassword = sharedPreferences.getString("user_password", "");

        // Check if username matches either email OR full name, AND password matches
        return (username.equals(storedEmail) || username.equals(storedFullName)) &&
                password.equals(storedPassword);
    }

    // Get user's full name for display
    public static String getUserFullName(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("user_fullname", "CHW");
    }
}