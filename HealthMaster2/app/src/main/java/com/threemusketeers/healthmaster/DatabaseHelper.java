package com.threemusketeers.healthmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "HealthMaster.db";
    public static final String TABLE_NAME = "client_info";
    public static final String COL_EMAIL = "email";
    public static final String COL_NAME = "name";
    public static final String COL_AGE = "age";
    public static final String COL_SEX = "sex";
    public static final String COL_HEIGHT = "height";
    public static final String COL_WEIGHT = "weight";
    public static final String COL_PHOTO = "photo";
    public static final String COL_BMI = "bmi";

    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "( EMAIL TEXT PRIMARY KEY, " +
                "NAME TEXT, " +
                "AGE TEXT, " +
                "SEX TEXT, " +
                "HEIGHT TEXT, " +
                "WEIGHT TEXT, " +
                "PHOTO BLOB, " +
                "BMI TEXT " +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String email, String name, int age, String sex, float height, float weight, byte[] photo, int bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_AGE, age);
        contentValues.put(COL_SEX, sex);
        contentValues.put(COL_HEIGHT, height);
        contentValues.put(COL_WEIGHT, weight);
        contentValues.put(COL_PHOTO, photo);
        contentValues.put(COL_BMI, bmi);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from client_info", null);
        return res;
    }

    public void UpdateData(String email, String name, int age, String sex, float height, float weight, byte[] photo, int bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_AGE, age);
        contentValues.put(COL_SEX, sex);
        contentValues.put(COL_HEIGHT, height);
        contentValues.put(COL_WEIGHT, weight);
        contentValues.put(COL_PHOTO, photo);
        contentValues.put(COL_BMI, bmi);
        db.update(TABLE_NAME, contentValues, "EMAIL = ?", new String[]{email});
    }
}
