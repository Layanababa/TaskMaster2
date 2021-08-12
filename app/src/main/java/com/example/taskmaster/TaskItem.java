package com.example.taskmaster;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TaskItem {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "task_title")
    private final String title ;

    private final String body ;
    private final String state ;

    public TaskItem(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    public void setId(long id) {
        this.id = id;
    }
}
