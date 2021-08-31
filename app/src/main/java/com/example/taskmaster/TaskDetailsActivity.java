package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details_activity);

        Intent intent = getIntent();

        String title = intent.getExtras().getString(TaskActivity.TASK_TITLE);
        String body = intent.getExtras().getString(TaskActivity.TASK_BODY);
        String state = intent.getExtras().getString(TaskActivity.TASK_STATE);

        TextView titleTextView = findViewById(R.id.title_details);
        TextView bodyTextView = findViewById(R.id.body_details);
        TextView stateTextView = findViewById(R.id.state_details);

        titleTextView.setText(title);
        bodyTextView.setText(body);
        stateTextView.setText(state);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView fileName = findViewById(R.id.File_details);

        fileName.setText(preferences.getString("FileName","File Name"));

    }
}