package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SecondFragment extends Fragment implements Runnable {

    private static final String TAG = "fragment2";
    View contentview;
    TextView textView1, textView2;
    EditText Review_text;
    ImageButton review_button, list, next;
    RadioGroup radioGroup;
    String wz = "https://v1.hitokoto.cn?c=i"; // 初始网址
    Handler handler;
    String[] v; // 用来保存从接口上获得的内容，以便回传主线程

    DBHelper dbHelper;
    SQLiteDatabase db;

    // 用以处理数据流
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentview = getLayoutInflater().inflate(R.layout.fragment_second, null);
        radioGroup = contentview.findViewById(R.id.rdg);
        textView1 = contentview.findViewById(R.id.jd);
        textView2 = contentview.findViewById(R.id.ly);
        Review_text = contentview.findViewById(R.id.review_text);
        review_button = contentview.findViewById(R.id.review_button);
        next = contentview.findViewById(R.id.next);
        list = contentview.findViewById(R.id.list);

        // 添加一个监听
        Review_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本内容发送改变之前使用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 发生时
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 发生后

                // TODO Auto-generated method stub
                int lines = Review_text.getLineCount();
                // 限制最大输入行数
                if (lines > 2) {
                    String str = s.toString();
                    int cursorStart = Review_text.getSelectionStart();
                    int cursorEnd = Review_text.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1) {
                        str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, s.length() - 1);
                    }
                    // setText会触发afterTextChanged的递归
                    Review_text.setText(str);
                    // setSelection用的索引不能使用str.length()否则会越界
                    Review_text.setSelection(Review_text.getText().length());
                    Toast.makeText(getActivity(), "输入内容请不要超过两行，我装不下呜呜呜呜", Toast.LENGTH_SHORT).show();
                }
            }
        }); // 设置页面2文本框的最大输入行数


        review_button = contentview.findViewById(R.id.review_button);
        next = contentview.findViewById(R.id.next); // 绑定按钮上一条
        list = contentview.findViewById(R.id.list); // 绑定按钮list

        // 刚打开界面时也要展示一条句子
        Thread t1 = new Thread(this); // 同一个Thread不能重复调用start
        t1.start();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dm) {

                    wz = "https://v1.hitokoto.cn?c=a&c=b";
                    Thread t1 = new Thread(SecondFragment.this::run);
                    t1.start();

                }
                else if(checkedId == R.id.game) {

                    wz = "https://v1.hitokoto.cn?c=c";
                    Thread t2 = new Thread(SecondFragment.this::run);
                    t2.start();

                }
                else if(checkedId == R.id.wx) {

                    wz = "https://v1.hitokoto.cn?c=d";
                    Thread t3 = new Thread(SecondFragment.this::run);
                    t3.start();
                }
                else if(checkedId == R.id.pm) {

                    wz = "https://v1.hitokoto.cn?c=i";
                    Thread t4 = new Thread(SecondFragment.this::run);
                    t4.start();

                }
                else if(checkedId == R.id.zx) {

                    wz = "https://v1.hitokoto.cn?c=k";
                    Thread t5 = new Thread(SecondFragment.this::run);
                    t5.start();

                }
                else if(checkedId == R.id.ys) {

                    wz = "https://v1.hitokoto.cn?c=h";
                    Thread t6 = new Thread(SecondFragment.this::run);
                    t6.start();

                }
                else if(checkedId == R.id.wyy) {

                    wz = "https://v1.hitokoto.cn?c=j";
                    Thread t7 = new Thread(SecondFragment.this::run);
                    t7.start();

                }

                else if(checkedId == R.id.hh) {

                    wz = "https://v1.hitokoto.cn?c=a&c=b&c=c&c=d&c=e&c=f&c=g&c=h&c=i&c=j&c=k&c=l";
                    Thread t8 = new Thread(SecondFragment.this::run);
                    t8.start();
                }

                Thread t = new Thread(SecondFragment.this::run);
                t.start();}
        });


        // 三个按钮的事件处理
        review_button.setOnClickListener(v -> {
            String review_text = Review_text.getText().toString();
            // 编辑框为空则提示
            if (review_text.isEmpty()) {
                Toast.makeText(getActivity(), "写点什么再保存吧!o(*￣▽￣*)ブ", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示").setMessage("一字千金,要留下它们吗").setPositiveButton("是", (dialog, which) -> {
                    // 保存编辑框中的内容并存入数据库
                    dbHelper = new DBHelper(getActivity());
                    db = dbHelper.getWritableDatabase();

                    String content = textView1.getText().toString(); // 获得句子
                    String source = textView2.getText().toString(); // 获得出处

                    // 获取当前时间并格式化
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String datetime = sdf.format(calendar.getTime());

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("sentence", content);
                    contentValues.put("source", source);
                    contentValues.put("date", datetime);
                    contentValues.put("review_text", review_text);

                    db.insert("sentence_data", null, contentValues);
                    // 已经收藏起来了,有时间就去回味一下吧,收藏成功的提示
                    Toast.makeText(getActivity(), "已经收藏起来了,有时间就去回味一下吧", Toast.LENGTH_SHORT).show();
                    Review_text.setText(null);
                }).setNegativeButton("否", null);
                builder.create().show();
            }
        });

        // 打开句子收藏展示页面
        list.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SentenceCollectionActivity.class);
            startActivity(intent);
        });

        // 用handler处理回传的值并显示
        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    String[] str = (String[]) msg.obj;
                    // Toast.makeText(getActivity(), str[1], Toast.LENGTH_SHORT).show();
                    String jz = str[0]; // 取出句子
                    String ly = str[1]; // 取出出处
                    Log.i(TAG, "handleMessage: getMessage msg = " + str);
                    textView1.setText(jz);
                    textView2.setText(ly);
                }
                super.handleMessage(msg);
            }
        };



    }

    // 子线程获取接口数据
    @Override
    public void run() {
        Log.i("run", "run: run()......");

        String value1 = ""; // 用于存储句子内容字段
        String value2 = ""; // 用于存储句子的来源字段

        try {
            URL url = new URL(wz);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responsecode = connection.getResponseCode();
            Log.i("response", "response: " + responsecode);

            if (responsecode == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                String content = convertInputStreamToString(in);
                Log.i(TAG, "run: content: " + content);
                JSONObject jsonObject = new JSONObject(content);
                value1 = jsonObject.optString("content"); // 取出正文即句子
                value2 = jsonObject.optString("author"); // 取出出处
                Log.i("value", "value: " + value1 + " - " + value2);
            }
            v = new String[]{value1, value2}; // 将正文与出处存入数组

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        // 获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(7, v);
        handler.sendMessage(msg);
        Log.i(TAG, "run: sendMessage ok");
    }
}



