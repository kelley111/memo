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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.page1);
        //设置屏幕滑动问题
        adjustDrawerEdgeSize(this, drawerLayout, 1f);
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
    private void adjustDrawerEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null)
            return;
        try {
            // 获取DrawerLayout的左边缘拖动处理器
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            Object leftDraggerObj = leftDraggerField.get(drawerLayout);

            // 获取左边缘拖动处理器的类对象
            Class<?> leftDraggerClass = leftDraggerObj.getClass();

            // 获取边缘大小字段，并设置其访问权限为可访问
            Field edgeSizeField = leftDraggerClass.getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);

            // 计算边缘大小
            int edgeSize = (int) (displayWidthPercentage * Resources.getSystem().getDisplayMetrics().widthPixels);

            // 设置边缘大小
            edgeSizeField.setInt(leftDraggerObj, edgeSize);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
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
