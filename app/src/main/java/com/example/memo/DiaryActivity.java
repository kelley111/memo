package com.example.memo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    String diarydata;
    String date, status;
    TextView read, year, day, time;
    DBHelper dbHelper;
    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        Intent intent = getIntent();
        diarydata = intent.getStringExtra("diarydata");
        date = intent.getStringExtra("date");

        // 日期字符串示例: "2024/05/28/星期二/12:34"
        String[] detailtime = date.split("/");
        String yearStr = detailtime[0] + "年";
        String dayStr = detailtime[1] + "月" + detailtime[2] + "日";
        String timeStr = detailtime[3] + " " + detailtime[4];

        read = findViewById(R.id.read);
        year = findViewById(R.id.year);
        day = findViewById(R.id.day);
        time = findViewById(R.id.time);

        read.setText(diarydata);

        year.setText(yearStr);
        day.setText(dayStr);
        time.setText(timeStr);

        read.setMovementMethod(ScrollingMovementMethod.getInstance()); // 使得文本框中文字过多时可滑动

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("diary_data", new String[]{"favorite_status"}, "date=?", new String[]{date},
                null, null, null);
        while (c.moveToNext()) {
            status = c.getString(c.getColumnIndexOrThrow("favorite_status")); // 收藏状态
        }
    }
    // 打开日记修改页面并传值
    public void change(View btn) {
        Intent intent = new Intent(this, DiaryWriteActivity.class);
        intent.putExtra("diarydata", diarydata);
        startActivity(intent);
    }

    // 删除当前日记
    public void delete(View btn) {
        Intent intent = new Intent(this, MainActivity.class);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.delete("diary_data", "date=?", new String[]{date}); // 数据库记录删除
                    Toast.makeText(getApplication(), "已成功删除", Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(DiaryActivity.this, "已成功删除", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("不！！手滑手滑~", null);
            builder.create().show();
        }
  //将日记进行收藏
    public void share(View btn) {
        if (status.equals("已收藏")) {
            Toast.makeText(this, "已在收藏夹里啦!有空可以去回味！！", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("要将这条随记收藏吗").setPositiveButton("是", new DialogInterface.OnClickListener() {
                       //收藏的具体操作
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHelper = new DBHelper(getApplicationContext());
                    db = dbHelper.getWritableDatabase();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String datetime = sdf.format(new Date()); // 获取当前日期和时间
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("favorite_status", "已收藏");
                    contentValues.put("favorite_time", datetime);
                    db.update("diary_data", contentValues, "date=?", new String[]{date}); // 数据库记录更新
                    Toast.makeText(getApplication(), "收藏成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DiaryActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }).setNegativeButton("否", null);
            builder.create().show();
        }
    }
}
