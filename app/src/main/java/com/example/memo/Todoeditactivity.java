package com.example.memo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Todoeditactivity extends AppCompatActivity {

    private EditText contentEditText;
    private EditText ddlEditText;
    private EditText creationDateEditText;
    private Button saveButton;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todoeditactivity);

        contentEditText = findViewById(R.id.edit_content);
        ddlEditText = findViewById(R.id.edit_ddl);
        creationDateEditText = findViewById(R.id.edit_creation_date);
        saveButton = findViewById(R.id.button_save);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        itemId = intent.getStringExtra("id");
        String content = intent.getStringExtra("content");
        String ddl = intent.getStringExtra("ddl");
        String creationDate = intent.getStringExtra("creation_date");

        contentEditText.setText(content);
        ddlEditText.setText(ddl);
        creationDateEditText.setText(creationDate);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        String newContent = contentEditText.getText().toString();
        String newDdl = ddlEditText.getText().toString();
        String newCreationDate = creationDateEditText.getText().toString();

        ContentValues values = new ContentValues();
        values.put("content", newContent);
        values.put("ddl", newDdl);
        values.put("creation_date", newCreationDate);

        Log.d("Todoeditactivity", "Updating item with ID: " + itemId);

        // 读取记录，检查其是否存在
        Cursor cursor = db.query(DBHelper.TB3, null, "id=?", new String[]{itemId}, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String existingContent = cursor.getString(cursor.getColumnIndex("content"));
            @SuppressLint("Range") String existingDdl = cursor.getString(cursor.getColumnIndex("ddl"));
            @SuppressLint("Range") String existingCreationDate = cursor.getString(cursor.getColumnIndex("creation_date"));
            Log.d("Todoeditactivity", "Existing record - Content: " + existingContent + ", DDL: " + existingDdl + ", Creation Date: " + existingCreationDate);
        } else {
            Log.d("Todoeditactivity", "No record found with ID: " + itemId);
            Toast.makeText(this, "记录不存在，无法更新", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        int rowsAffected = db.update(DBHelper.TB3, values, "id=?", new String[]{itemId});
        if (rowsAffected > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            Log.d("Todoeditactivity", "Update successful: " + rowsAffected + " rows affected");
            setResult(RESULT_OK); // 设置结果为 RESULT_OK，以解决刷新数据库
            finish();
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            Log.d("Todoeditactivity", "Update failed");
        }
    }
}
