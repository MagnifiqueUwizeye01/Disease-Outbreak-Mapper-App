package com.healthtracker.chw.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UnsyncedReportDao {
    @Insert
    void insert(UnsyncedReport report);

    @Query("SELECT * FROM unsynced_reports ORDER BY timestamp ASC")
    List<UnsyncedReport> getAllReports();

    @Query("SELECT COUNT(*) FROM unsynced_reports")
    int getRecordCount();

    @Query("SELECT * FROM unsynced_reports ORDER BY timestamp DESC LIMIT 1")
    UnsyncedReport getLatestReport();

    @Delete
    void delete(UnsyncedReport report);
}
