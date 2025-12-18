package com.example.healthtracker.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.healthtracker.db.entities.ReportEntity;

import java.util.List;

@Dao
public interface ReportDao {

    @Insert
    long insert(ReportEntity report);

    @Update
    int update(ReportEntity report);

    // ---------- Required Methods ----------
    @Query("SELECT * FROM reports WHERE status = 'PENDING'")
    List<ReportEntity> getPendingReports();

    @Query("UPDATE reports SET status = :newStatus WHERE uuid = :uuid")
    void updateStatus(String uuid, String newStatus);

    @Query("DELETE FROM reports WHERE localId = :id")
    void deleteById(long id);
}
