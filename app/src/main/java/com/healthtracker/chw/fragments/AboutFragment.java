package com.healthtracker.chw.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AboutFragment extends Fragment {

    private TextView tvVersion;
    private TextView tvBuildDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        
        // Initialize views
        tvVersion = view.findViewById(R.id.tv_version);
        tvBuildDate = view.findViewById(R.id.tv_build_date);
        
        // Load app information
        loadAppInfo();
        
        return view;
    }

    private void loadAppInfo() {
        try {
            // Get app version
            PackageManager pm = requireContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(requireContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            
            if (tvVersion != null) {
                tvVersion.setText(String.format(Locale.getDefault(), "%s (%d)", versionName, versionCode));
            }
            
            // Get build date (use current date as fallback, or use packageInfo.lastUpdateTime)
            if (tvBuildDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String buildDate = dateFormat.format(new Date(packageInfo.lastUpdateTime));
                tvBuildDate.setText(buildDate);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Fallback values if package info not found
            if (tvVersion != null) {
                tvVersion.setText("1.0.0");
            }
            if (tvBuildDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                tvBuildDate.setText(dateFormat.format(new Date()));
            }
        }
    }
}
