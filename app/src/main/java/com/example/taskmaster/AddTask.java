package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;


public class AddTask extends AppCompatActivity {

    public static final String TASK_COLLECTION = "task_collection";
    private TaskDao taskDao;
    private AppDataBase db;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, TASK_COLLECTION).allowMainThreadQueries().build();
        taskDao = db.taskDao();

        findViewById(R.id.button_add).setOnClickListener((view) ->{
            EditText inputTitle = findViewById(R.id.title);
            EditText inputBody = findViewById(R.id.description);
            EditText inputState = findViewById(R.id.state_edit);

            String title = inputTitle.getText().toString();
            String body = inputBody.getText().toString();
            String state = inputState.getText().toString();

            TaskItem taskItem = new TaskItem(title, body, state);

            taskDao.addItem(taskItem);
            Toast toast = Toast.makeText(this,"Submitted!", Toast.LENGTH_LONG);
            toast.show();

            Intent intent = new Intent( this, TaskActivity.class);
            startActivity(intent);
        });

    }


}