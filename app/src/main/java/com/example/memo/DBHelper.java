package com.example.memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 2;  // Increment the version number
    private static final String DB_NAME = "my.db";
    public static final String TB1_NAME = "diary_data";
    public static final String TB2_NAME = "sentence_data";
    public static final String TB3_NAME = "favorite_status";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("TAG", "onCreate: ");

        db.execSQL("PRAGMA foreign_keys=ON;");

        db.execSQL("CREATE TABLE " + TB1_NAME + " ("
                + "diarydata TEXT, "
                + "date DATE, "
                + "favorite_status TEXT, " // 收藏状态
                + "favorite_time TEXT" // 收藏时间
                + ")");

        db.execSQL("CREATE TABLE " + TB2_NAME + " ("
                + "sentence TEXT, "
                + "source TEXT, "
                + "review_text TEXT, "
                + "date DATE, "
                + "favorite_status TEXT" // 收藏状态
                + ")");

        db.execSQL("CREATE TABLE " + TB3_NAME + " ("
                + "treehole_diarydata TEXT, "
                + "treehole_date DATE, "
                + "store_date DATE, "
                + "author_id TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TAG", "onUpgrade: from " + oldVersion + " to " + newVersion);
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TB1_NAME + " ADD COLUMN favorite_status TEXT");
            db.execSQL("ALTER TABLE " + TB1_NAME + " ADD COLUMN favorite_time TEXT");
            db.execSQL("ALTER TABLE " + TB2_NAME + " ADD COLUMN favorite_status TEXT");
        }
    }
}
