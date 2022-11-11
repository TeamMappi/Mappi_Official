package com.example.mappi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class sqlDB extends SQLiteOpenHelper {

    //Setting variables
    User users;
    private static final String DB_NAME = "routesDB";
    private static final int DB_VERSION = 1;

    //Establish a connection between DBHelp and DB
    public sqlDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // initialize variables
        String sqlRoute = "CREATE TABLE userHistory(userId text, date text, transportType text, startPoint text, destination text)";
        db.execSQL(sqlRoute);
    }

    // Insert data into the database
    public boolean insertUserHistory(String userId, String date, String transportType, LatLng startPoint, LatLng destination) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("date", date);
        contentValues.put("transportType", transportType);
        contentValues.put("start", startPoint.getLatitude());
        contentValues.put("destination", destination.getLatitude());
        contentValues.put("startPoint", startPoint.getLatitude());
        db.insert("userHistory", null, contentValues);
        db.close();
        return true;
    }


    // gets data from DB to display route History
    public Cursor getUserHistory(String userId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select userId, date, transportType, startPoint, destination from userHistory " +
                "where userId=? order by date desc", new String[]{userId});
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlRoute = "DROP TABLE IF EXISTS userHistory";
        db.execSQL(sqlRoute);
    }
}
