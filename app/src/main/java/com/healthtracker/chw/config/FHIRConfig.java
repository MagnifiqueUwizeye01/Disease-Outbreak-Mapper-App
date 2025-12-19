package com.healthtracker.chw.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * FHIR configuration
 * Handles FHIR server connection details and base URL storage.
 */
public class FHIRConfig {

    private static final String PREFS_NAME = "fhir_prefs";
    private static final String KEY_BASE_URL = "fhir_base_url";


    private static final String DEFAULT_BASE_URL = "https://hapi.fhir.org/baseR4";

    /**
     * Retrieve the FHIR base URL from SharedPreferences.
     * If not set, return the default server URL.
     */
    public static String getBaseUrl(Context context) {
        if (context == null) {
            // Fallback if context is null (should not happen)
            return DEFAULT_BASE_URL;
        }

        SharedPreferences prefs = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    /**
     * Save a new FHIR base URL in SharedPreferences.
     */
    public static void setBaseUrl(Context context, String baseUrl) {
        if (context == null || baseUrl == null || baseUrl.isEmpty()) {
            return;
        }

        // Normalize: remove trailing slash
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        SharedPreferences prefs = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );

        prefs.edit()
                .putString(KEY_BASE_URL, baseUrl)
                .apply();
    }

    /**
     * Retrieve the default base URL.
     */
    public static String getDefaultBaseUrl() {
        return DEFAULT_BASE_URL;
    }
}
