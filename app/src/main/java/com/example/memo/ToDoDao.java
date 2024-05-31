package com.example.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ToDoDao {
    private DBHelper dbHelper;
    private ToDoItem toDoItem;

    public ToDoDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insert(ToDoItem toDoItem) {
        this.toDoItem = toDoItem;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task", toDoItem.getTask());
        values.put("completion_time", toDoItem.getCompletionTime());
        values.put("deadline", toDoItem.getDeadline());
        db.insert(DBHelper.TB3_NAME, null, values);
        db.close();
    }

    public List<ToDoItem> getAllToDoItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ToDoItem> toDoItems = new ArrayList<>();
        Cursor cursor = db.query(DBHelper.TB3_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                ToDoItem toDoItem = new ToDoItem(
                        cursor.getString(cursor.getColumnIndexOrThrow("task")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("completion_time")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("deadline"))
                );
                toDoItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                toDoItems.add(toDoItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toDoItems;
    }
}
