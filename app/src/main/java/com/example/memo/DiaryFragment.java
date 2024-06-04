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

import java.util.ArrayList;
import java.util.HashMap;

public class DiaryFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "itemclick";
    View contentview;
    DBHelper dbHelper;
    SQLiteDatabase db;

    ArrayList<HashMap<String,String>> listItems = new ArrayList<HashMap<String,String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getReadableDatabase();
        contentview = inflater.inflate(R.layout.diaryfragment, container, false);

        //从数据库中获取日记数据和日记记录的时间和日期
        Cursor c = db.query("diary_data", new String[]{"diarydata", "date", "favorite_status"},
                null, null, null, null, null); //查询data表中当前用户的日记和日期

        //解决第一次使用时数据为空的情况
        if(c.getCount() == 0){
            TextView dairy = contentview.findViewById(R.id.dairy);
            dairy.setVisibility(View.VISIBLE);
            return contentview;
        } else {
            c.moveToLast();//实现逆序输出，将后写入的日记先展示出来
            do {
                @SuppressLint("Range") String diarydata = c.getString(c.getColumnIndex("diarydata"));
                @SuppressLint("Range") String date = c.getString(c.getColumnIndex("date"));
                @SuppressLint("Range") String status = c.getString(c.getColumnIndex("favorite_status"));

                String detailtime[] = date.split("/");//分片获取日期中的各个值,以获取数据为年/月/日/时/分/秒/星期

                //更清晰的展示数据,使展示的数据效果更好
                for(int i = 0; i < detailtime.length - 1; i++){
                    if(detailtime[i].length() == 1){
                        detailtime[i] = "0" + detailtime[i];
                    }
                }

                String time = detailtime[3] + ":" + detailtime[4];
                String date3 = detailtime[0] + "-" + detailtime[1] + "-" + detailtime[2]; //日期
                String weekday = "星期" + detailtime[6];

                HashMap<String,String> map = new HashMap<String,String>();
                map.put("daynum", detailtime[2]);
                map.put("weekday", weekday);
                map.put("time", time);
                map.put("date", date3);
                map.put("text", diarydata);
                map.put("status", status);
                map.put("date1", date);//将未调整的完整日期同时存储下来，便于为以后页面查询提供日期参考
                listItems.add(map);

            } while(c.moveToPrevious());

            //生成适配器的Item和动态数组对应的元素
            DiaryListviewAdapter adapter = new DiaryListviewAdapter(getActivity(), R.layout.list_item1, listItems);

            //绑定控件
            ListView listView = contentview.findViewById(R.id.mylistview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);//为listView添加点击响应
            listView.setOnItemLongClickListener(this);//为listview长按添加响应

            return contentview;//将实现的页面返回
        }
    }

    //点击listitem后跳转到日记阅读页面
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = contentview.findViewById(R.id.mylistview);
        HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(position);//获得当前list展示的数据，储存在map中，通过key取值

        Log.i(TAG, "onItemClick: 打开新窗口");
        Intent Itemclick = new Intent(getActivity(), DiaryActivity.class);

        String date = map.get("date1");//获得完整未调整日期
        String diarydata = map.get("text");//获取日记数据准备传递到下一个页面

        Itemclick.putExtra("diarydata", diarydata);//将日记数据传至下一个页面
        Itemclick.putExtra("date", date);//将日期传至下一个页面

        //将完整未修改日期存储到sharedpreference中,便于后面页面查询值直接调用
        SharedPreferences sp = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("date", date);
        editor.apply();
        startActivity(Itemclick);
    }

    //长按删除功能
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        ListView listView = getActivity().findViewById(R.id.mylistview);
        HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        final String date = map.get("date1");

        db = dbHelper.getWritableDatabase();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "onClick: 对话框事件处理");

                // 删除数据库记录
                db.delete("diary_data", "date=?", new String[]{date});
                // 从列表中移除该项
                listItems.remove(position);
                // 更新适配器
                ((DiaryListviewAdapter) listView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("否", null);
        builder.create().show();
        return true; // 长按时不会产生单击
    }
}
