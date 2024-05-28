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
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        editText = findViewById(R.id.content2);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
//        Toast.makeText(this, "创建数据库", Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        diarydata = intent.getStringExtra("diarydata");
        editText.setText(diarydata);
    }

    //将修改后的数据放入数据库
    public void confirm(View btn){

        text = editText.getText().toString();//获得当前编辑框中数据

        SharedPreferences sp = getSharedPreferences("userinformation",MODE_PRIVATE);
        SharedPreferences sp1 = getSharedPreferences("data",MODE_PRIVATE);
        String user_id = sp.getString("user_id",null);//获得sharedpreferences中保存的用户账号作为查询依据
        String date = sp1.getString("date",null);//获得之前保存的日期数据以便准确修改日记数据

        //向数据库更新日记数据，不对日期做更改
        ContentValues values = new ContentValues();
        values.put("diarydata",text);
        db.update("diary_data",values,"user_id=? and date=?",new String[]{user_id,date});
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//返回主界面时，清除途经页面
        startActivity(intent);
        finish();

    }
    //结束当前页面，返回上一个页面
    public void cancel2(View btn){

        finish();
    }
}
