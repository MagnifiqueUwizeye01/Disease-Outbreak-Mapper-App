package com.healthtracker.chw.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Measure Report model
 * Represents aggregated disease case statistics over a time period
 * Used for creating real-time outbreak maps
 * 
 * Relationships:
 * - Aggregates many DiseaseReports (Many-to-Many through DiseaseReport)
 */
public class MeasureReport {
    private String measureId;
    private String reportType; // summary, individual
    private Date periodStart;
    private Date periodEnd;
    private int caseCount;
    private List<DiseaseReport> diseaseReports; // Many-to-Many relationship

    // Default constructor for Gson/Retrofit
    public MeasureReport() {
        this.diseaseReports = new ArrayList<>();
    }

    // Constructor with required fields
    public MeasureReport(String measureId, String reportType, Date periodStart, Date periodEnd, int caseCount) {
        this.measureId = measureId;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.caseCount = caseCount;
        this.diseaseReports = new ArrayList<>();
    }

    // Full constructor
    public MeasureReport(String measureId, String reportType, Date periodStart, Date periodEnd,
                         int caseCount, List<DiseaseReport> diseaseReports) {
        this.measureId = measureId;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.caseCount = caseCount;
        this.diseaseReports = diseaseReports != null ? diseaseReports : new ArrayList<>();
    }

    // Getters and Setters
    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public int getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(int caseCount) {
        this.caseCount = caseCount;
    }

    public List<DiseaseReport> getDiseaseReports() {
        return diseaseReports;
    }

    public void setDiseaseReports(List<DiseaseReport> diseaseReports) {
        this.diseaseReports = diseaseReports != null ? diseaseReports : new ArrayList<>();
    }

    public void addDiseaseReport(DiseaseReport diseaseReport) {
        if (diseaseReports == null) {
            diseaseReports = new ArrayList<>();
        }
        diseaseReports.add(diseaseReport);
    }
}

