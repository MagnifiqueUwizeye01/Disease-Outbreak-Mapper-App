package com.healthtracker.chw.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.healthtracker.chw.R;
import com.healthtracker.chw.data.local.UnsyncedReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OfflineCaseAdapter extends RecyclerView.Adapter<OfflineCaseAdapter.ViewHolder> {

    private List<UnsyncedReport> reports = new ArrayList<>();

    public void setReports(List<UnsyncedReport> reports) {
        this.reports = reports;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offline_case, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnsyncedReport report = reports.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiseaseName;
        Chip chipRiskLevel;
        TextView tvPatientInfo;
        TextView tvLocation;
        TextView tvSavedTime;
        Chip chipSyncStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvDiseaseName = itemView.findViewById(R.id.tv_disease_name);
            chipRiskLevel = itemView.findViewById(R.id.chip_risk_level);
            tvPatientInfo = itemView.findViewById(R.id.tv_patient_info);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvSavedTime = itemView.findViewById(R.id.tv_saved_time);
            chipSyncStatus = itemView.findViewById(R.id.chip_sync_status);
        }

        void bind(UnsyncedReport report) {
            tvDiseaseName.setText(report.diseaseType != null ? report.diseaseType : "Unknown Disease");

            // Risk Level
            if (report.severity != null) {
                chipRiskLevel.setText(report.severity.toUpperCase());
                // Simple color logic
                if (report.severity.toLowerCase().contains("high")
                        || report.severity.toLowerCase().contains("severe")) {
                    chipRiskLevel.setChipBackgroundColorResource(R.color.error); // Red-ish from design system usually
                } else if (report.severity.toLowerCase().contains("moderate")) {
                    chipRiskLevel.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                } else {
                    chipRiskLevel.setChipBackgroundColorResource(android.R.color.holo_green_light);
                }
            } else {
                chipRiskLevel.setVisibility(View.GONE);
            }

            // Patient Info
            String age = report.patientAge != null ? report.patientAge + "y" : "Age N/A";
            String gender = report.gender != null ? report.gender : "Unknown";
            tvPatientInfo.setText(age + ", " + gender);

            // Location
            tvLocation.setText(
                    report.address != null ? report.address : "Lat: " + report.latitude + ", Lon: " + report.longitude);

            // Time
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            tvSavedTime.setText("Saved: " + sdf.format(new Date(report.timestamp)));

            // Status is always PENDING for offline items
            chipSyncStatus.setText("PENDING");
            // chipSyncStatus.setChipBackgroundColorResource(R.color.medical_gold_primary);
            // // Assuming this resource exists, else default
        }
    }
}
