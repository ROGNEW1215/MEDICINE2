package com.example.medicine.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.medicine.model.HealthRecord;
import com.example.medicine.model.Medication;
import com.example.medicine.model.MedicationHistory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "medicine.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_MEDICATION = "medication_plan";
    public static final String COL_ID = "_id";
    public static final String COL_MED_NAME = "med_name";
    public static final String COL_DOSAGE = "dosage";
    public static final String COL_REMIND_TIME = "remind_time";
    public static final String COL_ICON_TYPE = "icon_type";
    public static final String COL_ALARM_ID = "alarm_id";
    public static final String COL_IS_TAKEN = "is_taken";

    public static final String TABLE_HEALTH = "health_record";
    public static final String COL_SYSTOLIC = "systolic";
    public static final String COL_DIASTOLIC = "diastolic";
    public static final String COL_RECORD_TIME = "record_time";

    public static final String TABLE_HISTORY = "medication_history";
    public static final String COL_MED_ID = "med_id";
    public static final String COL_TAKEN_TIME = "taken_time";

    private static final String CREATE_MEDICATION_TABLE =
            "CREATE TABLE " + TABLE_MEDICATION + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MED_NAME + " TEXT NOT NULL, "
                    + COL_DOSAGE + " TEXT NOT NULL, "
                    + COL_REMIND_TIME + " TEXT NOT NULL, "
                    + COL_ICON_TYPE + " INTEGER DEFAULT 0, "
                    + COL_ALARM_ID + " INTEGER NOT NULL, "
                    + COL_IS_TAKEN + " INTEGER DEFAULT 0"
                    + ")";

    private static final String CREATE_HEALTH_TABLE =
            "CREATE TABLE " + TABLE_HEALTH + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_SYSTOLIC + " INTEGER NOT NULL, "
                    + COL_DIASTOLIC + " INTEGER NOT NULL, "
                    + COL_RECORD_TIME + " TEXT NOT NULL"
                    + ")";

    private static final String CREATE_HISTORY_TABLE =
            "CREATE TABLE " + TABLE_HISTORY + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_MED_ID + " INTEGER NOT NULL, "
                    + COL_MED_NAME + " TEXT NOT NULL, "
                    + COL_TAKEN_TIME + " TEXT NOT NULL"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEDICATION_TABLE);
        db.execSQL(CREATE_HEALTH_TABLE);
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_MEDICATION
                    + " ADD COLUMN " + COL_IS_TAKEN + " INTEGER DEFAULT 0");
            db.execSQL(CREATE_HEALTH_TABLE);
            db.execSQL(CREATE_HISTORY_TABLE);
        }
    }

    public long insertPlan(Medication medication) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MED_NAME, medication.getMedName());
        values.put(COL_DOSAGE, medication.getDosage());
        values.put(COL_REMIND_TIME, medication.getRemindTime());
        values.put(COL_ICON_TYPE, medication.getIconType());
        values.put(COL_ALARM_ID, medication.getAlarmId());
        values.put(COL_IS_TAKEN, medication.getIsTaken());
        long id = db.insert(TABLE_MEDICATION, null, values);
        medication.setId(id);
        return id;
    }

    public int updatePlan(Medication medication) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MED_NAME, medication.getMedName());
        values.put(COL_DOSAGE, medication.getDosage());
        values.put(COL_REMIND_TIME, medication.getRemindTime());
        values.put(COL_ICON_TYPE, medication.getIconType());
        values.put(COL_ALARM_ID, medication.getAlarmId());
        return db.update(TABLE_MEDICATION, values,
                COL_ID + " = ?", new String[]{String.valueOf(medication.getId())});
    }

    public int updateTakenStatus(long id, int isTaken) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_TAKEN, isTaken);
        return db.update(TABLE_MEDICATION, values,
                COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void resetDailyTakenStatus() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_IS_TAKEN, 0);
        db.update(TABLE_MEDICATION, values, null, null);
    }

    public List<Medication> getAllPlans() {
        List<Medication> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICATION, null, null, null, null, null,
                COL_REMIND_TIME + " ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToMedication(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public Medication getById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICATION, null,
                COL_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        Medication medication = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                medication = cursorToMedication(cursor);
            }
            cursor.close();
        }
        return medication;
    }

    public int deletePlan(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_MEDICATION, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public long insertHealthRecord(HealthRecord record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SYSTOLIC, record.getSystolic());
        values.put(COL_DIASTOLIC, record.getDiastolic());
        values.put(COL_RECORD_TIME, record.getRecordTime());
        long id = db.insert(TABLE_HEALTH, null, values);
        record.setId(id);
        return id;
    }

    public long insertMedicationHistory(MedicationHistory history) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MED_ID, history.getMedId());
        values.put(COL_MED_NAME, history.getMedName());
        values.put(COL_TAKEN_TIME, history.getTakenTime());
        long id = db.insert(TABLE_HISTORY, null, values);
        history.setId(id);
        return id;
    }

    public List<MedicationHistory> getAllMedicationHistory() {
        List<MedicationHistory> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null,
                COL_TAKEN_TIME + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToHistory(cursor));
            }
            cursor.close();
        }
        return list;
    }

    public int getNextAlarmId() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_ALARM_ID + ") FROM " + TABLE_MEDICATION, null);
        int nextId = 1;
        if (cursor != null) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                nextId = cursor.getInt(0) + 1;
            }
            cursor.close();
        }
        return nextId;
    }

    private Medication cursorToMedication(Cursor cursor) {
        Medication medication = new Medication();
        medication.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        medication.setMedName(cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_NAME)));
        medication.setDosage(cursor.getString(cursor.getColumnIndexOrThrow(COL_DOSAGE)));
        medication.setRemindTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_REMIND_TIME)));
        medication.setIconType(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ICON_TYPE)));
        medication.setAlarmId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ALARM_ID)));
        medication.setIsTaken(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_TAKEN)));
        return medication;
    }

    private MedicationHistory cursorToHistory(Cursor cursor) {
        MedicationHistory history = new MedicationHistory();
        history.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        history.setMedId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_MED_ID)));
        history.setMedName(cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_NAME)));
        history.setTakenTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_TAKEN_TIME)));
        return history;
    }
}
