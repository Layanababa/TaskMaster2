package com.example.taskmaster;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AddTask extends AppCompatActivity {
    private static final String TAG = "addtask";
    public static final String TASK_COLLECTION = "task_collection";
    private TaskDao taskDao;
    private AppDataBase db;
    private List<Team> teams;
    private ArrayAdapter<String> teamAdapter;
    private String[] teamsNames ;
    private String teamName ;
    private Handler handler;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast toast = Toast.makeText(AddTask.this , "Task has been added" , Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        }) ;

        teams = new ArrayList<>();
        getTeamFromApi();

        Spinner spinner = findViewById(R.id.team_spinner);
        teamsNames = getResources().getStringArray(R.array.team_names_array);

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

//                if(isNetworkAvailable(getApplicationContext())){
//                    Log.i(TAG, "onClick: the network is available");
//                }else {
//                    Log.i(TAG, "onClick: net down");
//                }

                Team team = teams.stream().filter(team1 -> team1.getName().equals(teamName)).collect(Collectors.toList()).get(0);
                Task task = Task.builder().title(title).body(body).state(state).team(team).build();

                saveTaskToAPI(task);
                Toast.makeText(AddTask.this, "Item Saved", Toast.LENGTH_SHORT).show();

            }
        });


        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, TASK_COLLECTION).allowMainThreadQueries().build();
        taskDao = db.taskDao();


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

//    public boolean isNetworkAvailable(Context context){
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
//    }

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