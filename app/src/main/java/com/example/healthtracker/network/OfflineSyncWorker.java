package com.example.healthtracker.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.healthtracker.db.entities.ReportEntity;
import com.example.healthtracker.network.ApiService;
import com.example.healthtracker.network.RestClient;
import com.example.healthtracker.repository.ReportRepository;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OfflineSyncWorker extends Worker {

    public OfflineSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        ReportRepository repo = new ReportRepository(getApplicationContext());
        // synchronous wait pattern: use callback style and block until completion (simple approach)
        final Object lock = new Object();
        final Result[] finalResult = {Result.success()};

        repo.getPending(list -> {
            if (list == null || list.isEmpty()) {
                synchronized (lock) { lock.notify(); }
                return;
            }
            // build retrofit client (baseUrl should be configured; use placeholder)
            String baseUrl = getApplicationContext().getSharedPreferences("cfg", Context.MODE_PRIVATE)
                    .getString("base_url", "https://example.com/"); // replace in settings
            ApiService api = RestClient.getClient(baseUrl).create(ApiService.class);

            for (ReportEntity e : list) {
                try {
                    RequestBody rb = RequestBody.create(MediaType.parse("application/fhir+json"), e.payloadJson);
                    Call<ResponseBody> call = api.postObservation(rb);
                    Response<ResponseBody> resp = call.execute();
                    if (resp.isSuccessful()) {
                        // delete
                        repo.deleteById(e.localId, unused -> {});
                    } else {
                        // set to failed or leave pending
                    }
                } catch (Exception ex) {
                    // network error, schedule retry
                    finalResult[0] = Result.retry();
                }
            }
            synchronized (lock) { lock.notify(); }
        });

        // wait for repo.getPending to finish (max a few seconds)
        try {
            synchronized (lock) { lock.wait(8000); }
        } catch (InterruptedException ignored) {}

        return finalResult[0];
    }
}
