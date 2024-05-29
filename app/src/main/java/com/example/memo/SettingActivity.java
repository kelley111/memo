package com.example.memo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

class SettingsActivity extends AppCompatActivity {

    private RadioGroup backgroundGroup;
    private RadioGroup styleGroup;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backgroundGroup = findViewById(R.id.backgroundGroup);
        styleGroup = findViewById(R.id.styleGroup);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedBackgroundId = backgroundGroup.getCheckedRadioButtonId();
                int selectedStyleId = styleGroup.getCheckedRadioButtonId();

                RadioButton selectedBackground = findViewById(selectedBackgroundId);
                RadioButton selectedStyle = findViewById(selectedStyleId);

                SharedPreferences sp = getSharedPreferences("user_settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("background", selectedBackground.getText().toString());
                editor.putString("style", selectedStyle.getText().toString());
                editor.apply();

                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
