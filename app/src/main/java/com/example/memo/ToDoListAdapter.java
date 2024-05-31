package com.example.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ToDoListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int resource;
    private ArrayList<HashMap<String, String>> data;

    public ToDoListAdapter(Context context, int resource, ArrayList<HashMap<String, String>> data) {
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
        }

        HashMap<String, String> item = data.get(position);

        TextView taskView = convertView.findViewById(R.id.task_text_view);
        TextView completionTimeView = convertView.findViewById(R.id.completion_time_text_view);
        TextView deadlineView = convertView.findViewById(R.id.deadline_text_view);

        taskView.setText(item.get("task"));
        completionTimeView.setText(item.get("completion_time"));
        deadlineView.setText(item.get("deadline"));

        return convertView;
    }
}
