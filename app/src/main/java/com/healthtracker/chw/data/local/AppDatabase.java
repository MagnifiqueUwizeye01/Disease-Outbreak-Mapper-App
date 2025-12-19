package com.healthtracker.chw.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { UnsyncedReport.class, User.class }, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UnsyncedReportDao unsyncedReportDao();

    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "health_track_db")
                            .fallbackToDestructiveMigration() // Dev mode: wipe data on schema change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
