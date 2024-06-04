package com.example.memo;

public class ToDoItem {
    private int id;
    private String task;
    private long completionTime;
    private long deadline;

    public ToDoItem(String task, long completionTime, long deadline) {
        this.task = task;
        this.completionTime = completionTime;
        this.deadline = deadline;
    }

    // Getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

}
