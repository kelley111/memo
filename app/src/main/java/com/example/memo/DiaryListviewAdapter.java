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
      //  TextView day = itemView.findViewById(R.id.day);
        TextView time = itemView.findViewById(R.id.time);
        //TextView ddate = itemView.findViewById(R.id.ddate);
        TextView text = itemView.findViewById(R.id.text);
      //  TextView status = itemView.findViewById(R.id.status);

        date.setText(map.get("date"));
     //   day.setText(map.get("day"));
        time.setText(map.get("time"));
       // ddate.setText(map.get("ddate"));
        text.setText(map.get("text"));
      //  status.setText(map.get("status"));

        return itemView;
    }


}

