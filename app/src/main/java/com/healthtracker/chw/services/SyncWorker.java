package com.healthtracker.chw.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.healthtracker.chw.data.local.AppDatabase;
import com.healthtracker.chw.data.local.UnsyncedReport;
import com.healthtracker.chw.data.local.UnsyncedReportDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker";
    private final UnsyncedReportDao dao;
    private final FHIRService fhirService;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        dao = AppDatabase.getDatabase(context).unsyncedReportDao();
        fhirService = new FHIRService(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting sync work...");
        List<UnsyncedReport> reports = dao.getAllReports();

        if (reports.isEmpty()) {
            return Result.success();
        }

        Log.d(TAG, "Found " + reports.size() + " reports to sync");

        int failures = 0;

        for (UnsyncedReport report : reports) {
            boolean success = syncReport(report);
            if (!success) {
                failures++;
            }
        }

        if (failures > 0) {
            Log.w(TAG, failures + " reports failed to sync. Retrying later.");
            return Result.retry();
        }

        Log.d(TAG, "All reports synced successfully");
        return Result.success();
    }

    private boolean syncReport(UnsyncedReport report) {
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = { false };

        try {
            // Deserialize symptoms
            List<String> symptoms = new Gson().fromJson(report.symptomsJson, new TypeToken<List<String>>() {
            }.getType());

            fhirService.submitReportForSync(
                    report.patientName, report.gender, report.dateOfBirth, report.patientAge,
                    report.chwName, report.chwId,
                    report.latitude, report.longitude, report.address,
                    report.encounterDate, report.encounterType,
                    report.diseaseType, symptoms, report.severity,
                    report.observationDetails, report.notes,
                    new FHIRService.SaveCallback() {
                        @Override
                        public void onSuccess(String reportId, String locationId) {
                            Log.d(TAG, "Synced report: " + report.id);
                            // Delete from DB on success
                            dao.delete(report);
                            result[0] = true;
                            latch.countDown();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "Failed to sync report " + report.id + ": " + error);
                            result[0] = false;
                            latch.countDown();
                        }
                    });

            // Wait for callback
            latch.await(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e(TAG, "Exception syncing report " + report.id, e);
            return false;
        }

        return result[0];
    }
}
