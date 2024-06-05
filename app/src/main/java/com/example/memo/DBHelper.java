package com.example.memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION =6 ;
    private static final String DB_NAME = "my.db";
    public static final String TB1 = "diary_data";
    public static final String TB2 = "sentence_data";
    public static final String TB3 = "todo_list";
    public static final String TB4 = "diary_favorite_data";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "onCreate: Creating SQL");

        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL("CREATE TABLE " + TB1 + " ("
                + "diarydata TEXT, "
                + "date DATE, "
                + "favorite_status TEXT, "
                + "favorite_time TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + TB2 + " ("
                + "sentence TEXT, "
                + "source TEXT, "
                + "review_text TEXT, "
                + "date DATE, "
                + "favorite_status TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + TB3 + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "content TEXT, "
                + "ddl TEXT, "
                + "creation_date TEXT"
                + ")");

        db.execSQL("CREATE TABLE " + TB4 + " ("
                + "diarydata TEXT, "
                + "date DATE, "
                + "favorite_status TEXT, "
                + "favorite_time TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            Log.i("DBHelper", "onUpgrade: Adding columns to diary_data and sentence_data tables");
            db.execSQL("ALTER TABLE " + TB1 + " ADD COLUMN favorite_status TEXT");
            db.execSQL("ALTER TABLE " + TB1 + " ADD COLUMN favorite_time TEXT");
            db.execSQL("ALTER TABLE " + TB2 + " ADD COLUMN favorite_status TEXT");
        }
        if (oldVersion < 6) {
            Log.i("DBHelper", "onUpgrade: Creating todo_list table");
            db.execSQL("CREATE TABLE " + TB3 + " ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "content TEXT, "
                    + "ddl TEXT, "
                    + "creation_date TEXT"
                    + ")");
        }
        if (oldVersion < 6) {
            Log.i("DBHelper", "onUpgrade: Creating diary_favorite_data table");
            db.execSQL("CREATE TABLE " + TB4 + " ("
                    + "diarydata TEXT, "
                    + "date DATE, "
                    + "favorite_status TEXT, "
                    + "favorite_time TEXT"
                    + ")");
        }
    }
}
