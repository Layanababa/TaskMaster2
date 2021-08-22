package com.example.taskmaster;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "setting";
    private List<Team> teams;
    private List<String> teamsName;
    private String teamName;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        Spinner spinner = findViewById(R.id.team_setting_spinner);
        teamsName = new ArrayList<>();

        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        teamsName.add(team.getName());
                    }
                    ArrayAdapter<String> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamsName);
                    runOnUiThread(() -> {
                        spinner.setAdapter(teamSpinnerAdapter);
                    });

                },
                error -> {
                    Log.e(TAG, "FAILED!!: ", error);
                });

        findViewById(R.id.setting).setOnClickListener((view) -> {
            EditText userName = findViewById(R.id.username);
            String username = userName.getText().toString();
            String team = ((Spinner) findViewById(R.id.team_setting_spinner)).getSelectedItem().toString();

            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Toast.makeText(getApplicationContext(), userName + "saved", Toast.LENGTH_LONG).show();
                }
            };

            Amplify.API.query(ModelQuery.list(Team.class, Team.NAME.eq(team)),
                    response -> {
                        List<Team> list = new ArrayList<>();
                        for (Team team1 : response.getData()) {
                            list.add(team1);
                        }
                        preferenceEditor.putString("UserName", username);
                        preferenceEditor.putString("teamId", list.get(0).getId());
                        preferenceEditor.putString("teamName", team);
                        Message message = handler.obtainMessage(1);
                        message.sendToTarget();
                        preferenceEditor.apply();
                    },
                    error -> {
                        Log.e(TAG, "Failed", error);
                    });

            Toast toast = Toast.makeText(this, "Saved!", Toast.LENGTH_LONG);
            toast.show();
        });

        findViewById(R.id.toHome).setOnClickListener((view) -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            SettingsActivity.this.startActivity(intent);
        });

    }

    List<Team> getTeamFromApi() {
        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        Log.i(TAG, "succeed to getTeamFromApi: Team Name --> " + team.getName());
                        teams.add(team);
                    }
                },

                error -> Log.i(TAG, "failed to getTeamFromApi: Team Name -->" + error)
        );
        return teams;
    }
}
