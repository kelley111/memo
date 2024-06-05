package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DiaryWriteActivity extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;
    EditText editText;
    String diarydata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        editText = findViewById(R.id.content2);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        diarydata = intent.getStringExtra("diarydata");
        editText.setText(diarydata);
    }

}
