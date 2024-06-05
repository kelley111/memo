package com.example.memo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class DiaryCreateActivity extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_create);

        // 实例化 DBHelper 并获取可写数据库
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    // 当用户点击保存按钮时调用的方法
    public void create(View btn){
        // 获取用户输入的日记内容
        EditText editText = findViewById(R.id.content);
        String text = editText.getText().toString();

        // 如果内容为空，则显示警告对话框，否则保存日记
        if(text.isEmpty()){
            // 显示警告对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("请输入内容后再进行保存，无法保存空内容").setNegativeButton("确定",null);
            builder.create().show();
        }
        else {
            // 获取当前时间
            Calendar cal = Calendar.getInstance();
            int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1; // 获取星期几
            String datatime = new String(); // 星期几的汉字表示
            if (weekday == 1) datatime = "一";
            else if (weekday == 2) datatime = "二";
            else if (weekday == 3) datatime = "三";
            else if (weekday == 4) datatime = "四";
            else if (weekday == 5) datatime = "五";
            else if (weekday == 6) datatime = "六";
            else if (weekday == 0) datatime = "日";
            int year = cal.get(Calendar.YEAR);// 年
            int month = cal.get(Calendar.MONTH) + 1;// 月
            int day = cal.get(Calendar.DAY_OF_MONTH);// 日
            int hour = cal.get(Calendar.HOUR_OF_DAY);// 小时
            int minute = cal.get(Calendar.MINUTE);// 分钟
            int second = cal.get(Calendar.SECOND);// 秒
            String date = year + "/" + month + "/" + day + "/" + hour + "/" + minute + "/" + second + "/" + datatime;// 组合成完整时间日期
            // 显示日期时间
            Toast.makeText(this, date, Toast.LENGTH_SHORT).show();

            // 向数据库写入数据
            ContentValues values = new ContentValues();
            values.put("diarydata", text); // 日记内容
            values.put("date", date); // 日期时间
            values.put("favorite_status","未收藏"); // 收藏状态，默认为未收藏
            db.insert("diary_data", null, values); // 插入数据

            // 返回到主界面
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 确保如果目标 Activity 已经在任务栈的顶部，那么不会创建新的实例
            startActivity(intent);
            finish(); // 关闭当前页面
        }
    }

    // 当用户点击取消按钮时调用的方法
    public void cancel(View btn){
        finish(); // 关闭当前页面
    }
}
