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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Todolistfragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "itemclick";
    View contentView;
    DBHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<HashMap<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        contentView = inflater.inflate(R.layout.fragment_todolist, container, false);

        // 从数据库中获取待办事项
        Cursor cursor = db.query("todo_items", new String[]{"id", "task", "completion_time", "deadline"},
                null, null, null, null, null);

        // 解决第一次使用时数据为空的情况
        if (cursor.getCount() == 0) {
            TextView emptyView = contentView.findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);
            return contentView;
        } else {
            cursor.moveToLast();
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String task = cursor.getString(cursor.getColumnIndex("task"));
                @SuppressLint("Range") long completionTime = cursor.getLong(cursor.getColumnIndex("completion_time"));
                @SuppressLint("Range") long deadline = cursor.getLong(cursor.getColumnIndex("deadline"));

                String formattedCompletionTime = formatDate(completionTime);
                String formattedDeadline = formatDate(deadline);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(id));
                map.put("task", task);
                map.put("completion_time", formattedCompletionTime);
                map.put("deadline", formattedDeadline);
                listItems.add(map);

            } while (cursor.moveToPrevious());

            // 生成适配器的Item和动态数组对应的元素
            ToDoListAdapter adapter = new ToDoListAdapter(getActivity(), R.layout.todo_list_item, listItems);

            // 绑定控件
            ListView listView = contentView.findViewById(R.id.todo_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);

            // 添加新任务的按钮
            FloatingActionButton fab = contentView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewItem();
                }
            });

            return contentView;
        }
    }

    private void addNewItem() {
        // 示例：添加一个新的待办事项
        ToDoItem newItem = new ToDoItem("New Task", System.currentTimeMillis(), System.currentTimeMillis() + 86400000);
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        ToDoDao toDoDao = new ToDoDao(getActivity());
        toDoDao.insert(newItem);
        listItems.add(new HashMap<String, String>() {{
            put("task", newItem.getTask());
            put("completion_time", formatDate(newItem.getCompletionTime()));
            put("deadline", formatDate(newItem.getDeadline()));
        }});
        ((ToDoListAdapter) ((ListView) contentView.findViewById(R.id.todo_listview)).getAdapter()).notifyDataSetChanged();
    }

    // 点击listitem后展示任务详细信息
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = contentView.findViewById(R.id.todo_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);

        Log.i(TAG, "onItemClick: 打开新窗口");
        Intent itemClick = new Intent(getActivity(), ToDoDetailActivity.class);

        String task = map.get("task");
        String completionTime = map.get("completion_time");
        String deadline = map.get("deadline");

        itemClick.putExtra("task", task);
        itemClick.putExtra("completion_time", completionTime);
        itemClick.putExtra("deadline", deadline);

        startActivity(itemClick);
    }

    // 长按删除功能
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        ListView listView = getActivity().findViewById(R.id.todo_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        final int itemId = Integer.parseInt(map.get("id"));

        db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("请确认是否删除当前任务").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");

                // 删除数据库记录
                db.delete("todo_items", "id=?", new String[]{String.valueOf(itemId)});
                // 从列表中移除该项
                listItems.remove(position);
                // 更新适配器
                ((ToDoListAdapter) listView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("否", null);
        builder.create().show();

        return true; // 长按时不会产生单击
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }
}
