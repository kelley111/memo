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

public class SettingListViewAdapter extends ArrayAdapter {

    public SettingListViewAdapter(@NonNull Context context, int resource, ArrayList<HashMap<String,String>> list) {

        super(context, resource, list);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item2,parent,false);
        }

        Map<String,String> map = (Map<String, String>) getItem(position);
        TextView information = itemView.findViewById(R.id.information);
        TextView message = itemView.findViewById(R.id.message);

        information.setText(map.get("information"));
        message.setText(map.get("message"));
        return itemView;

    }


}

