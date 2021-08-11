package com.example.taskmaster;

public class TaskItem {
    private final String title ;
    private final String body ;
    private final String state ;

    public TaskItem(String title, String body, String state) {
        this.title = title;
        this.body = body;
        this.state = state;
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
}
