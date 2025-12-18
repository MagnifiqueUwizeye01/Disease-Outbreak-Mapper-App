package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

public class SettingsFragment extends Fragment {

    private com.healthtracker.chw.utils.SessionManager sessionManager;
    private com.google.android.material.switchmaterial.SwitchMaterial swAutoSync, swAlerts, swReminders;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new com.healthtracker.chw.utils.SessionManager(requireContext());

        // Bind Views
        // Note: The layout wraps switches in Linearlayouts with IDs, but the switches
        // inside don't have IDs.
        // We need to traverse the layout or assign IDs. Looking at
        // fragment_settings.xml, the SwitchMaterial is the child of the LinearLayout.

        // Strategy: Use the container LinearLayout to find its child SwitchMaterial
        View containerAutoSync = view.findViewById(R.id.setting_auto_sync);
        View containerAlerts = view.findViewById(R.id.setting_outbreak_alerts);
        View containerReminders = view.findViewById(R.id.setting_sync_reminders);
        View containerClearData = view.findViewById(R.id.setting_clear_data);

        // Find Switches inside containers
        if (containerAutoSync instanceof android.view.ViewGroup) {
            swAutoSync = findSwitch((android.view.ViewGroup) containerAutoSync);
        }
        if (containerAlerts instanceof android.view.ViewGroup) {
            swAlerts = findSwitch((android.view.ViewGroup) containerAlerts);
        }
        if (containerReminders instanceof android.view.ViewGroup) {
            swReminders = findSwitch((android.view.ViewGroup) containerReminders);
        }

        // Initialize States
        if (swAutoSync != null)
            swAutoSync.setChecked(sessionManager.isAutoSyncEnabled());
        if (swAlerts != null)
            swAlerts.setChecked(sessionManager.areOutbreakAlertsEnabled());
        if (swReminders != null)
            swReminders.setChecked(sessionManager.areSyncRemindersEnabled());

        // Listeners
        if (swAutoSync != null) {
            swAutoSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sessionManager.setAutoSync(isChecked);
                android.widget.Toast.makeText(getContext(), isChecked ? "Auto Sync Enabled" : "Auto Sync Disabled",
                        android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        if (swAlerts != null) {
            swAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> sessionManager.setOutbreakAlerts(isChecked));
        }

        if (swReminders != null) {
            swReminders
                    .setOnCheckedChangeListener((buttonView, isChecked) -> sessionManager.setSyncReminders(isChecked));
        }

        // Extended Settings Bindings
        View containerLanguage = view.findViewById(R.id.setting_language);
        View containerGps = view.findViewById(R.id.setting_gps_accuracy);
        View containerDataUsage = view.findViewById(R.id.setting_data_usage);

        // Initialize Extended State Text
        updateLanguageText(containerLanguage);
        updateGpsText(containerGps);
        updateDataUsageText(containerDataUsage);

        // Extended Listeners
        if (containerLanguage != null) {
            containerLanguage.setOnClickListener(v -> showLanguageDialog(containerLanguage));
        }

        if (containerGps != null) {
            containerGps.setOnClickListener(v -> showGpsDialog(containerGps));
        }

        if (containerDataUsage != null) {
            containerDataUsage.setOnClickListener(v -> showDataUsageDialog(containerDataUsage));
        }

        // Clear Data Listener
        if (containerClearData != null) {
            containerClearData.setOnClickListener(v -> showClearDataConfirmation());
        }
    }

    private com.google.android.material.switchmaterial.SwitchMaterial findSwitch(android.view.ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof com.google.android.material.switchmaterial.SwitchMaterial) {
                return (com.google.android.material.switchmaterial.SwitchMaterial) child;
            }
        }
        return null;
    }

    // --- Helper Methods to Find TextViews in Layout ---
    // Assuming structure: LinearLayout -> [ImageView, LinearLayout -> [Title,
    // Subtitle/Value], ...]
    // The second TextView in the inner layout is the "Value" or "Subtitle".
    private android.widget.TextView findSubtitleTextView(View container) {
        if (!(container instanceof android.view.ViewGroup))
            return null;
        android.view.ViewGroup root = (android.view.ViewGroup) container;

        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof android.view.ViewGroup) {
                // This is likely the text container
                android.view.ViewGroup textGroup = (android.view.ViewGroup) child;
                if (textGroup.getChildCount() >= 2) {
                    View possibleSubtitle = textGroup.getChildAt(1);
                    if (possibleSubtitle instanceof android.widget.TextView) {
                        return (android.widget.TextView) possibleSubtitle;
                    }
                }
            }
        }
        return null;
    }

    // --- Dialogs & Updates ---

    private void showLanguageDialog(View container) {
        String[] languages = { "English", "Kinyarwanda", "French" };
        String[] codes = { "en", "rw", "fr" };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Language")
                .setSingleChoiceItems(languages, -1, (dialog, which) -> {
                    String selectedCode = codes[which];
                    String selectedName = languages[which];

                    // Update Locale
                    com.healthtracker.chw.utils.LocaleHelper.setLocale(requireContext(), selectedCode);

                    // Update Session (optional, for display consistency)
                    sessionManager.setLanguage(selectedName);

                    updateLanguageText(container);
                    dialog.dismiss();

                    // Restart Activity to apply changes
                    requireActivity().recreate();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateLanguageText(View container) {
        android.widget.TextView subtitle = findSubtitleTextView(container);
        if (subtitle != null)
            subtitle.setText(sessionManager.getLanguage());
    }

    private void showGpsDialog(View container) {
        String[] modes = { "High Accuracy", "Balanced", "Power Save" };
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("GPS Accuracy")
                .setSingleChoiceItems(modes, -1, (dialog, which) -> {
                    sessionManager.setGpsAccuracy(modes[which]);
                    updateGpsText(container);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateGpsText(View container) {
        android.widget.TextView subtitle = findSubtitleTextView(container);
        if (subtitle != null)
            subtitle.setText(sessionManager.getGpsAccuracy());
    }

    private void showDataUsageDialog(View container) {
        String[] options = { "Normal Mode", "Low Bandwidth Mode" };
        int current = sessionManager.isLowBandwithModeEnabled() ? 1 : 0;

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Data Usage")
                .setSingleChoiceItems(options, current, (dialog, which) -> {
                    sessionManager.setLowBandwidthMode(which == 1);
                    updateDataUsageText(container);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateDataUsageText(View container) {
        android.widget.TextView subtitle = findSubtitleTextView(container);
        if (subtitle != null) {
            if (sessionManager.isLowBandwithModeEnabled()) {
                subtitle.setText("Low Bandwidth Mode (Optimized)");
            } else {
                subtitle.setText("Normal Mode");
            }
        }
    }

    private void showClearDataConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Clear Local Data")
                .setMessage("Are you sure you want to delete all locally stored reports? This action cannot be undone.")
                .setPositiveButton("Clear Data", (dialog, which) -> clearLocalData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearLocalData() {
        // Run database operation in background
        new Thread(() -> {
            com.healthtracker.chw.data.local.AppDatabase db = com.healthtracker.chw.data.local.AppDatabase
                    .getDatabase(requireContext());
            db.clearAllTables();

            requireActivity().runOnUiThread(() -> android.widget.Toast
                    .makeText(getContext(), "Local data cleared", android.widget.Toast.LENGTH_SHORT).show());
        }).start();
    }
}
