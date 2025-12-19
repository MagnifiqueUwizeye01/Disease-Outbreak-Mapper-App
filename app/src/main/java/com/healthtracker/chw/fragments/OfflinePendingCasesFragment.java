package com.healthtracker.chw.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.NetworkType;
import androidx.work.Constraints;

import com.google.android.material.button.MaterialButton;
import com.healthtracker.chw.R;
import com.healthtracker.chw.adapters.OfflineCaseAdapter;
import com.healthtracker.chw.data.local.AppDatabase;
import com.healthtracker.chw.data.local.UnsyncedReport;
import com.healthtracker.chw.data.local.UnsyncedReportDao;
import com.healthtracker.chw.services.SyncWorker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OfflinePendingCasesFragment extends Fragment {

    private RecyclerView recyclerView;
    private OfflineCaseAdapter adapter;
    private MaterialButton btnSyncNow;
    private UnsyncedReportDao dao;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline_pending_cases, container, false);

        // Initialize dependencies
        dao = AppDatabase.getDatabase(requireContext()).unsyncedReportDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Setup UI
        recyclerView = view.findViewById(R.id.recycler_offline_cases);
        btnSyncNow = view.findViewById(R.id.btn_sync_now);

        setupRecyclerView();
        setupSyncButton();

        // Enable options menu for back button handling
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadReports();

        // Force back arrow
        if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
            androidx.appcompat.app.ActionBar actionBar = ((androidx.appcompat.app.AppCompatActivity) getActivity())
                    .getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new OfflineCaseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSyncButton() {
        btnSyncNow.setOnClickListener(v -> {
            // Update UI to loading state
            btnSyncNow.setEnabled(false);
            btnSyncNow.setText("Syncing...");
            btnSyncNow.setIconResource(R.drawable.ic_sync);

            // Perform manual sync directly
            performManualSync();
        });
    }

    private void performManualSync() {
        executorService.execute(() -> {
            List<UnsyncedReport> reports = dao.getAllReports();
            if (reports.isEmpty()) {
                mainHandler.post(() -> {
                    Toast.makeText(getContext(), "No pending reports to sync.", Toast.LENGTH_SHORT).show();
                    resetSyncButton("Try to Sync Now");
                });
                return;
            }

            // Reference FHIR Service
            com.healthtracker.chw.services.FHIRService fhirService = new com.healthtracker.chw.services.FHIRService(
                    requireContext());
            java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
            java.util.concurrent.atomic.AtomicInteger failCount = new java.util.concurrent.atomic.AtomicInteger(0);
            // Use a specific error container (AtomicReference) to pass string to UI thread
            java.util.concurrent.atomic.AtomicReference<String> lastError = new java.util.concurrent.atomic.AtomicReference<>(
                    "");

            final int total = reports.size();

            // Use Latch to synchronize the serial processing
            for (int i = 0; i < total; i++) {
                UnsyncedReport report = reports.get(i);
                final int currentProgress = i + 1;

                // Update UI Progress
                mainHandler.post(() -> {
                    if (btnSyncNow != null) {
                        btnSyncNow.setText("Syncing " + currentProgress + "/" + total + "...");
                    }
                });

                java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

                try {
                    List<String> symptoms = new com.google.gson.Gson().fromJson(report.symptomsJson,
                            new com.google.gson.reflect.TypeToken<List<String>>() {
                            }.getType());

                    fhirService.submitReportForSync(
                            report.patientName, report.gender, report.dateOfBirth, report.patientAge,
                            report.chwName, report.chwId, report.chwEmail,
                            report.latitude, report.longitude, report.address,
                            report.encounterDate, report.encounterType,
                            report.diseaseType, symptoms, report.severity,
                            report.observationDetails, report.notes,
                            new com.healthtracker.chw.services.FHIRService.SaveCallback() {
                                @Override
                                public void onSuccess(String reportId, String locationId) {
                                    dao.delete(report); // Delete from DB
                                    successCount.incrementAndGet();
                                    latch.countDown();
                                }

                                @Override
                                public void onError(String error) {
                                    failCount.incrementAndGet();
                                    lastError.set(error); // Capture error
                                    latch.countDown();
                                }
                            });

                    // Wait up to 10s per report
                    boolean completed = latch.await(10, java.util.concurrent.TimeUnit.SECONDS);
                    if (!completed) {
                        failCount.incrementAndGet();
                        lastError.set("Timeout (10s)");
                    }

                } catch (Exception e) {
                    failCount.incrementAndGet();
                    lastError.set(e.getMessage());
                }
            }

            // Update UI with results
            mainHandler.post(() -> {
                if (successCount.get() > 0) {
                    Toast.makeText(getContext(), "✅ " + successCount.get() + "/" + total + " Synced Successfully!",
                            Toast.LENGTH_LONG).show();
                    loadReports(); // Refresh list
                }

                if (failCount.get() > 0) {
                    String errorMsg = lastError.get();
                    if (errorMsg == null || errorMsg.isEmpty())
                        errorMsg = "Check connection";
                    // Show actual error
                    Toast.makeText(getContext(), "❌ Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                }

                resetSyncButton("Try to Sync Now");
            });
        });
    }

    private void resetSyncButton(String text) {
        if (btnSyncNow != null && isAdded()) {
            btnSyncNow.setEnabled(true);
            btnSyncNow.setText(text);
            btnSyncNow.setIconResource(R.drawable.ic_refresh);
        }
    }

    private void loadReports() {
        executorService.execute(() -> {
            List<UnsyncedReport> reports = dao.getAllReports();
            mainHandler.post(() -> {
                if (isAdded()) {
                    adapter.setReports(reports);

                    // Update header text or empty state if needed
                    if (reports.isEmpty()) {
                        // showEmptyState(); // Optional: Implement empty state View
                    }
                }
            });
        });
    }
}
