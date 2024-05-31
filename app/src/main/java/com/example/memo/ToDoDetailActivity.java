package com.example.memo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ToDoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        // 获取传递的数据
        String task = getIntent().getStringExtra("task");
        String completionTime = getIntent().getStringExtra("completion_time");
        String deadline = getIntent().getStringExtra("deadline");

        // 绑定视图
        TextView taskTextView = findViewById(R.id.detail_task_text_view);
        TextView completionTimeTextView = findViewById(R.id.detail_completion_time_text_view);
        TextView deadlineTextView = findViewById(R.id.detail_deadline_text_view);

        // 设置数据
        taskTextView.setText(task);
        completionTimeTextView.setText("Completion Time: " + completionTime);
        deadlineTextView.setText("Deadline: " + deadline);
    }
}
