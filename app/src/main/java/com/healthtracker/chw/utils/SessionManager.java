package com.healthtracker.chw.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session persistence
 * Keeps users logged in after closing the app
 */
public class SessionManager {
    private static final String PREFS_NAME = "chw_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_CHW_CODE = "chw_code";
    private static final String KEY_PHONE = "phone";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Save user session after successful login
     */
    public void saveSession(String userId, String email, String name, String chwCode, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_CHW_CODE, chwCode);
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get current user ID
     */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    /**
     * Get current user email
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Get current user name
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    /**
     * Get current user CHW code
     */
    public String getChwCode() {
        return prefs.getString(KEY_CHW_CODE, null);
    }

    /**
     * Get current user phone
     */
    public String getPhone() {
        return prefs.getString(KEY_PHONE, null);
    }

    // Settings Preferences
    private static final String KEY_AUTO_SYNC = "auto_sync";
    private static final String KEY_OUTBREAK_ALERTS = "outbreak_alerts";
    private static final String KEY_SYNC_REMINDERS = "sync_reminders";

    public void setAutoSync(boolean enabled) {
        editor.putBoolean(KEY_AUTO_SYNC, enabled);
        editor.apply();
    }

    public boolean isAutoSyncEnabled() {
        return prefs.getBoolean(KEY_AUTO_SYNC, true); // Default to true
    }

    public void setOutbreakAlerts(boolean enabled) {
        editor.putBoolean(KEY_OUTBREAK_ALERTS, enabled);
        editor.apply();
    }

    public boolean areOutbreakAlertsEnabled() {
        return prefs.getBoolean(KEY_OUTBREAK_ALERTS, true);
    }

    public void setSyncReminders(boolean enabled) {
        editor.putBoolean(KEY_SYNC_REMINDERS, enabled);
        editor.apply();
    }

    public boolean areSyncRemindersEnabled() {
        return prefs.getBoolean(KEY_SYNC_REMINDERS, true);
    }

    // Extended Settings
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_GPS_ACCURACY = "gps_accuracy";
    private static final String KEY_LOW_BANDWIDTH = "low_bandwidth";

    public void setLanguage(String language) {
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "English");
    }

    public void setGpsAccuracy(String accuracy) {
        editor.putString(KEY_GPS_ACCURACY, accuracy);
        editor.apply();
    }

    public String getGpsAccuracy() {
        return prefs.getString(KEY_GPS_ACCURACY, "High Accuracy");
    }

    public void setLowBandwidthMode(boolean enabled) {
        editor.putBoolean(KEY_LOW_BANDWIDTH, enabled);
        editor.apply();
    }

    public boolean isLowBandwithModeEnabled() {
        return prefs.getBoolean(KEY_LOW_BANDWIDTH, false);
    }

    /**
     * Clear session (logout)
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
