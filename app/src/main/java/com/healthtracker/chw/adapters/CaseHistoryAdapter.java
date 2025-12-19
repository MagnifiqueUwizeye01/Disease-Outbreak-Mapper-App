package com.healthtracker.chw.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.healthtracker.chw.R;
import com.healthtracker.chw.models.DiseaseReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaseHistoryAdapter extends RecyclerView.Adapter<CaseHistoryAdapter.CaseViewHolder> {

    private List<DiseaseReport> cases;
    private OnCaseActionListener listener;

    public interface OnCaseActionListener {
        void onCaseClick(String reportId);

        void onEditClick(String reportId);

        void onDeleteClick(String reportId);
    }

    public CaseHistoryAdapter(OnCaseActionListener listener) {
        this.cases = new ArrayList<>();
        this.listener = listener;
    }

    public void setCases(List<DiseaseReport> cases) {
        this.cases = cases != null ? cases : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_case_history, parent, false);
        return new CaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseViewHolder holder, int position) {
        DiseaseReport report = cases.get(position);
        holder.bind(report);
    }

    @Override
    public int getItemCount() {
        return cases.size();
    }

    class CaseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDiseaseName;
        private Chip chipRiskLevel;
        private android.widget.ImageButton btnMoreOptions;
        private TextView tvPatientInfo;
        private TextView tvLocation;
        private TextView tvReportDate;
        private Chip chipStatus;

        CaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiseaseName = itemView.findViewById(R.id.tv_disease_name);
            chipRiskLevel = itemView.findViewById(R.id.chip_risk_level);
            btnMoreOptions = itemView.findViewById(R.id.btn_more_options);
            tvPatientInfo = itemView.findViewById(R.id.tv_patient_info);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvReportDate = itemView.findViewById(R.id.tv_report_date);
            chipStatus = itemView.findViewById(R.id.chip_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    DiseaseReport report = cases.get(position);
                    if (report != null && report.getReportId() != null) {
                        listener.onCaseClick(report.getReportId());
                    }
                }
            });

            btnMoreOptions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    DiseaseReport report = cases.get(position);
                    if (report != null && report.getReportId() != null) {
                        android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                        popup.getMenu().add("Edit");
                        popup.getMenu().add("Delete");
                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getTitle().equals("Edit")) {
                                listener.onEditClick(report.getReportId());
                            } else if (item.getTitle().equals("Delete")) {
                                listener.onDeleteClick(report.getReportId());
                            }
                            return true;
                        });
                        popup.show();
                    }
                }
            });
        }

        void bind(DiseaseReport report) {
            // Disease name
            String diseaseType = report.getDiseaseType();
            if (diseaseType == null || diseaseType.isEmpty()) {
                diseaseType = "Unknown Disease";
            }
            tvDiseaseName.setText(diseaseType);

            // Risk level
            String riskLevel = "LOW";
            int riskColor = android.graphics.Color.parseColor("#4CAF50"); // Green for low
            if (report.getRiskAssessment() != null && report.getRiskAssessment().getLevel() != null) {
                String level = report.getRiskAssessment().getLevel().toUpperCase();
                if (level.contains("HIGH") || level.contains("SEVERE")) {
                    riskLevel = "HIGH";
                    riskColor = android.graphics.Color.parseColor("#F44336"); // Red
                } else if (level.contains("MEDIUM") || level.contains("MODERATE")) {
                    riskLevel = "MEDIUM";
                    riskColor = android.graphics.Color.parseColor("#FF9800"); // Orange
                } else {
                    riskLevel = "LOW";
                }
            }
            chipRiskLevel.setText(riskLevel);
            chipRiskLevel.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(riskColor));

            // Patient info
            String patientInfo = "N/A";
            if (report.getEncounter() != null && report.getEncounter().getPatient() != null) {
                com.healthtracker.chw.models.Patient patient = report.getEncounter().getPatient();
                StringBuilder info = new StringBuilder();
                // Calculate age from date of birth if available
                if (patient.getDateOfBirth() != null) {
                    Date dob = patient.getDateOfBirth();
                    int age = calculateAge(dob);
                    if (age > 0) {
                        info.append(age).append("y");
                    }
                }
                if (patient.getGender() != null) {
                    if (info.length() > 0)
                        info.append(", ");
                    String gender = patient.getGender();
                    // Capitalize first letter
                    if (gender.length() > 0) {
                        gender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
                    }
                    info.append(gender);
                }
                if (info.length() > 0) {
                    patientInfo = info.toString();
                }
            }
            tvPatientInfo.setText(patientInfo);

            // Location
            String location = "Unknown";
            if (report.getEncounter() != null && report.getEncounter().getGpsLocation() != null) {
                Double lat = report.getEncounter().getGpsLocation().getLatitude();
                Double lng = report.getEncounter().getGpsLocation().getLongitude();
                if (lat != null && lng != null) {
                    location = String.format(Locale.getDefault(), "%.4f, %.4f", lat, lng);
                }
            }
            tvLocation.setText(location);

            // Report date
            String dateText = "Unknown date";
            Date reportDate = report.getReportDate();
            if (reportDate == null && report.getEncounter() != null) {
                reportDate = report.getEncounter().getEncounterDate();
            }
            if (reportDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                dateText = dateFormat.format(reportDate);
            }
            tvReportDate.setText(dateText);

            // Status
            String status = report.getStatus() != null ? report.getStatus().toUpperCase() : "SYNCED";
            chipStatus.setText(status);
            if ("SYNCED".equals(status) || "COMPLETED".equals(status)) {
                chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#4CAF50")));
            } else {
                chipStatus.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#FF9800")));
            }
        }

        private int calculateAge(Date dateOfBirth) {
            if (dateOfBirth == null)
                return 0;
            Date today = new Date();
            long diff = today.getTime() - dateOfBirth.getTime();
            return (int) (diff / (1000L * 60 * 60 * 24 * 365));
        }
    }
}
