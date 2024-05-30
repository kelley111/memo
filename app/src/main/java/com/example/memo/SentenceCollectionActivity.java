package com.example.memo;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SentenceCollectionActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    DBHelper dbHelper;
    SQLiteDatabase db;

    String sentence,review_text,source,date;
    TextView Sentence_read,Source_read,Review_read;
    ArrayList<HashMap<String,String>> listItems = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_collection);

        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("sentence_data",
                new String[]{"sentence", "source", "review_text", "date"},
                null, // 查询所有数据，条件参数设置为null
                null,
                null,
                null,
                null);

        if(cursor.getCount()>0){

            cursor.moveToLast();
            do{

                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence"));
                review_text = cursor.getString(cursor.getColumnIndexOrThrow("review_text"));
                source = cursor.getString(cursor.getColumnIndexOrThrow("source"));
                date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("sentence",sentence);
                map.put("review_text",review_text);
                map.put("source",source);
                map.put("date",date);
                listItems.add(map);

            }
            while (cursor.moveToPrevious());

            SimpleAdapter adapter = new SimpleAdapter(this,listItems,R.layout.sentence_celection_read_list,new String[]{"sentence","review_text",
                    "source","date"},new int[]{R.id.sentence,R.id.mywords,R.id.source,R.id.sentence_time});
            ListView listView = findViewById(R.id.sentence_collection_read_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
        }
        else {
            TextView sentence_tip = findViewById(R.id.sentence_tip);
            sentence_tip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ListView listView = findViewById(R.id.sentence_collection_read_listview);
        HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(position);//获得当前list展示的数据，储存在map中，通过key取值
        sentence = map.get("sentence");
        review_text = map.get("review_text");
        source = map.get("source");
        date = map.get("date");

        //绑定popupwindow中各文本框
        PopupWindow popupWindow;
        View popupView = getLayoutInflater().inflate(R.layout.layout_popoutwindow, null);
        Sentence_read = popupView.findViewById(R.id.sentence_read);
        Source_read = popupView.findViewById(R.id.source_read);
        Review_read = popupView.findViewById(R.id.review_read);
        ImageButton Sentence_delete = popupView.findViewById(R.id.sentence_delete);

        Sentence_read.setText(sentence);
        Source_read.setText(source);
        Review_read.setText(review_text);

        //展示弹窗，参考了csdn
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),(Bitmap) null));//点击空白pop消失

        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(popupView,1,2,3);

        //弹窗点击
        Sentence_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SentenceCollectionActivity.this);
                builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplication(), "删除成功", Toast.LENGTH_SHORT).show();
                        //将界面中的对应list移除
                        listItems.remove(position);
                        SimpleAdapter adapter = new SimpleAdapter(SentenceCollectionActivity.this,listItems,R.layout.sentence_celection_read_list,new String[]{"sentence","review_text",
                                "source","date"},new int[]{R.id.sentence,R.id.mywords,R.id.source,R.id.sentence_time});
                        listView.setAdapter(adapter);
                        popupWindow.dismiss();//关闭弹窗
                    }
                }).setNegativeButton("否",null);
                builder.create().show();
            }
        });
    }
}
