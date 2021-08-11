package com.example.taskmaster;

import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetails extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details);

        String taskTitle = getIntent().getStringExtra(MainActivity.TASKTITLE);
        TextView title = findViewById(R.id.task_details);
        title.setText(taskTitle);

    }
}
