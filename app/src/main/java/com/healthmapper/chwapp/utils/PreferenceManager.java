package com.healthmapper.chwapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "CHW_APP_PREFS";
    private static final String KEY_CHW_ID = "chw_id";
    private static final String KEY_CHW_NAME = "chw_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REMEMBER_LOGIN = "remember_login";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_time";
    private static final String KEY_OFFLINE_MODE = "offline_mode";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getCurrentCHWId() {
        return sharedPreferences.getString(KEY_CHW_ID, null);
    }

    public void setCurrentCHWId(String chwId) {
        editor.putString(KEY_CHW_ID, chwId);
        editor.apply();
    }

    public String getCurrentCHWName() {
        return sharedPreferences.getString(KEY_CHW_NAME, null);
    }

    public void setCurrentCHWName(String chwName) {
        editor.putString(KEY_CHW_NAME, chwName);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean shouldRememberLogin() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false);
    }

    public void setRememberLogin(boolean remember) {
        editor.putBoolean(KEY_REMEMBER_LOGIN, remember);
        editor.apply();
    }

    public long getLastSyncTime() {
        return sharedPreferences.getLong(KEY_LAST_SYNC_TIME, 0L);
    }

    public void setLastSyncTime(long timestamp) {
        editor.putLong(KEY_LAST_SYNC_TIME, timestamp);
        editor.apply();
    }

    public boolean isOfflineMode() {
        return sharedPreferences.getBoolean(KEY_OFFLINE_MODE, false);
    }

    public void setOfflineMode(boolean offlineMode) {
        editor.putBoolean(KEY_OFFLINE_MODE, offlineMode);
        editor.apply();
    }

    public void clearSession() {
        editor.remove(KEY_CHW_ID);
        editor.remove(KEY_CHW_NAME);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}