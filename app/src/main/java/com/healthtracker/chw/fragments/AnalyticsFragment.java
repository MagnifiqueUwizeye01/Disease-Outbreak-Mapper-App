package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.healthtracker.chw.R;
import com.healthtracker.chw.models.DiseaseReport;
import com.healthtracker.chw.models.RiskAssessment;
import com.healthtracker.chw.services.SupabaseService;

import com.healthtracker.chw.models.GPSLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AnalyticsFragment extends Fragment {

    private SupabaseService supabaseService;
    private LinearLayout weeklyChartContainer;
    private LinearLayout diseaseLegendContainer;
    private TextView tvLowRiskCount;
    private TextView tvMediumRiskCount;
    private TextView tvHighRiskCount;
    private TextView tvTotalCases;
    private TextView tvActiveZones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        
        supabaseService = new SupabaseService(requireContext());
        
        // Find views by traversing the view hierarchy
        findViewsByTraversing(view);
        
        // Load analytics data
        loadAnalyticsData();
        
        return view;
    }

    private void findViewsByTraversing(View rootView) {
        // Get the ScrollView's child (LinearLayout)
        ViewGroup scrollView = (ViewGroup) rootView;
        if (scrollView.getChildCount() == 0) return;
        
        LinearLayout mainLayout = (LinearLayout) scrollView.getChildAt(0);
        
        // Find weekly chart container (first card after header)
        if (mainLayout.getChildCount() > 1) {
            View weeklyCard = mainLayout.getChildAt(1);
            if (weeklyCard instanceof ViewGroup) {
                ViewGroup cardContent = (ViewGroup) ((ViewGroup) weeklyCard).getChildAt(0);
                if (cardContent instanceof LinearLayout && cardContent.getChildCount() > 1) {
                    weeklyChartContainer = (LinearLayout) cardContent.getChildAt(1);
                }
            }
        }
        
        // Find disease legend container (second card)
        if (mainLayout.getChildCount() > 2) {
            View diseaseCard = mainLayout.getChildAt(2);
            if (diseaseCard instanceof ViewGroup) {
                ViewGroup cardContent = (ViewGroup) ((ViewGroup) diseaseCard).getChildAt(0);
                if (cardContent instanceof LinearLayout && cardContent.getChildCount() > 1) {
                    LinearLayout chartRow = (LinearLayout) cardContent.getChildAt(1);
                    if (chartRow.getChildCount() > 1) {
                        diseaseLegendContainer = (LinearLayout) chartRow.getChildAt(1);
                    }
                }
            }
        }
        
        // Find risk level views (third card)
        if (mainLayout.getChildCount() > 3) {
            View riskCard = mainLayout.getChildAt(3);
            if (riskCard instanceof ViewGroup) {
                ViewGroup cardContent = (ViewGroup) ((ViewGroup) riskCard).getChildAt(0);
                if (cardContent instanceof LinearLayout && cardContent.getChildCount() > 1) {
                    LinearLayout riskLayout = (LinearLayout) cardContent.getChildAt(1);
                    if (riskLayout.getChildCount() >= 3) {
                        LinearLayout lowRisk = (LinearLayout) riskLayout.getChildAt(0);
                        LinearLayout mediumRisk = (LinearLayout) riskLayout.getChildAt(1);
                        LinearLayout highRisk = (LinearLayout) riskLayout.getChildAt(2);
                        
                        if (lowRisk.getChildCount() > 0) {
                            tvLowRiskCount = (TextView) lowRisk.getChildAt(0);
                        }
                        if (mediumRisk.getChildCount() > 0) {
                            tvMediumRiskCount = (TextView) mediumRisk.getChildAt(0);
                        }
                        if (highRisk.getChildCount() > 0) {
                            tvHighRiskCount = (TextView) highRisk.getChildAt(0);
                        }
                    }
                }
            }
        }
        
        // Find total cases and active zones (last LinearLayout)
        for (int i = mainLayout.getChildCount() - 1; i >= 0; i--) {
            View child = mainLayout.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout statsRow = (LinearLayout) child;
                if (statsRow.getChildCount() >= 2) {
                    View totalCard = statsRow.getChildAt(0);
                    View zonesCard = statsRow.getChildAt(1);
                    
                    if (totalCard instanceof ViewGroup) {
                        ViewGroup totalCardContent = (ViewGroup) totalCard;
                        if (totalCardContent.getChildCount() > 0) {
                            LinearLayout totalContent = (LinearLayout) totalCardContent.getChildAt(0);
                            if (totalContent.getChildCount() > 0) {
                                tvTotalCases = (TextView) totalContent.getChildAt(0);
                            }
                        }
                    }
                    
                    if (zonesCard instanceof ViewGroup) {
                        ViewGroup zonesCardContent = (ViewGroup) zonesCard;
                        if (zonesCardContent.getChildCount() > 0) {
                            LinearLayout zonesContent = (LinearLayout) zonesCardContent.getChildAt(0);
                            if (zonesContent.getChildCount() > 0) {
                                tvActiveZones = (TextView) zonesContent.getChildAt(0);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void loadAnalyticsData() {
        supabaseService.getAllDiseaseReports(new SupabaseService.ReportsCallback() {
            @Override
            public void onSuccess(List<DiseaseReport> reports) {
                requireActivity().runOnUiThread(() -> {
                    AnalyticsData data = calculateAnalytics(reports);
                    updateAnalyticsUI(data, reports);
                });
            }
            
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    android.util.Log.e("AnalyticsFragment", "Error loading analytics: " + error);
                    // Show empty data
                    AnalyticsData emptyData = new AnalyticsData();
                    updateAnalyticsUI(emptyData, new ArrayList<>());
                });
            }
        });
    }
    
    private AnalyticsData calculateAnalytics(List<DiseaseReport> reports) {
        AnalyticsData data = new AnalyticsData();
        data.allReports = reports != null ? reports : new ArrayList<>();
        data.totalCaseCount = data.allReports.size();
        
        // Count risk levels from risk assessments
        Map<String, Integer> riskCounts = new HashMap<>();
        riskCounts.put("low", 0);
        riskCounts.put("medium", 0);
        riskCounts.put("high", 0);
        
        // Count disease distribution
        Map<String, Integer> diseaseCounts = new HashMap<>();
        Set<String> uniqueZones = new HashSet<>();
        
        for (DiseaseReport report : data.allReports) {
            // Count diseases
            String diseaseType = report.getDiseaseType() != null ? report.getDiseaseType() : "Unknown";
            diseaseCounts.put(diseaseType, diseaseCounts.getOrDefault(diseaseType, 0) + 1);
            
            // Count risk levels (from risk assessment if available)
            if (report.getRiskAssessment() != null && report.getRiskAssessment().getLevel() != null) {
                String level = report.getRiskAssessment().getLevel().toLowerCase();
                if (level.contains("low")) riskCounts.put("low", riskCounts.get("low") + 1);
                else if (level.contains("medium") || level.contains("moderate")) riskCounts.put("medium", riskCounts.get("medium") + 1);
                else if (level.contains("high") || level.contains("severe")) riskCounts.put("high", riskCounts.get("high") + 1);
            }
            
            // Count unique zones (from encounter location if available)
            if (report.getEncounter() != null && report.getEncounter().getGpsLocation() != null) {
                GPSLocation loc = report.getEncounter().getGpsLocation();
                if (loc.getLatitude() != null && loc.getLongitude() != null) {
                    // Create zone identifier (rounded coordinates)
                    String zone = String.format(Locale.getDefault(), "%.2f,%.2f", 
                        Math.round(loc.getLatitude() * 100.0) / 100.0,
                        Math.round(loc.getLongitude() * 100.0) / 100.0);
                    uniqueZones.add(zone);
                }
            }
        }
        
        data.lowRiskCount = riskCounts.get("low");
        data.mediumRiskCount = riskCounts.get("medium");
        data.highRiskCount = riskCounts.get("high");
        data.activeZoneCount = uniqueZones.size();
        
        // Convert disease counts to list
        data.diseaseDistribution = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : diseaseCounts.entrySet()) {
            DiseaseCount dc = new DiseaseCount();
            dc.disease_type = entry.getKey();
            dc.count = entry.getValue();
            data.diseaseDistribution.add(dc);
        }
        
        return data;
    }

    private void updateAnalyticsUI(AnalyticsData data, List<DiseaseReport> reports) {
        // Update risk levels
        if (tvLowRiskCount != null) {
            tvLowRiskCount.setText(String.valueOf(data.lowRiskCount));
        }
        if (tvMediumRiskCount != null) {
            tvMediumRiskCount.setText(String.valueOf(data.mediumRiskCount));
        }
        if (tvHighRiskCount != null) {
            tvHighRiskCount.setText(String.valueOf(data.highRiskCount));
        }
        
        // Update total cases and active zones
        if (tvTotalCases != null) {
            tvTotalCases.setText(String.valueOf(data.totalCaseCount));
        }
        if (tvActiveZones != null) {
            tvActiveZones.setText(String.valueOf(data.activeZoneCount));
        }
        
        // Update weekly chart
        updateWeeklyChart(reports);
        
        // Update disease distribution
        updateDiseaseDistribution(data.diseaseDistribution);
    }
    
    private static class AnalyticsData {
        List<DiseaseReport> allReports = new ArrayList<>();
        int lowRiskCount = 0;
        int mediumRiskCount = 0;
        int highRiskCount = 0;
        int totalCaseCount = 0;
        int activeZoneCount = 0;
        List<DiseaseCount> diseaseDistribution = new ArrayList<>();
    }
    
    private static class DiseaseCount {
        String disease_type;
        int count;
    }

    private void updateWeeklyChart(List<DiseaseReport> reports) {
        if (weeklyChartContainer == null || reports == null) return;
        
        // Calculate cases per day of week (last 7 days, Mon-Fri)
        Calendar cal = Calendar.getInstance();
        Map<Integer, Integer> dayCounts = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            dayCounts.put(i, 0);
        }
        
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.add(Calendar.DAY_OF_YEAR, -7);
        
        for (DiseaseReport report : reports) {
            Date reportDate = report.getReportDate() != null ? report.getReportDate() : 
                (report.getEncounter() != null && report.getEncounter().getEncounterDate() != null ? 
                    report.getEncounter().getEncounterDate() : null);
            
            if (reportDate != null && reportDate.after(weekAgo.getTime())) {
                cal.setTime(reportDate);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                // Convert to 0-4 (Monday=0, Tuesday=1, ..., Friday=4)
                // Calendar: Sunday=1, Monday=2, ..., Saturday=7
                int adjustedDay = -1;
                if (dayOfWeek == Calendar.MONDAY) adjustedDay = 0;
                else if (dayOfWeek == Calendar.TUESDAY) adjustedDay = 1;
                else if (dayOfWeek == Calendar.WEDNESDAY) adjustedDay = 2;
                else if (dayOfWeek == Calendar.THURSDAY) adjustedDay = 3;
                else if (dayOfWeek == Calendar.FRIDAY) adjustedDay = 4;
                
                if (adjustedDay >= 0) {
                    dayCounts.put(adjustedDay, dayCounts.get(adjustedDay) + 1);
                }
            }
        }
        
        // Find max value for scaling
        int maxCount = 0;
        for (Integer count : dayCounts.values()) {
            if (count > maxCount) maxCount = count;
        }
        if (maxCount == 0) maxCount = 1; // Avoid division by zero
        
        // Update chart bars (Mon=0, Tue=1, Wed=2, Thu=3, Fri=4)
        if (weeklyChartContainer.getChildCount() >= 5) {
            for (int i = 0; i < 5; i++) {
                View dayView = weeklyChartContainer.getChildAt(i);
                if (dayView instanceof LinearLayout) {
                    LinearLayout dayContainer = (LinearLayout) dayView;
                    if (dayContainer.getChildCount() >= 3) {
                        View bar = dayContainer.getChildAt(0);
                        TextView countText = (TextView) dayContainer.getChildAt(1);
                        
                        int count = dayCounts.getOrDefault(i, 0);
                        // Scale bar height (20dp to 100dp range)
                        int barHeight = maxCount > 0 ? (int) (20 + (count * 80.0 / maxCount)) : 20;
                        if (barHeight < 20) barHeight = 20;
                        if (barHeight > 100) barHeight = 100;
                        
                        // Convert dp to pixels
                        float density = getResources().getDisplayMetrics().density;
                        int heightPx = (int) (barHeight * density);
                        
                        ViewGroup.LayoutParams params = bar.getLayoutParams();
                        params.height = heightPx;
                        bar.setLayoutParams(params);
                        
                        if (countText != null) {
                            countText.setText(String.valueOf(count));
                        }
                    }
                }
            }
        }
    }

    private void updateDiseaseDistribution(List<DiseaseCount> distribution) {
        if (diseaseLegendContainer == null || distribution == null || distribution.isEmpty()) return;
        
        // Calculate total
        int total = 0;
        for (DiseaseCount dc : distribution) {
            total += dc.count;
        }
        if (total == 0) return;
        
        // Update legend text views
        // The layout has 4 legend items, update them if we have data
        if (diseaseLegendContainer.getChildCount() > 0) {
            int index = 0;
            for (DiseaseCount dc : distribution) {
                if (index >= diseaseLegendContainer.getChildCount()) break;
                
                View legendItemView = diseaseLegendContainer.getChildAt(index);
                if (legendItemView instanceof LinearLayout) {
                    LinearLayout legendItem = (LinearLayout) legendItemView;
                    if (legendItem.getChildCount() >= 2) {
                        View textView = legendItem.getChildAt(1);
                        if (textView instanceof TextView) {
                            TextView text = (TextView) textView;
                            int percentage = (int) ((dc.count * 100.0) / total);
                            String diseaseName = dc.disease_type != null ? dc.disease_type : "Unknown";
                            text.setText(String.format(Locale.getDefault(), "%s (%d%%)", diseaseName, percentage));
                        }
                    }
                }
                index++;
            }
        }
    }
}
