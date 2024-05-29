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
        dbHelper = new DBHelper(this);
        db=dbHelper.getWritableDatabase();

    }

    public void create(View btn){

        //若内容为空则提示，否则保存
        EditText editText = findViewById(R.id.content);
        String text = editText.getText().toString();
        if(text.isEmpty()){
            //用弹窗表示当前内容为空不能保存
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("tips").setMessage("请输入内容后再进行保存，无法保存空内容").setNegativeButton("ok",null);
            builder.create().show();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            //获取系统的日期
            int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            String weekdayC = new String();//将获取的数字星期数改为汉字
            //Toast.makeText(this, weekday, Toast.LENGTH_SHORT).show();
            if (weekday == 1) weekdayC = "一";
            else if (weekday == 2) weekdayC = "二";
            else if (weekday == 3) weekdayC = "三";
            else if (weekday == 4) weekdayC = "四";
            else if (weekday == 5) weekdayC = "五";
            else if (weekday == 6) weekdayC = "六";
            else if (weekday == 0) weekdayC = "日";
            int year = calendar.get(Calendar.YEAR);//年
            int month = calendar.get(Calendar.MONTH) + 1;//月
            int day = calendar.get(Calendar.DAY_OF_MONTH);//日
            int hour = calendar.get(Calendar.HOUR_OF_DAY);//小时
            int minute = calendar.get(Calendar.MINUTE);//分钟
            int second = calendar.get(Calendar.SECOND);//秒
            String date = year + "/" + month + "/" + day + "/" + hour + "/" + minute + "/" + second + "/" + weekdayC;//组合成完整时间日期
             Toast.makeText(this, date, Toast.LENGTH_SHORT).show();

            //向数据库写入数据
            ContentValues values = new ContentValues();
            values.put("diarydata", text);
            values.put("date", date);
            values.put("favorite_status","未收藏");
            db.insert("diary_data", null, values);

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
    //关闭页面
    public void cancel(View btn){
        finish();
    }
}
