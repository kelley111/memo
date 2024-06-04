package com.example.memo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class TodoList extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todolist);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    public void create(View view) {
        EditText contentEditText = findViewById(R.id.content);
        EditText ddlEditText = findViewById(R.id.ddl);
        String content = contentEditText.getText().toString();
        String ddl = ddlEditText.getText().toString();

        if (content.isEmpty() || ddl.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示")
                    .setMessage("请输入内容和截止日期后再进行保存，无法保存空内容")
                    .setNegativeButton("确定", null);
            builder.create().show();
        } else {
            Calendar calendar = Calendar.getInstance();
            String creationDate = calendar.get(Calendar.YEAR) + "/" +
                    (calendar.get(Calendar.MONTH) + 1) + "/" +
                    calendar.get(Calendar.DAY_OF_MONTH) + " " +
                    calendar.get(Calendar.HOUR_OF_DAY) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" +
                    calendar.get(Calendar.SECOND);

            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("ddl", ddl);
            values.put("creation_date", creationDate);
            db.insert(DBHelper.TB3, null, values);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void cancel(View view) {
        finish();
    }
}
