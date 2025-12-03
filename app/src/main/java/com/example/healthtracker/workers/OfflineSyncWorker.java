package com.example.healthtracker.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.healthtracker.repository.ReportRepository;

public class OfflineSyncWorker extends Worker {

    private final ReportRepository repo;

    public OfflineSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        repo = new ReportRepository(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            repo.syncPendingReports();
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
