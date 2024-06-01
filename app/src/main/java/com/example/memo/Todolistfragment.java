package com.example.memo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class Todolistfragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "itemclick";
    View contentview;
    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        contentview = inflater.inflate(R.layout.fragment_todolist, container, false);

        // 从数据库中获取待办事项数据
        Cursor cursor = db.query(DBHelper.TB3_NAME, new String[]{"id", "content", "ddl", "creation_date"},
                null, null, null, null, null);

        // 解决第一次使用时数据为空的情况
        if (cursor.getCount() == 0) {
            TextView emptyView = contentview.findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);
            return contentview;
        } else {
            cursor.moveToLast(); // 实现逆序输出，将后写入的待办事项先展示出来
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String ddl = cursor.getString(cursor.getColumnIndex("ddl"));
                @SuppressLint("Range") String creationDate = cursor.getString(cursor.getColumnIndex("creation_date"));

                HashMap<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(id));
                map.put("content", content);
                map.put("ddl", ddl);
                map.put("creation_date", creationDate);
                listItems.add(map);

            } while (cursor.moveToPrevious());

            // 生成适配器的Item和动态数组对应的元素
            ToDoListAdapter adapter = new ToDoListAdapter(getActivity(), R.layout.todo_list_item, listItems); // 自定义adapter添加值

            // 绑定控件
            ListView listView = contentview.findViewById(R.id.todo_listview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this); // 为listView添加点击响应
            listView.setOnItemLongClickListener(this); // 为listview长按添加响应

            return contentview; // 将实现的页面返回
        }
    }

    // 点击listitem后跳转到待办事项编辑页面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = contentview.findViewById(R.id.todo_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position); // 获得当前list展示的数据，储存在map中，通过key取值

        Log.i(TAG, "onItemClick: 打开新窗口");
        Intent itemClick = new Intent(getActivity(), Todoeditactivity.class);

        String itemId = map.get("id");
        String content = map.get("content");
        String ddl = map.get("ddl");
        String creationDate = map.get("creation_date");

        itemClick.putExtra("id", itemId);
        itemClick.putExtra("content", content);
        itemClick.putExtra("ddl", ddl);
        itemClick.putExtra("creation_date", creationDate);

        startActivity(itemClick);
    }

    // 长按删除功能
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        ListView listView = getActivity().findViewById(R.id.todo_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        final String itemId = map.get("id");

        db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");

                // 删除数据库记录
                db.delete(DBHelper.TB3_NAME, "id=?", new String[]{itemId});
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
}
