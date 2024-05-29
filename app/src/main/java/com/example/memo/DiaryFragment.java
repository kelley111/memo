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
        Cursor cursor = db.query("diary_data", new String[]{"diarydata", "date", "favorite_status"},
                null, null, null, null, null); // 删除了 user_id 条件//查询data表中当前用户的日记和日期

        //解决第一次使用时数据为空的情况
        if(cursor.getCount() == 0){
            TextView suiji = contentview.findViewById(R.id.suiji);
            suiji.setVisibility(View.VISIBLE);
            return contentview;
        }
        else {
            cursor.moveToLast();//实现逆序输出，将后写入的日记先展示出来
            do {

                @SuppressLint("Range") String diarydata = cursor.getString(cursor.getColumnIndex("diarydata"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("favorite_status"));

                String d[] = date.split("/");//分片获取日期中的各个值,数组中依次为年/月/日/时/分/秒/星期

//更清晰的展示数据,使展示的数据效果更好
                for(int i = 0;i<d.length-1;i++){

                    if(d[i].length()==1){

                        d[i]="0"+d[i];

                    }
                }

                String time = d[3]+":"+d[4];
                String riqi = d[0]+"-"+d[1]+"-"+d[2];
                String weekday = "星期"+d[6];

                HashMap<String,String> map = new HashMap<String,String>();
                map.put("daynum",d[2]);
                map.put("weekday",weekday);
                map.put("time",time);
                map.put("date",riqi);
                map.put("text",diarydata);
                map.put("status",status);
                map.put("date1",date);//将未调整的完整日期同时存储下来，便于为以后页面查询提供日期参考
                listItems.add(map);

            }
            while(cursor.moveToPrevious());

            //生成适配器的Item和动态数组对应的元素
//        SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),listItems,R.layout.list_item1,new String[]{"daynum","weekday","time","date","text"},new int[]{R.id.daynum,
//                R.id.weekday,R.id.time,R.id.date,R.id.textd});//普通添加值的方式

            DiaryListviewAdapter adapter = new DiaryListviewAdapter(getActivity(),R.layout.list_item1,listItems);//自定义adapter添加值

            //绑定控件
            ListView listView = contentview.findViewById(R.id.mylistview);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);//为listView添加点击响应
            listView.setOnItemLongClickListener(this);//为listview长按添加响应

            return  contentview;//将实现的页面返回
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
        Itemclick.putExtra("date",date);//将日期传至下一个页面


        //将完整未修改日期存储到sharedpreference中,便于后面页面查询值直接调用
        SharedPreferences sp = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("date",date);
        editor.apply();//必须有这句，否则不执行
        startActivity(Itemclick);
    }

    //长按删除功能
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
        ListView listView = getActivity().findViewById(R.id.mylistview);
        HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
        String date = map.get("date1");//获得map中的日期数据，以便删除时将数据库记录一并删除

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("diary_data",new String[]{"treehole_status"},"user_id=? and date=?",new String[]{date},
                null,null,null);
        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndexOrThrow("treehole_status")).equals("已投递")){

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示").setMessage("该随记已投递到树洞，请到我的投递中撤销").setPositiveButton("确定",null);
                builder.create().show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i(TAG, "onClick: 对话框事件处理");

                        db.delete("diary_data","user_id=? and date=?",new String[]{date});//数据库记录删除
                        listItems.remove(position);
                        DiaryListviewAdapter adapter = new DiaryListviewAdapter(getActivity(),R.layout.list_item1,listItems);//删除后修改list值
                        listView.setAdapter(adapter);

                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("否",null);
                builder.create().show();
                Log.i(TAG, "onItemLongClick: size=" + listItems.size());
            }
        }

        return true;//长按时不会产生单击
    }

}

