package com.example.healthtracker.repository;

import android.content.Context;

import com.example.healthtracker.api.ApiClient;
import com.example.healthtracker.api.ApiService;
import com.example.healthtracker.db.AppDatabase;
import com.example.healthtracker.db.dao.ReportDao;
import com.example.healthtracker.db.entities.ReportEntity;

import java.util.List;

import retrofit2.Response;

public class ReportRepository {

    private final ReportDao dao;
    private final ApiService api;

    public ReportRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).reportDao();
        api = ApiClient.getApiService();
    }

    // Insert async
    public void insert(ReportEntity entity, InsertCallback callback) {
        new Thread(() -> {
            long id = dao.insert(entity);
            if (callback != null) callback.onInserted(id);
        }).start();
    }

    public interface InsertCallback {
        void onInserted(long id);
    }

    // Get PENDING reports
    public List<ReportEntity> getPending() {
        return dao.getPendingReports();
    }

    // MAIN SYNC METHOD (called by Worker)
    public void syncPendingReports() throws Exception {
        List<ReportEntity> pending = getPending();

        for (ReportEntity item : pending) {
            Response<Void> response =
                    api.sendObservation(item.payloadJson).execute();

            if (response.isSuccessful()) {
                dao.updateStatus(item.uuid, "SYNCED");
            } else {
                throw new Exception("Server error " + response.code());
            }
        }
    }
}
