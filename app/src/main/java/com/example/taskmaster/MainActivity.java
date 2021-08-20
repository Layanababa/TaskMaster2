package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        userName.setText(preferences.getString("UserName","Go to settings to set the username."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureAmplify();
//        try{
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//            Log.i(TAG, "onCreate: successfully initilaized amplify");
//        }catch(AmplifyException exception){
//            Log.e(TAG, "onCreate: Failed to initilaize amplify", exception);
//        }

//        Expense expenceItem = Expense.builder().("name").description("desc").build();

//        local data store
//        Amplify.DataStore.save(expenceItem,
//                    success -> Log.i(TAG, "Saved item: " + success.item().getName()),
//                    error -> Log.e(TAG, "Could not save item to DataStore", error)
//                );
//
//        Amplify.DataStore.query(Expense.class,
//                expenseIterator -> {
//                    while (expenseIterator.hasNext()) {
//                        Expense expense = expenseIterator.next();
//
//                        Log.i(TAG, "==== Expense ====");
//                        Log.i(TAG, "Name: " + expense.getName());
//
//
//                    }
//                },
//                failure -> Log.e(TAG, "Could not query DataStore", failure)
//        );

//        Amplify.API.mutate(ModelMutation.create(expenceItem),
//                    success -> Log.i(TAG, "Saved item: " + success.getData().getName()),
//                    error -> Log.e(TAG, "Could not save item to API/Dynamodb", error)
//                );
//
//        Amplify.API.query(ModelQuery.list(Expense.class), response -> {
//            List<Expense> expenseList =  new ArrayList<>();
//            for(Expense expense : response.getData()){
//                expenseList.add(expense);
//                Log.i(TAG, "the response are => " + expense.getName());
//            }
//        }, error -> Log.e(TAG, "Failed to get expense => " + error.toString())
//        );

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