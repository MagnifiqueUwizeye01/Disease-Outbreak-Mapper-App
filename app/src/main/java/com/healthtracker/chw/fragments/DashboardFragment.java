package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.healthtracker.chw.R;

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NavController navController = Navigation.findNavController(view);

        // Quick action chips
        View chipHighRisk = view.findViewById(R.id.chip_high_risk);
        View chipSyncPending = view.findViewById(R.id.chip_sync_pending);

        if (chipHighRisk != null) {
            chipHighRisk.setOnClickListener(v ->
                    navController.navigate(R.id.mapFragment));
        }

        if (chipSyncPending != null) {
            chipSyncPending.setOnClickListener(v ->
                    navController.navigate(R.id.offlinePendingCasesFragment));
        }

        // Main cards
        View cardReportCase = view.findViewById(R.id.card_report_case);
        View cardOutbreakMap = view.findViewById(R.id.card_outbreak_map);
        View cardTrends = view.findViewById(R.id.card_trends);
        View cardHistory = view.findViewById(R.id.card_history);

        if (cardReportCase != null) {
            cardReportCase.setOnClickListener(v ->
                    navController.navigate(R.id.reportCaseFragment));
        }

        if (cardOutbreakMap != null) {
            cardOutbreakMap.setOnClickListener(v ->
                    navController.navigate(R.id.mapFragment));
        }

        if (cardTrends != null) {
            cardTrends.setOnClickListener(v ->
                    navController.navigate(R.id.analyticsFragment));
        }

        if (cardHistory != null) {
            cardHistory.setOnClickListener(v ->
                    navController.navigate(R.id.caseHistoryFragment));
        }

        // Stats cards (if they need click listeners)
        View cardPendingReports = view.findViewById(R.id.card_pending_reports);
        View cardHighRisk = view.findViewById(R.id.card_high_risk);

        if (cardPendingReports != null) {
            cardPendingReports.setOnClickListener(v ->
                    navController.navigate(R.id.offlinePendingCasesFragment));
        }

        if (cardHighRisk != null) {
            cardHighRisk.setOnClickListener(v ->
                    navController.navigate(R.id.mapFragment));
        }
    }
}