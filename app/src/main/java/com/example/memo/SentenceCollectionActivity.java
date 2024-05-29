package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class SentenceCollectionActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db;
    String sentence, review_text, source, date;
    ArrayList<HashMap<String, String>> listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_collection);

        dbHelper = new DBHelper(this);
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("sentence_data", new String[]{"sentence", "source", "review_text", "date"},
                null, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            do {
                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence"));
                review_text = cursor.getString(cursor.getColumnIndexOrThrow("review_text"));
                source = cursor.getString(cursor.getColumnIndexOrThrow("source"));
                date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                HashMap<String, String> map = new HashMap<>();
                map.put("sentence", sentence);
                map.put("review_text", review_text);
                map.put("source", source);
                map.put("date", date);
                listItems.add(map);
            } while (cursor.moveToPrevious());

            SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.sentence_celection_read_list,
                    new String[]{"sentence", "review_text", "source", "date"},
                    new int[]{R.id.sentence, R.id.mywords, R.id.source, R.id.sentence_time});
            ListView listView = findViewById(R.id.sentence_collection_read_listview);
            listView.setAdapter(adapter);
        } else {
            TextView sentence_tip = findViewById(R.id.sentence_tip);
            sentence_tip.setVisibility(View.VISIBLE);
        }
    }
}
