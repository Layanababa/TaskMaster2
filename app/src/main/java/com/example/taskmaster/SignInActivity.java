package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "signIn";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
                return false;
            }
        });

        findViewById(R.id.btnlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameInput = findViewById(R.id.username);
                EditText passwordInput = findViewById(R.id.pass);

                String usernamSignIn = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                signIn(usernamSignIn,password);


            }
        });
    }

    void signIn(String username , String password){
        Amplify.Auth.signIn(username , password ,
                success -> {
                    Log.i(TAG, "successfully signed in -->: " + success.toString());
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                } ,
                failure -> Log.i(TAG, "failed to sign in --> " + failure.toString())
        );
    }
}