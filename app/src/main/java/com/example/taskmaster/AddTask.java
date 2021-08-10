package com.example.taskmaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


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