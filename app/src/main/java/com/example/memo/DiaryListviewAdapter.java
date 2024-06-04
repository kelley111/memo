package com.example.memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiaryListviewAdapter extends ArrayAdapter {

    public DiaryListviewAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String,String>> list) {

        super(context, resource, list);

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item1,parent,false);
        }

        Map<String,String> map = (Map<String, String>) getItem(position);
        TextView date = itemView.findViewById(R.id.date);
        TextView time = itemView.findViewById(R.id.time);
        TextView text = itemView.findViewById(R.id.text);
        date.setText(map.get("date"));
        time.setText(map.get("time"));
        text.setText(map.get("text"));

        return itemView;
    }


}

