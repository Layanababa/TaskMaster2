package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Expense;
import com.amplifyframework.datastore.generated.model.Task;


public class AddTask extends AppCompatActivity {
    private static final String TAG = "main";
    public static final String TASK_COLLECTION = "task_collection";
    private TaskDao taskDao;
    private AppDataBase db;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

//        try{
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//            Log.i(TAG, "onCreate: successfully initilaized amplify");
//        }catch(AmplifyException exception){
//            Log.e(TAG, "onCreate: Failed to initilaize amplify", exception);
//        }

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputTitle = findViewById(R.id.title);
                EditText inputBody = findViewById(R.id.description);
                EditText inputState = findViewById(R.id.state_edit);

                String title = inputTitle.getText().toString();
                String body = inputBody.getText().toString();
                String state = inputState.getText().toString();

                Task task = Task.builder().title(title).body(body).state(state).build();

                if(isNetworkAvailable(getApplicationContext())){
                    Log.i(TAG, "onClick: the network is available");
                }else {
                    Log.i(TAG, "onClick: net down");
                }

                saveTaskToAPI(task);
                Toast.makeText(AddTask.this, "Item Saved", Toast.LENGTH_SHORT).show();

            }
        });


        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, TASK_COLLECTION).allowMainThreadQueries().build();
        taskDao = db.taskDao();

//        findViewById(R.id.button_add).setOnClickListener((view) ->{
//            EditText inputTitle = findViewById(R.id.title);
//            EditText inputBody = findViewById(R.id.description);
//            EditText inputState = findViewById(R.id.state_edit);
//
//            String title = inputTitle.getText().toString();
//            String body = inputBody.getText().toString();
//            String state = inputState.getText().toString();
//
//            TaskItem taskItem = new TaskItem(title, body, state);
//
//            taskDao.addItem(taskItem);
//            Toast toast = Toast.makeText(this,"Submitted!", Toast.LENGTH_LONG);
//            toast.show();
//
//            Intent intent = new Intent( this, TaskActivity.class);
//            startActivity(intent);
//        });

    }

     private void saveTaskToAPI (Task task){
                Amplify.API.mutate(ModelMutation.create(task),
                    success -> Log.i(TAG, "Saved item: " + success.getData().getTitle ()),
                    error -> Log.e(TAG, "Could not save item to API/Dynamodb", error)
                );
    }

    public boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}