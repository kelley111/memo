package com.example.memo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class dairy_favorite_collection extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "itemclick";
    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dairy_favorite_collection); // 确保布局文件正确

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        loadDataFromDatabase();

        ListView listView = findViewById(R.id.mylistview);
        DiaryListviewAdapter adapter = new DiaryListviewAdapter(this, R.layout.list_item1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    private void loadDataFromDatabase() {
        Cursor cursor = db.query("diary_data", new String[]{"diarydata", "date", "favorite_status"}, null, null, null, null, null);

        if (cursor.getCount() == 0) {
            TextView suiji = findViewById(R.id.suiji);
            suiji.setVisibility(View.VISIBLE);
        } else {
            cursor.moveToLast();
            do {
                @SuppressLint("Range") String diarydata = cursor.getString(cursor.getColumnIndex("diarydata"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("favorite_status"));

                String[] d = date.split("/");

                for (int i = 0; i < d.length - 1; i++) {
                    if (d[i].length() == 1) {
                        d[i] = "0" + d[i];
                    }
                }

                String time = d[3] + ":" + d[4];
                String date3 = d[0] + "-" + d[1] + "-" + d[2];
                String weekday = "星期" + d[6];

                HashMap<String, String> map = new HashMap<>();
                map.put("daynum", d[2]);
                map.put("weekday", weekday);
                map.put("time", time);
                map.put("date", date3);
                map.put("text", diarydata);
                map.put("status", status);
                map.put("date1", date);
                listItems.add(map);

            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);

        Log.i(TAG, "onItemClick: 打开新窗口");
        Intent Itemclick = new Intent(dairy_favorite_collection.this, DiaryActivity.class);

        String date = map.get("date1");
        String diarydata = map.get("text");

        Itemclick.putExtra("diarydata", diarydata);
        Itemclick.putExtra("date", date);

        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("date", date);
        editor.apply();
        startActivity(Itemclick);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
        final String date = map.get("date1");

        db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");

                db.delete("diary_data", "date=?", new String[]{date});
                listItems.remove(position);
                ((DiaryListviewAdapter) ((ListView) findViewById(R.id.mylistview)).getAdapter()).notifyDataSetChanged();

                Toast.makeText(dairy_favorite_collection.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("否", null);
        builder.create().show();

        return true;
    }
}
