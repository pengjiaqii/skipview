package com.jade.testdemo.util;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


public class AddAppListDB extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "add_app_list_activity_data";
    public static final String COLUMN_COMPONENT = "componentName";
    public static final String COLUMN_USER = "profileId";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_PACKAGE = "packageName";
    public static final String COLUMN_APP_NAME = "appName";
    public static final String COLUMN_CLASS_NAME = "className";
    public static final String COLUMN_IS_CHECKED = "isChecked";
    public static final String COLUMN_LAST_UPDATED = "lastUpdated";
    public static final String COLUMN_VERSION = "version";

    public AddAppListDB(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_PACKAGE + " TEXT NOT NULL, " +
                COLUMN_APP_NAME + " TEXT NOT NULL, " +
                COLUMN_CLASS_NAME + " TEXT, " +
                COLUMN_IS_CHECKED + " INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY (" + COLUMN_PACKAGE + ", " + COLUMN_CLASS_NAME + ") " +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}