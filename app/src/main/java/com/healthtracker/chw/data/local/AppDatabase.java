package com.healthtracker.chw.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { UnsyncedReport.class }, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UnsyncedReportDao unsyncedReportDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "health_track_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
