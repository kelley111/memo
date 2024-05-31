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
    public String getTask() { return task; }
    public void setTask(String task) { this.task = task; }
    public long getCompletionTime() { return completionTime; }
    public void setCompletionTime(long completionTime) { this.completionTime = completionTime; }
    public long getDeadline() { return deadline; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
}
