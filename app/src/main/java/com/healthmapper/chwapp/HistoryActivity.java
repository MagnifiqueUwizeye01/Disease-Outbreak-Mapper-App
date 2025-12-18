package com.healthmapper.chwapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.healthmapper.chwapp.data.entities.DiseaseReport;
import com.healthmapper.chwapp.utils.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private List<DiseaseReport> reportsList;
    private List<DiseaseReport> filteredReports;
    private String currentFilter = "ALL";
    private PreferenceManager preferenceManager;

    private LinearLayout reportsContainer;
    private Button btnAll, btnPending, btnSubmitted, btnDraft;
    private TextView statsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            preferenceManager = new PreferenceManager(this);
            createHistoryUI();
            loadReportsHistory();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void createHistoryUI() {
        setTitle("Reports History - Prince Bimenyimana");

        // Main scroll container
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#FAFAFA"));

        // Main container
        LinearLayout mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);

        // Header section
        LinearLayout headerSection = createHistoryHeader();

        // Stats section
        LinearLayout statsSection = createStatsSection();

        // Filter buttons section
        LinearLayout filterSection = createFilterSection();

        // Reports container
        reportsContainer = new LinearLayout(this);
        reportsContainer.setOrientation(LinearLayout.VERTICAL);
        reportsContainer.setPadding(20, 20, 20, 20);

        mainContainer.addView(headerSection);
        mainContainer.addView(statsSection);
        mainContainer.addView(filterSection);
        mainContainer.addView(reportsContainer);

        scrollView.addView(mainContainer);
        setContentView(scrollView);
    }

    private LinearLayout createHistoryHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(20, 40, 20, 30);
        header.setBackgroundColor(Color.parseColor("#2E7D32"));

        // Back button
        Button backButton = new Button(this);
        backButton.setText("← Back");
        backButton.setTextColor(Color.WHITE);
        backButton.setBackgroundColor(Color.TRANSPARENT);
        backButton.setOnClickListener(v -> finish());

        // Title section
        LinearLayout titleSection = new LinearLayout(this);
        titleSection.setOrientation(LinearLayout.VERTICAL);
        titleSection.setPadding(20, 0, 0, 0);

        TextView title = new TextView(this);
        title.setText("📋 Disease Reports");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);

        TextView subtitle = new TextView(this);
        subtitle.setText("Manage your submitted reports");
        subtitle.setTextColor(Color.parseColor("#C8E6C9"));
        subtitle.setTextSize(16);

        titleSection.addView(title);
        titleSection.addView(subtitle);

        header.addView(backButton);
        header.addView(titleSection);

        return header;
    }

    private LinearLayout createStatsSection() {
        LinearLayout statsSection = new LinearLayout(this);
        statsSection.setOrientation(LinearLayout.VERTICAL);
        statsSection.setPadding(20, 0, 20, 20);

        // Stats card
        LinearLayout statsCard = new LinearLayout(this);
        statsCard.setOrientation(LinearLayout.VERTICAL);
        statsCard.setPadding(25, 25, 25, 25);
        statsCard.setBackgroundColor(Color.WHITE);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(15);
        cardBg.setStroke(1, Color.parseColor("#E0E0E0"));
        statsCard.setBackground(cardBg);

        statsText = new TextView(this);
        statsText.setText("📊 Loading reports...");
        statsText.setTextSize(16);
        statsText.setTextColor(Color.parseColor("#333333"));

        statsCard.addView(statsText);

        // Add margin for shadow effect
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, -15, 0, 0);

        statsSection.addView(statsCard, cardParams);

        return statsSection;
    }

    private LinearLayout createFilterSection() {
        LinearLayout filterSection = new LinearLayout(this);
        filterSection.setOrientation(LinearLayout.VERTICAL);
        filterSection.setPadding(20, 20, 20, 20);

        TextView filterTitle = new TextView(this);
        filterTitle.setText("🔍 Filter Reports");
        filterTitle.setTextSize(18);
        filterTitle.setTextColor(Color.parseColor("#333333"));
        filterTitle.setPadding(0, 0, 0, 15);

        // Filter buttons row
        LinearLayout filterRow = new LinearLayout(this);
        filterRow.setOrientation(LinearLayout.HORIZONTAL);
        filterRow.setWeightSum(4);

        btnAll = createFilterButton("All", "#2196F3", true);
        btnPending = createFilterButton("Pending", "#FF9800", false);
        btnSubmitted = createFilterButton("Submitted", "#4CAF50", false);
        btnDraft = createFilterButton("Draft", "#9E9E9E", false);

        btnAll.setOnClickListener(v -> filterReports("ALL"));
        btnPending.setOnClickListener(v -> filterReports("PENDING"));
        btnSubmitted.setOnClickListener(v -> filterReports("SUBMITTED"));
        btnDraft.setOnClickListener(v -> filterReports("DRAFT"));

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        buttonParams.setMargins(3, 0, 3, 0);

        filterRow.addView(btnAll, buttonParams);
        filterRow.addView(btnPending, buttonParams);
        filterRow.addView(btnSubmitted, buttonParams);
        filterRow.addView(btnDraft, buttonParams);

        filterSection.addView(filterTitle);
        filterSection.addView(filterRow);

        return filterSection;
    }

    private Button createFilterButton(String text, String color, boolean active) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(active ? Color.WHITE : Color.parseColor("#666666"));
        button.setBackgroundColor(active ? Color.parseColor(color) : Color.parseColor("#F0F0F0"));
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setPadding(15, 10, 15, 10);

        return button;
    }

    private void loadReportsHistory() {
        try {
            reportsList = new ArrayList<>();
            filteredReports = new ArrayList<>();

            // Create sample disease reports with realistic data
            reportsList.add(new DiseaseReport(
                    "RPT001",
                    "Cholera",
                    new Date(),
                    "SUBMITTED",
                    25,
                    "CHW001",
                    -1.9441, 30.0619, // Kigali coordinates
                    "Patient showed symptoms of severe dehydration and diarrhea. Referred to health center."
            ));

            reportsList.add(new DiseaseReport(
                    "RPT002",
                    "Malaria",
                    new Date(System.currentTimeMillis() - 86400000), // Yesterday
                    "PENDING",
                    8,
                    "CHW001",
                    -1.9500, 29.8700,
                    "Child with high fever (39°C) and chills. Rapid test positive for malaria."
            ));

            reportsList.add(new DiseaseReport(
                    "RPT003",
                    "Tuberculosis",
                    new Date(System.currentTimeMillis() - 172800000), // 2 days ago
                    "DRAFT",
                    35,
                    "CHW001",
                    -1.9350, 29.8800,
                    "Persistent cough for 3 weeks, weight loss, night sweats. Needs further investigation."
            ));

            reportsList.add(new DiseaseReport(
                    "RPT004",
                    "Measles",
                    new Date(System.currentTimeMillis() - 259200000), // 3 days ago
                    "SUBMITTED",
                    5,
                    "CHW001",
                    -1.9450, 29.8650,
                    "Rash appeared 2 days ago, fever, cough. Child not vaccinated."
            ));

            updateStats();
            filterReports("ALL");

        } catch (Exception e) {
            Toast.makeText(this, "Error loading reports: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStats() {
        int total = reportsList.size();
        int submitted = 0;
        int pending = 0;
        int draft = 0;

        for (DiseaseReport report : reportsList) {
            switch (report.getStatus()) {
                case "SUBMITTED": submitted++; break;
                case "PENDING": pending++; break;
                case "DRAFT": draft++; break;
            }
        }

        String statsInfo = String.format(
                "📊 Total Reports: %d • ✅ Submitted: %d • ⏳ Pending: %d • 📝 Draft: %d",
                total, submitted, pending, draft
        );

        statsText.setText(statsInfo);

        // Update filter button texts
        btnAll.setText("All (" + total + ")");
        btnPending.setText("Pending (" + pending + ")");
        btnSubmitted.setText("Submitted (" + submitted + ")");
        btnDraft.setText("Draft (" + draft + ")");
    }

    private void filterReports(String status) {
        currentFilter = status;
        filteredReports.clear();

        for (DiseaseReport report : reportsList) {
            if (status.equals("ALL") || report.getStatus().equals(status)) {
                filteredReports.add(report);
            }
        }

        updateFilterButtons();
        displayReports();
    }

    private void updateFilterButtons() {
        // Reset all buttons
        resetFilterButton(btnAll);
        resetFilterButton(btnPending);
        resetFilterButton(btnSubmitted);
        resetFilterButton(btnDraft);

        // Activate current filter button
        Button activeButton = null;
        String color = "#2196F3";

        switch (currentFilter) {
            case "ALL":
                activeButton = btnAll;
                color = "#2196F3";
                break;
            case "PENDING":
                activeButton = btnPending;
                color = "#FF9800";
                break;
            case "SUBMITTED":
                activeButton = btnSubmitted;
                color = "#4CAF50";
                break;
            case "DRAFT":
                activeButton = btnDraft;
                color = "#9E9E9E";
                break;
        }

        if (activeButton != null) {
            activeButton.setTextColor(Color.WHITE);
            activeButton.setBackgroundColor(Color.parseColor(color));
        }
    }

    private void resetFilterButton(Button button) {
        button.setTextColor(Color.parseColor("#666666"));
        button.setBackgroundColor(Color.parseColor("#F0F0F0"));
    }

    private void displayReports() {
        reportsContainer.removeAllViews();

        if (filteredReports.isEmpty()) {
            // Show empty state
            LinearLayout emptyState = createEmptyState();
            reportsContainer.addView(emptyState);
        } else {
            // Show reports
            for (DiseaseReport report : filteredReports) {
                LinearLayout reportCard = createReportCard(report);
                reportsContainer.addView(reportCard);
            }
        }
    }

    private LinearLayout createEmptyState() {
        LinearLayout emptyState = new LinearLayout(this);
        emptyState.setOrientation(LinearLayout.VERTICAL);
        emptyState.setGravity(Gravity.CENTER);
        emptyState.setPadding(40, 60, 40, 60);

        TextView emptyIcon = new TextView(this);
        emptyIcon.setText("📭");
        emptyIcon.setTextSize(48);
        emptyIcon.setGravity(Gravity.CENTER);

        TextView emptyTitle = new TextView(this);
        emptyTitle.setText("No Reports Found");
        emptyTitle.setTextSize(20);
        emptyTitle.setTextColor(Color.parseColor("#666666"));
        emptyTitle.setGravity(Gravity.CENTER);

        TextView emptyMessage = new TextView(this);
        emptyMessage.setText("No reports match the current filter criteria.");
        emptyMessage.setTextSize(16);
        emptyMessage.setTextColor(Color.parseColor("#999999"));
        emptyMessage.setGravity(Gravity.CENTER);

        emptyState.addView(emptyIcon);
        emptyState.addView(emptyTitle);
        emptyState.addView(emptyMessage);

        return emptyState;
    }

    private LinearLayout createReportCard(DiseaseReport report) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 20, 25, 20);

        // Card styling
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(12);
        cardBg.setStroke(1, Color.parseColor("#E0E0E0"));
        card.setBackground(cardBg);

        // Header row
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);

        // Disease info
        LinearLayout diseaseInfo = new LinearLayout(this);
        diseaseInfo.setOrientation(LinearLayout.VERTICAL);

        TextView diseaseType = new TextView(this);
        diseaseType.setText(getDiseaseIcon(report.getDiseaseType()) + " " + report.getDiseaseType());
        diseaseType.setTextSize(18);
        diseaseType.setTextColor(Color.parseColor("#333333"));
        diseaseType.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView reportDate = new TextView(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        reportDate.setText("📅 " + dateFormat.format(report.getReportDate()));
        reportDate.setTextSize(14);
        reportDate.setTextColor(Color.parseColor("#666666"));

        diseaseInfo.addView(diseaseType);
        diseaseInfo.addView(reportDate);

        // Status badge
        TextView statusBadge = createStatusBadge(report.getStatus());

        LinearLayout.LayoutParams diseaseParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        headerRow.addView(diseaseInfo, diseaseParams);
        headerRow.addView(statusBadge);

        // Details row
        LinearLayout detailsRow = new LinearLayout(this);
        detailsRow.setOrientation(LinearLayout.VERTICAL);
        detailsRow.setPadding(0, 15, 0, 0);

        TextView patientInfo = new TextView(this);
        patientInfo.setText("👤 Patient Age: " + report.getPatientAge() + " years");
        patientInfo.setTextSize(14);
        patientInfo.setTextColor(Color.parseColor("#666666"));

        TextView locationInfo = new TextView(this);
        locationInfo.setText("📍 Location: " + String.format("%.4f, %.4f", report.getLatitude(), report.getLongitude()));
        locationInfo.setTextSize(14);
        locationInfo.setTextColor(Color.parseColor("#666666"));

        TextView notesInfo = new TextView(this);
        notesInfo.setText("📝 " + report.getNotes());
        notesInfo.setTextSize(14);
        notesInfo.setTextColor(Color.parseColor("#444444"));
        notesInfo.setPadding(0, 8, 0, 0);

        detailsRow.addView(patientInfo);
        detailsRow.addView(locationInfo);
        detailsRow.addView(notesInfo);

        // Action buttons
        LinearLayout actionsRow = createActionButtons(report);

        card.addView(headerRow);
        card.addView(detailsRow);
        card.addView(actionsRow);

        // Add margin between cards
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 15);
        card.setLayoutParams(cardParams);

        return card;
    }

    private String getDiseaseIcon(String disease) {
        switch (disease.toLowerCase()) {
            case "cholera": return "🦠";
            case "malaria": return "🦟";
            case "tuberculosis": return "🫁";
            case "measles": return "🔴";
            default: return "🏥";
        }
    }

    private TextView createStatusBadge(String status) {
        TextView badge = new TextView(this);
        badge.setText(status);
        badge.setTextSize(12);
        badge.setTextColor(Color.WHITE);
        badge.setPadding(15, 8, 15, 8);
        badge.setTypeface(null, android.graphics.Typeface.BOLD);

        int color;
        switch (status) {
            case "SUBMITTED": color = Color.parseColor("#4CAF50"); break;
            case "PENDING": color = Color.parseColor("#FF9800"); break;
            case "DRAFT": color = Color.parseColor("#9E9E9E"); break;
            default: color = Color.parseColor("#666666"); break;
        }

        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(color);
        badgeBg.setCornerRadius(15);
        badge.setBackground(badgeBg);

        return badge;
    }

    private LinearLayout createActionButtons(DiseaseReport report) {
        LinearLayout actionsRow = new LinearLayout(this);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);
        actionsRow.setPadding(0, 15, 0, 0);
        actionsRow.setGravity(Gravity.END);

        if (!"SUBMITTED".equals(report.getStatus())) {
            Button editButton = new Button(this);
            editButton.setText("✏️ Edit");
            editButton.setTextSize(12);
            editButton.setBackgroundColor(Color.parseColor("#2196F3"));
            editButton.setTextColor(Color.WHITE);
            editButton.setAllCaps(false);
            editButton.setPadding(20, 10, 20, 10);
            editButton.setOnClickListener(v -> editReport(report));

            Button deleteButton = new Button(this);
            deleteButton.setText("🗑️ Delete");
            deleteButton.setTextSize(12);
            deleteButton.setBackgroundColor(Color.parseColor("#F44336"));
            deleteButton.setTextColor(Color.WHITE);
            deleteButton.setAllCaps(false);
            deleteButton.setPadding(20, 10, 20, 10);
            deleteButton.setOnClickListener(v -> deleteReport(report));

            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.setMargins(5, 0, 0, 0);

            actionsRow.addView(editButton);
            actionsRow.addView(deleteButton, buttonParams);
        } else {
            TextView submittedNote = new TextView(this);
            submittedNote.setText("✅ Report submitted - no actions available");
            submittedNote.setTextSize(12);
            submittedNote.setTextColor(Color.parseColor("#4CAF50"));
            submittedNote.setTypeface(null, android.graphics.Typeface.ITALIC);

            actionsRow.addView(submittedNote);
        }

        return actionsRow;
    }

    private void editReport(DiseaseReport report) {
        Toast.makeText(this, "Edit functionality for " + report.getDiseaseType() + " report - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void deleteReport(DiseaseReport report) {
        // Simple delete confirmation
        reportsList.remove(report);
        updateStats();
        filterReports(currentFilter);
        Toast.makeText(this, "Report deleted: " + report.getDiseaseType(), Toast.LENGTH_SHORT).show();
    }
}