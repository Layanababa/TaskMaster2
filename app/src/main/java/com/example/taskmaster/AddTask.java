package com.example.taskmaster;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class AddTask extends AppCompatActivity {
    private static final String TAG = "addtask";
    public static final String TASK_COLLECTION = "task_collection";
    private static final int REQUEST_FOR_FILE = 999;
//    private TaskDao taskDao;
//    private AppDataBase db;
    private List<Team> teams;
    private ArrayAdapter<String> teamAdapter;
    private String[] teamsNames ;
    private String teamName ;
    private Handler handler;
    private String fileUplouded;
    private String fileName;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast toast = Toast.makeText(AddTask.this , "Task has been added" , Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        }) ;

        Spinner spinner = findViewById(R.id.team_spinner);
        teamsNames = getResources().getStringArray(R.array.team_names_array);
//        saveTeamToApi(teamsNames);
        teams = new ArrayList<>();
        getTeamFromApi();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this ,
                R.array.team_names_array, android.R.layout.simple_spinner_item
        ) ;
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamName = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                teamName = (String) parent.getItemAtPosition(0);

            }
        });

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText inputTitle = findViewById(R.id.title);
                EditText inputBody = findViewById(R.id.description);
                EditText inputState = findViewById(R.id.state_edit);

                String title = inputTitle.getText().toString();
                String body = inputBody.getText().toString();
                String state = inputState.getText().toString();

                if(fileUplouded == null){
                    fileName= "";
                }else{
                    fileName = fileUplouded;
                }

                preferenceEditor.putString("FileName", fileName);
                preferenceEditor.apply();

                if(isNetworkAvailable(getApplicationContext())){
                    Log.i(TAG, "onClick: the network is available");
                }else {
                    Log.i(TAG, "onClick: net down");
                }

                Team team = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    team = teams.stream().filter(team1 -> team1.getName().equals(teamName)).collect(Collectors.toList()).get(0);
                }
                Task task = Task.builder().title(title).body(body).state(state).team(team).fileName(fileName).build();

                saveTaskToAPI(task);
                Toast.makeText(AddTask.this, "Item Saved", Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadFileToS3();
                getFileFromDevice();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOR_FILE && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());

            File uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");
            fileUplouded = new Date().toString();
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

            Amplify.Storage.uploadFile(
                    fileUplouded ,
                    uploadFile,
                    success -> {
                        Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                    },
                    error -> {
                        Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                    }
            );
        }
    }

    private void getFileFromDevice() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a File");
        startActivityForResult(chooseFile, REQUEST_FOR_FILE); // deprecated
    }

//    private void getFileFromS3Storage() {
//        Amplify.Storage.downloadFile(
//                "uploadFile",
//                new File(),
//                success -> {
//                    // displaying in an image view
//                    success.getFile();
//                },
//                error -> {}
//        );
//    }

    private void uploadFileToS3() {
        File testFile = new File(getApplicationContext().getFilesDir(), "test");

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(testFile));
            bufferedWriter.append("This is a test file to demonstrate S3 functionality");
            bufferedWriter.close();
        } catch (Exception exception) {
            Log.e(TAG, "uploadFileToS3: failed" + exception.toString());
        }

        Amplify.Storage.uploadFile(
                "test",
                testFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );
    }

     Task saveTaskToAPI (Task task){
                Amplify.API.mutate(ModelMutation.create(task),
                    success -> {
                    Log.i(TAG, "Saved item: " + success.getData().getTitle ());
                    handler.sendEmptyMessage(1);
                    },
                    error -> Log.e(TAG, "Could not save item to API/Dynamodb", error)
                );
                return task;
    }

    public boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    List<Team> getTeamFromApi(){
        Amplify.API.query(ModelQuery.list(Team.class) ,
                response -> {
                    for (Team team : response.getData()) {
                        Log.i(TAG, "succeed to getTeamFromApi: Team Name --> "+ team.getName());
                        teams.add(team) ;
                    }
                },

                error -> Log.i(TAG, "failed to getTeamFromApi: Team Name -->" + error)
                );
        return teams ;
    }

    void saveTeamToApi(String[] teams){
        for (String name : teams){
            Team team = Team.builder().name(name).build();
            Amplify.API.mutate(ModelMutation.create(team),
                    success -> Log.i(TAG, "Successfully"),
                    error -> Log.i(TAG, "failed " + error)
            );
        }


   }
}