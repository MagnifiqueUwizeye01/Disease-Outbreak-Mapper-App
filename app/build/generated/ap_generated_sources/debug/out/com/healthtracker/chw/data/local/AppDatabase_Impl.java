package com.healthtracker.chw.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile UnsyncedReportDao _unsyncedReportDao;

  private volatile UserDao _userDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `unsynced_reports` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `patientName` TEXT, `gender` TEXT, `dateOfBirth` TEXT, `patientAge` INTEGER, `chwName` TEXT, `chwId` TEXT, `chwEmail` TEXT, `latitude` REAL, `longitude` REAL, `address` TEXT, `encounterDate` TEXT, `encounterType` TEXT, `diseaseType` TEXT, `symptomsJson` TEXT, `severity` TEXT, `observationDetails` TEXT, `notes` TEXT, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`uid` TEXT NOT NULL, `name` TEXT, `email` TEXT, `role` TEXT, `phone` TEXT, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`uid`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e17a9f6087728fd7af8a941bcb4d6b65')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `unsynced_reports`");
        db.execSQL("DROP TABLE IF EXISTS `users`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUnsyncedReports = new HashMap<String, TableInfo.Column>(19);
        _columnsUnsyncedReports.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("patientName", new TableInfo.Column("patientName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("gender", new TableInfo.Column("gender", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("dateOfBirth", new TableInfo.Column("dateOfBirth", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("patientAge", new TableInfo.Column("patientAge", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("chwName", new TableInfo.Column("chwName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("chwId", new TableInfo.Column("chwId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("chwEmail", new TableInfo.Column("chwEmail", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("address", new TableInfo.Column("address", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("encounterDate", new TableInfo.Column("encounterDate", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("encounterType", new TableInfo.Column("encounterType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("diseaseType", new TableInfo.Column("diseaseType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("symptomsJson", new TableInfo.Column("symptomsJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("severity", new TableInfo.Column("severity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("observationDetails", new TableInfo.Column("observationDetails", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUnsyncedReports.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUnsyncedReports = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUnsyncedReports = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUnsyncedReports = new TableInfo("unsynced_reports", _columnsUnsyncedReports, _foreignKeysUnsyncedReports, _indicesUnsyncedReports);
        final TableInfo _existingUnsyncedReports = TableInfo.read(db, "unsynced_reports");
        if (!_infoUnsyncedReports.equals(_existingUnsyncedReports)) {
          return new RoomOpenHelper.ValidationResult(false, "unsynced_reports(com.healthtracker.chw.data.local.UnsyncedReport).\n"
                  + " Expected:\n" + _infoUnsyncedReports + "\n"
                  + " Found:\n" + _existingUnsyncedReports);
        }
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(6);
        _columnsUsers.put("uid", new TableInfo.Column("uid", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("role", new TableInfo.Column("role", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phone", new TableInfo.Column("phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.healthtracker.chw.data.local.User).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e17a9f6087728fd7af8a941bcb4d6b65", "4617a894d981db3503b3eef9b65e1f2d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "unsynced_reports","users");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `unsynced_reports`");
      _db.execSQL("DELETE FROM `users`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UnsyncedReportDao.class, UnsyncedReportDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UnsyncedReportDao unsyncedReportDao() {
    if (_unsyncedReportDao != null) {
      return _unsyncedReportDao;
    } else {
      synchronized(this) {
        if(_unsyncedReportDao == null) {
          _unsyncedReportDao = new UnsyncedReportDao_Impl(this);
        }
        return _unsyncedReportDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }
}
