package com.example.memo;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.page1);
        //设置屏幕滑动问题
        applyUserSettings(this, drawerLayout, 1f);
        ViewPager2 view = findViewById(R.id.viewPage2);
        MyPageAdapter pageAdapter = new MyPageAdapter(this);
        view.setAdapter(pageAdapter);

        int[] images = new int[]{

                android.R.drawable.presence_audio_away,

                android.R.drawable.ic_menu_compass,

                android.R.drawable.ic_menu_myplaces
        };

        String[] tiles = new String[]{"日记", "箴言", "代办日程"};
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = findViewById(R.id.topline);
        new TabLayoutMediator(tabLayout, view,
                (tab,position) -> tab.setIcon(images[position]).setText(tiles[position])
        ).attach();

        //一些菜单当中的事件
        //获取保存好的字段信息

    }




    private void applyUserSettings(Activity activity,DrawerLayout drawerLayout,float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;

        try {
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);

            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = (int) (displayWidthPercentage * Resources.getSystem().getDisplayMetrics().widthPixels);
            edgeSizeField.setInt(leftDragger, edgeSize);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }




    // 该方法解决DrawerLayout只能从屏幕边缘滑动的问题,来自csdn
    private void setDrawerLeftEdgeSize (Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");//通过反射获得DrawerLayout类中mLeftDragger字段
            leftDraggerField.setAccessible(true);//私有属性要允许修改
            ViewDragHelper vdh = (ViewDragHelper) leftDraggerField.get(drawerLayout);//获取ViewDragHelper的实例, 通过ViewDragHelper实例获取mEdgeSize字段
            Field edgeSizeField = vdh.getClass().getDeclaredField("mEdgeSize");//依旧是通过反射获取ViewDragHelper类中mEdgeSize字段, 这个字段控制边缘触发范围
            edgeSizeField.setAccessible(true);//依然是私有属性要允许修改
            int edgeSize = edgeSizeField.getInt(vdh);//这⾥获得mEdgeSize真实值
            Log.d("AAA", "mEdgeSize: "+edgeSize);//这⾥可以打印⼀下看看值是多少
            //Start 获取⼿机屏幕宽度，单位px
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            //End 获取⼿机屏幕
            Log.d("AAA", "point: "+point.x);//依然可以打印⼀下看看值是多少
            edgeSizeField.setInt(vdh, 150);//这⾥设置mEdgeSize的值，Math.max函数取得是最⼤值，也可以⾃⼰指定想要触发的范围值，如: 500,注
            //写到这⾥已经实现了，但是你会发现，如果长按触发范围，侧边栏也会弹出来，⽽且速度不太同步，稳定

            //Start 解决长按会触发侧边栏
            //长按会触发侧边栏是DrawerLayout类的私有类ViewDragCallback中的mPeekRunnable实现导致，我们通过反射把它置空
            Field leftCallbackField = drawerLayout.getClass().getDeclaredField("mLeftCallback");//通过反射拿到私有类ViewDragCallback字段
            leftCallbackField.setAccessible(true);//私有字段设置允许修改
            ViewDragHelper.Callback vdhCallback = (ViewDragHelper.Callback) leftCallbackField.get(drawerLayout);//ViewDragCallback类是私有类，我们返回类型定义成他的
            Field peekRunnableField = vdhCallback.getClass().getDeclaredField("mPeekRunnable");//我们依然是通过反射拿到关键字段，mPeekRunnable
            peekRunnableField.setAccessible(true);
            //定义⼀个空的实现
            Runnable nullRunnable = new Runnable(){
                @Override
                public void run() {
                }
            };
            peekRunnableField.set(vdhCallback, nullRunnable);//给mPeekRunnable字段置空

        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    //新建日记
    public void create1(View btn){
        Intent intent = new Intent(this,DiaryCreateActivity.class);
        startActivity(intent);
    }
    public  void sentencecollection(View btn){
        Intent intent = new Intent(this,SentenceCollectionActivity.class);
        startActivity(intent);
    }
   public  void dairy_favorite_collection(View view){
        Intent intent = new Intent(this,dairy_favorite_collection.class);
        startActivity(intent);
   }
   public  void  create2(View btn){
       Intent intent = new Intent(this,TodoList.class);
       startActivity(intent);

   }

}
