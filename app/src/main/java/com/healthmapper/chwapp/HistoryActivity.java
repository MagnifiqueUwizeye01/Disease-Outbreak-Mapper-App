package com.healthmapper.chwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.healthmapper.chwapp.data.entities.DiseaseReport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private List<DiseaseReport> reportsList;
    private List<DiseaseReport> filteredReports;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Reports History");
        setupUI();
        loadReportsHistory();
    }

    private void setupUI() {
        // Initialize UI components
        // For now, working without XML layout
        reportsList = new ArrayList<>();
        filteredReports = new ArrayList<>();
    }

    private void loadReportsHistory() {
        // Create sample disease reports for testing
        reportsList.clear();

        // Sample reports with different statuses
        reportsList.add(new DiseaseReport(
                "RPT001",
                "Cholera",
                new Date(),
                "SUBMITTED",
                25,
                "CHW001",
                -1.9403, 29.8739, // Kigali coordinates
                "Patient showed symptoms of severe dehydration"
        ));

        reportsList.add(new DiseaseReport(
                "RPT002",
                "Malaria",
                new Date(System.currentTimeMillis() - 86400000), // Yesterday
                "PENDING",
                8,
                "CHW001",
                -1.9500, 29.8700,
                "Child with high fever and chills"
        ));

        reportsList.add(new DiseaseReport(
                "RPT003",
                "Tuberculosis",
                new Date(System.currentTimeMillis() - 172800000), // 2 days ago
                "DRAFT",
                35,
                "CHW001",
                -1.9350, 29.8800,
                "Persistent cough for 3 weeks"
        ));

        reportsList.add(new DiseaseReport(
                "RPT004",
                "Measles",
                new Date(System.currentTimeMillis() - 259200000), // 3 days ago
                "SUBMITTED",
                5,
                "CHW001",
                -1.9450, 29.8650,
                "Rash appeared 2 days ago"
        ));

        updateFilteredReports("ALL");
        displayReportsSummary();
    }

    private void updateFilteredReports(String filter) {
        currentFilter = filter;
        filteredReports.clear();

        for (DiseaseReport report : reportsList) {
            if (filter.equals("ALL") || report.getStatus().equals(filter)) {
                filteredReports.add(report);
            }
        }

        displayReportsSummary();
    }

    private void displayReportsSummary() {
        int totalReports = reportsList.size();
        int submittedCount = getReportsByStatus("SUBMITTED").size();
        int pendingCount = getReportsByStatus("PENDING").size();
        int draftCount = getReportsByStatus("DRAFT").size();
        int currentFilterCount = filteredReports.size();

        String summary = "Disease Reports Summary:\n\n" +
                "Total Reports: " + totalReports + "\n" +
                "Submitted: " + submittedCount + "\n" +
                "Pending: " + pendingCount + "\n" +
                "Drafts: " + draftCount + "\n\n" +
                "Current Filter (" + currentFilter + "): " + currentFilterCount + " reports";

        Toast.makeText(this, "History loaded - " + currentFilterCount + " reports in " + currentFilter + " filter", Toast.LENGTH_LONG).show();
    }

    public void filterReports(String status) {
        updateFilteredReports(status);

        String filterMessage = "Showing " + status.toLowerCase() + " reports: " + filteredReports.size() + " found";
        Toast.makeText(this, filterMessage, Toast.LENGTH_SHORT).show();
    }

    public void refreshReports() {
        Toast.makeText(this, "Refreshing reports...", Toast.LENGTH_SHORT).show();
        loadReportsHistory();
    }

    public void openReportDetail(DiseaseReport report) {
        if (report == null) return;

        String reportDetails = "Report Details:\n\n" +
                "ID: " + report.getReportId() + "\n" +
                "Disease: " + report.getDiseaseType() + "\n" +
                "Status: " + report.getStatus() + "\n" +
                "Patient Age: " + report.getPatientAge() + "\n" +
                "Location: " + report.getLatitude() + ", " + report.getLongitude() + "\n" +
                "Notes: " + report.getNotes();

        Toast.makeText(this, "Opening report detail for " + report.getDiseaseType(), Toast.LENGTH_SHORT).show();

        // TODO: Open detailed report view
    }

    public void editReport(DiseaseReport report) {
        if (report == null) return;

        if ("SUBMITTED".equals(report.getStatus())) {
            Toast.makeText(this, "Cannot edit submitted reports", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Opening editor for " + report.getDiseaseType() + " report", Toast.LENGTH_SHORT).show();

        // TODO: Open report editor
    }

    public void deleteReport(DiseaseReport report) {
        if (report == null) return;

        if ("SUBMITTED".equals(report.getStatus())) {
            Toast.makeText(this, "Cannot delete submitted reports", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove from list
        reportsList.remove(report);
        updateFilteredReports(currentFilter);

        Toast.makeText(this, "Report deleted: " + report.getDiseaseType(), Toast.LENGTH_SHORT).show();
    }

    // Utility methods
    private List<DiseaseReport> getReportsByStatus(String status) {
        List<DiseaseReport> filtered = new ArrayList<>();
        for (DiseaseReport report : reportsList) {
            if (report.getStatus().equals(status)) {
                filtered.add(report);
            }
        }
        return filtered;
    }

    // Public methods for filter functionality
    public void showAllReports() {
        filterReports("ALL");
    }

    public void showPendingReports() {
        filterReports("PENDING");
    }

    public void showSubmittedReports() {
        filterReports("SUBMITTED");
    }

    public void showDraftReports() {
        filterReports("DRAFT");
    }

    // Getters for testing
    public List<DiseaseReport> getAllReports() {
        return new ArrayList<>(reportsList);
    }

    public List<DiseaseReport> getCurrentFilteredReports() {
        return new ArrayList<>(filteredReports);
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public int getReportCount() {
        return reportsList.size();
    }
}