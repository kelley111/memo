package com.example.memo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

        int rowsAffected = db.update(DBHelper.TB3_NAME, values, "id=?", new String[]{itemId});

        if (rowsAffected > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
        }
    }
}
