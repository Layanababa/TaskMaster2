package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Expense;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private static final String TAG = "mainActivity";
    public static String TASKTITLE = "taskTitle";
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userName = findViewById(R.id.homeusername);
        TextView teamName = findViewById(R.id.team);
        userName.setText(preferences.getString("UserName","User Name"));
        teamName.setText(preferences.getString("teamName","Team Name"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureAmplify();

        Button addTaskButton = findViewById(R.id.button_first);
        addTaskButton.setOnClickListener(toAddTask);

        findViewById(R.id.button_second).setOnClickListener(toAllTask);

        findViewById(R.id.button_settings).setOnClickListener(toSettings);

        findViewById(R.id.button_task_details).setOnClickListener(toTaskDetails1);
        findViewById(R.id.button_task_details1).setOnClickListener(toTaskDetails2);
        findViewById(R.id.button_task_details2).setOnClickListener(toTaskDetails3);

        findViewById(R.id.task_activity).setOnClickListener(toTaskActivity);
    }

    public View.OnClickListener toAddTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), AddTask.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener toAllTask = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), AllTask.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener toSettings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);


        }
    };

    public View.OnClickListener toTaskDetails1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button createTaskDetails = findViewById(R.id.button_task_details);
            String title = createTaskDetails.getText().toString();
            Intent intent = new Intent(getBaseContext(), TaskDetails.class);
            intent.putExtra(TASKTITLE, title);
            startActivity(intent);


        }
    };

    public View.OnClickListener toTaskDetails2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button createTaskDetails2 = findViewById(R.id.button_task_details1);
            String title = createTaskDetails2.getText().toString();
            Intent intent = new Intent(getBaseContext(), TaskDetails.class);
            intent.putExtra(TASKTITLE, title);
            startActivity(intent);


        }
    };

    public View.OnClickListener toTaskDetails3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button createTaskDetails3 = findViewById(R.id.button_task_details2);
            String title = createTaskDetails3.getText().toString();
            Intent intent = new Intent(getBaseContext(), TaskDetails.class);
            intent.putExtra(TASKTITLE, title);
            startActivity(intent);


        }
    };

    public View.OnClickListener toTaskActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(), TaskActivity.class);
            startActivity(intent);


        }
    };

    void configureAmplify(){
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

        } catch(AmplifyException exception){
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }

    }
}