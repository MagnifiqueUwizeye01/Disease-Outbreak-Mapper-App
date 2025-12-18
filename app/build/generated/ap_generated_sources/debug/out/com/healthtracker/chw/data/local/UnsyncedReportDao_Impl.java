package com.healthtracker.chw.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UnsyncedReportDao_Impl implements UnsyncedReportDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UnsyncedReport> __insertionAdapterOfUnsyncedReport;

  private final EntityDeletionOrUpdateAdapter<UnsyncedReport> __deletionAdapterOfUnsyncedReport;

  public UnsyncedReportDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUnsyncedReport = new EntityInsertionAdapter<UnsyncedReport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `unsynced_reports` (`id`,`patientName`,`gender`,`dateOfBirth`,`patientAge`,`chwName`,`chwId`,`latitude`,`longitude`,`address`,`encounterDate`,`encounterType`,`diseaseType`,`symptomsJson`,`severity`,`observationDetails`,`notes`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UnsyncedReport entity) {
        statement.bindLong(1, entity.id);
        if (entity.patientName == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.patientName);
        }
        if (entity.gender == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.gender);
        }
        if (entity.dateOfBirth == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.dateOfBirth);
        }
        if (entity.patientAge == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.patientAge);
        }
        if (entity.chwName == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.chwName);
        }
        if (entity.chwId == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.chwId);
        }
        if (entity.latitude == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.latitude);
        }
        if (entity.longitude == null) {
          statement.bindNull(9);
        } else {
          statement.bindDouble(9, entity.longitude);
        }
        if (entity.address == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.address);
        }
        if (entity.encounterDate == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.encounterDate);
        }
        if (entity.encounterType == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.encounterType);
        }
        if (entity.diseaseType == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.diseaseType);
        }
        if (entity.symptomsJson == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.symptomsJson);
        }
        if (entity.severity == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.severity);
        }
        if (entity.observationDetails == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.observationDetails);
        }
        if (entity.notes == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.notes);
        }
        statement.bindLong(18, entity.timestamp);
      }
    };
    this.__deletionAdapterOfUnsyncedReport = new EntityDeletionOrUpdateAdapter<UnsyncedReport>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `unsynced_reports` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final UnsyncedReport entity) {
        statement.bindLong(1, entity.id);
      }
    };
  }

  @Override
  public void insert(final UnsyncedReport report) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfUnsyncedReport.insert(report);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final UnsyncedReport report) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfUnsyncedReport.handle(report);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<UnsyncedReport> getAllReports() {
    final String _sql = "SELECT * FROM unsynced_reports ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
      final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
      final int _cursorIndexOfDateOfBirth = CursorUtil.getColumnIndexOrThrow(_cursor, "dateOfBirth");
      final int _cursorIndexOfPatientAge = CursorUtil.getColumnIndexOrThrow(_cursor, "patientAge");
      final int _cursorIndexOfChwName = CursorUtil.getColumnIndexOrThrow(_cursor, "chwName");
      final int _cursorIndexOfChwId = CursorUtil.getColumnIndexOrThrow(_cursor, "chwId");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
      final int _cursorIndexOfEncounterDate = CursorUtil.getColumnIndexOrThrow(_cursor, "encounterDate");
      final int _cursorIndexOfEncounterType = CursorUtil.getColumnIndexOrThrow(_cursor, "encounterType");
      final int _cursorIndexOfDiseaseType = CursorUtil.getColumnIndexOrThrow(_cursor, "diseaseType");
      final int _cursorIndexOfSymptomsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "symptomsJson");
      final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
      final int _cursorIndexOfObservationDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "observationDetails");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final List<UnsyncedReport> _result = new ArrayList<UnsyncedReport>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final UnsyncedReport _item;
        _item = new UnsyncedReport();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfPatientName)) {
          _item.patientName = null;
        } else {
          _item.patientName = _cursor.getString(_cursorIndexOfPatientName);
        }
        if (_cursor.isNull(_cursorIndexOfGender)) {
          _item.gender = null;
        } else {
          _item.gender = _cursor.getString(_cursorIndexOfGender);
        }
        if (_cursor.isNull(_cursorIndexOfDateOfBirth)) {
          _item.dateOfBirth = null;
        } else {
          _item.dateOfBirth = _cursor.getString(_cursorIndexOfDateOfBirth);
        }
        if (_cursor.isNull(_cursorIndexOfPatientAge)) {
          _item.patientAge = null;
        } else {
          _item.patientAge = _cursor.getInt(_cursorIndexOfPatientAge);
        }
        if (_cursor.isNull(_cursorIndexOfChwName)) {
          _item.chwName = null;
        } else {
          _item.chwName = _cursor.getString(_cursorIndexOfChwName);
        }
        if (_cursor.isNull(_cursorIndexOfChwId)) {
          _item.chwId = null;
        } else {
          _item.chwId = _cursor.getString(_cursorIndexOfChwId);
        }
        if (_cursor.isNull(_cursorIndexOfLatitude)) {
          _item.latitude = null;
        } else {
          _item.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        }
        if (_cursor.isNull(_cursorIndexOfLongitude)) {
          _item.longitude = null;
        } else {
          _item.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        }
        if (_cursor.isNull(_cursorIndexOfAddress)) {
          _item.address = null;
        } else {
          _item.address = _cursor.getString(_cursorIndexOfAddress);
        }
        if (_cursor.isNull(_cursorIndexOfEncounterDate)) {
          _item.encounterDate = null;
        } else {
          _item.encounterDate = _cursor.getString(_cursorIndexOfEncounterDate);
        }
        if (_cursor.isNull(_cursorIndexOfEncounterType)) {
          _item.encounterType = null;
        } else {
          _item.encounterType = _cursor.getString(_cursorIndexOfEncounterType);
        }
        if (_cursor.isNull(_cursorIndexOfDiseaseType)) {
          _item.diseaseType = null;
        } else {
          _item.diseaseType = _cursor.getString(_cursorIndexOfDiseaseType);
        }
        if (_cursor.isNull(_cursorIndexOfSymptomsJson)) {
          _item.symptomsJson = null;
        } else {
          _item.symptomsJson = _cursor.getString(_cursorIndexOfSymptomsJson);
        }
        if (_cursor.isNull(_cursorIndexOfSeverity)) {
          _item.severity = null;
        } else {
          _item.severity = _cursor.getString(_cursorIndexOfSeverity);
        }
        if (_cursor.isNull(_cursorIndexOfObservationDetails)) {
          _item.observationDetails = null;
        } else {
          _item.observationDetails = _cursor.getString(_cursorIndexOfObservationDetails);
        }
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _item.notes = null;
        } else {
          _item.notes = _cursor.getString(_cursorIndexOfNotes);
        }
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getRecordCount() {
    final String _sql = "SELECT COUNT(*) FROM unsynced_reports";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public UnsyncedReport getLatestReport() {
    final String _sql = "SELECT * FROM unsynced_reports ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
      final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
      final int _cursorIndexOfDateOfBirth = CursorUtil.getColumnIndexOrThrow(_cursor, "dateOfBirth");
      final int _cursorIndexOfPatientAge = CursorUtil.getColumnIndexOrThrow(_cursor, "patientAge");
      final int _cursorIndexOfChwName = CursorUtil.getColumnIndexOrThrow(_cursor, "chwName");
      final int _cursorIndexOfChwId = CursorUtil.getColumnIndexOrThrow(_cursor, "chwId");
      final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
      final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
      final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
      final int _cursorIndexOfEncounterDate = CursorUtil.getColumnIndexOrThrow(_cursor, "encounterDate");
      final int _cursorIndexOfEncounterType = CursorUtil.getColumnIndexOrThrow(_cursor, "encounterType");
      final int _cursorIndexOfDiseaseType = CursorUtil.getColumnIndexOrThrow(_cursor, "diseaseType");
      final int _cursorIndexOfSymptomsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "symptomsJson");
      final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
      final int _cursorIndexOfObservationDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "observationDetails");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final UnsyncedReport _result;
      if (_cursor.moveToFirst()) {
        _result = new UnsyncedReport();
        _result.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfPatientName)) {
          _result.patientName = null;
        } else {
          _result.patientName = _cursor.getString(_cursorIndexOfPatientName);
        }
        if (_cursor.isNull(_cursorIndexOfGender)) {
          _result.gender = null;
        } else {
          _result.gender = _cursor.getString(_cursorIndexOfGender);
        }
        if (_cursor.isNull(_cursorIndexOfDateOfBirth)) {
          _result.dateOfBirth = null;
        } else {
          _result.dateOfBirth = _cursor.getString(_cursorIndexOfDateOfBirth);
        }
        if (_cursor.isNull(_cursorIndexOfPatientAge)) {
          _result.patientAge = null;
        } else {
          _result.patientAge = _cursor.getInt(_cursorIndexOfPatientAge);
        }
        if (_cursor.isNull(_cursorIndexOfChwName)) {
          _result.chwName = null;
        } else {
          _result.chwName = _cursor.getString(_cursorIndexOfChwName);
        }
        if (_cursor.isNull(_cursorIndexOfChwId)) {
          _result.chwId = null;
        } else {
          _result.chwId = _cursor.getString(_cursorIndexOfChwId);
        }
        if (_cursor.isNull(_cursorIndexOfLatitude)) {
          _result.latitude = null;
        } else {
          _result.latitude = _cursor.getDouble(_cursorIndexOfLatitude);
        }
        if (_cursor.isNull(_cursorIndexOfLongitude)) {
          _result.longitude = null;
        } else {
          _result.longitude = _cursor.getDouble(_cursorIndexOfLongitude);
        }
        if (_cursor.isNull(_cursorIndexOfAddress)) {
          _result.address = null;
        } else {
          _result.address = _cursor.getString(_cursorIndexOfAddress);
        }
        if (_cursor.isNull(_cursorIndexOfEncounterDate)) {
          _result.encounterDate = null;
        } else {
          _result.encounterDate = _cursor.getString(_cursorIndexOfEncounterDate);
        }
        if (_cursor.isNull(_cursorIndexOfEncounterType)) {
          _result.encounterType = null;
        } else {
          _result.encounterType = _cursor.getString(_cursorIndexOfEncounterType);
        }
        if (_cursor.isNull(_cursorIndexOfDiseaseType)) {
          _result.diseaseType = null;
        } else {
          _result.diseaseType = _cursor.getString(_cursorIndexOfDiseaseType);
        }
        if (_cursor.isNull(_cursorIndexOfSymptomsJson)) {
          _result.symptomsJson = null;
        } else {
          _result.symptomsJson = _cursor.getString(_cursorIndexOfSymptomsJson);
        }
        if (_cursor.isNull(_cursorIndexOfSeverity)) {
          _result.severity = null;
        } else {
          _result.severity = _cursor.getString(_cursorIndexOfSeverity);
        }
        if (_cursor.isNull(_cursorIndexOfObservationDetails)) {
          _result.observationDetails = null;
        } else {
          _result.observationDetails = _cursor.getString(_cursorIndexOfObservationDetails);
        }
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _result.notes = null;
        } else {
          _result.notes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
