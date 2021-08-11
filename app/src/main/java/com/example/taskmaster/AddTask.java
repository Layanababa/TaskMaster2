package com.example.taskmaster;

import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        findViewById(R.id.button_add).setOnClickListener((view) ->{
            Toast toast = Toast.makeText(this,"Submitted!", Toast.LENGTH_LONG);
            toast.show();
        });

    }
}