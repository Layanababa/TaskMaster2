package com.example.taskmaster;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        findViewById(R.id.setting).setOnClickListener((view)->{
            EditText userName = findViewById(R.id.username);

            preferenceEditor.putString("UserName", userName.getText().toString());
            preferenceEditor.apply();

            Toast toast = Toast.makeText(this,"Saved!", Toast.LENGTH_LONG);
            toast.show();
        });

        findViewById(R.id.toHome).setOnClickListener((view)->{
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            SettingsActivity.this.startActivity(intent);
        });

    }
}
