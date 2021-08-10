package com.example.taskmaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
