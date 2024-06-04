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
    private static final int REQUEST_CODE_EDIT_TODO = 1;
    private static final String TAG = "itemclick";
    View contentview;
    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();
    ToDoListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        contentview = inflater.inflate(R.layout.fragment_todolist, container, false);

        loadData(); // 再立即重新加载数据

        ListView listView = contentview.findViewById(R.id.todo_listview);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        return contentview;
    }

    private void loadData() {
        listItems.clear(); //清除当前数据
        //重新从数据库中获取数据
        Cursor cursor = db.query(DBHelper.TB3, new String[]{"id", "content", "ddl", "creation_date"},
                null, null, null, null, null);

        if (cursor.getCount() == 0) {
            TextView emptyView = contentview.findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);

        } else {
            cursor.moveToLast();
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

            if (adapter == null) {
                adapter = new ToDoListAdapter(getActivity(), R.layout.todo_list_item, listItems);
                ListView listView = contentview.findViewById(R.id.todo_listview);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = contentview.findViewById(R.id.todo_listview);
        HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);

        Intent itemClick = new Intent(getActivity(), Todoeditactivity.class);
        itemClick.putExtra("id", map.get("id"));
        itemClick.putExtra("content", map.get("content"));
        itemClick.putExtra("ddl", map.get("ddl"));
        itemClick.putExtra("creation_date", map.get("creation_date"));

        startActivityForResult(itemClick, REQUEST_CODE_EDIT_TODO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_TODO && resultCode == getActivity().RESULT_OK) {
            loadData(); // 刷新数据
        }
    }

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

                db.delete(DBHelper.TB3, "id=?", new String[]{itemId});
                listItems.remove(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("否", null);
        builder.create().show();

        return true;
    }
}
