package com.example.memo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SentenceCollectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    DBHelper dbHelper;
    SQLiteDatabase db;
    String sentence, review_text, source, date;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_collection);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("sentence_data",
                new String[]{"sentence", "source", "review_text", "date"},
                null,
                null,
                null,
                null,
                null);

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            do {
                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence"));
                review_text = cursor.getString(cursor.getColumnIndexOrThrow("review_text"));
                source = cursor.getString(cursor.getColumnIndexOrThrow("source"));
                date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                HashMap<String, String> map = new HashMap<>();
                map.put("sentence", sentence);
                map.put("review_text", review_text);
                map.put("source", source);
                map.put("date", date);
                listItems.add(map);
            } while (cursor.moveToPrevious());

            SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.sentence_celection_read_list,
                    new String[]{"sentence", "review_text", "source", "date"},
                    new int[]{R.id.sentence, R.id.mywords, R.id.source, R.id.sentence_time});
            ListView listView = findViewById(R.id.sentence_collection_read_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        } else {
            TextView sentence_tip = findViewById(R.id.sentence_tip);
            sentence_tip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = findViewById(R.id.sentence_collection_read_listview);
        HashMap<String, String> item = (HashMap<String, String>) listView.getItemAtPosition(position);
        sentence = item.get("sentence");
        review_text = item.get("review_text");
        source = item.get("source");
        date = item.get("date");

        // 绑定弹窗中的文本框和删除按钮
        PopupWindow popupWindow;
        View popupView = getLayoutInflater().inflate(R.layout.layout_popoutwindow, null);
        TextView sentenceTextView = popupView.findViewById(R.id.sentence_read);
        TextView sourceTextView = popupView.findViewById(R.id.source_read);
        TextView reviewTextView = popupView.findViewById(R.id.review_read);
        ImageButton deleteButton = popupView.findViewById(R.id.sentence_delete);

        sentenceTextView.setText(sentence);
        sourceTextView.setText(source);
        reviewTextView.setText(review_text);

        // 展示弹窗
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null)); // 点击空白区域关闭弹窗

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // 弹窗中删除按钮点击事件处理
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SentenceCollectionActivity.this);
                builder.setTitle("确认删除").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        // 移除列表中对应项
                        listItems.remove(position);
                        SimpleAdapter adapter = new SimpleAdapter(SentenceCollectionActivity.this, listItems, R.layout.sentence_celection_read_list,
                                new String[]{"sentence", "review_text", "source", "date"},
                                new int[]{R.id.sentence, R.id.mywords, R.id.source, R.id.sentence_time});
                        listView.setAdapter(adapter);
                        popupWindow.dismiss(); // 关闭弹窗
                    }
                }).setNegativeButton("否", null);
                builder.create().show();
            }
        });
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = findViewById(R.id.sentence_collection_read_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        final String date = map.get("date");

        db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(SentenceCollectionActivity.this);
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.delete("sentence_data","date=?",new String[]{date});
                listItems.remove(position);
                SimpleAdapter adapter = new SimpleAdapter(SentenceCollectionActivity.this, listItems, R.layout.sentence_celection_read_list,
                        new String[]{"sentence", "review_text", "source", "date"},
                        new int[]{R.id.sentence, R.id.mywords, R.id.source, R.id.sentence_time});
                listView.setAdapter(adapter);
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("否", null).show();

        return true; // 长按时不会产生单击
    }
}
