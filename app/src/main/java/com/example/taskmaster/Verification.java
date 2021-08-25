package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.core.Amplify;

import java.util.Date;
import java.util.Random;

public class Verification extends AppCompatActivity {
    private static final String TAG = "verification";
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast.makeText(getApplicationContext(), "your account has been confirmed successfully", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText verificationCode = findViewById(R.id.confirmationCode);
                String confirmeCode = verificationCode.getText().toString();
                String username = getIntent().getExtras().getString("username");

                confirmeUser(username, confirmeCode);
            }
        });

    }

    void confirmeUser(String username, String confirmeCode){
        Amplify.Auth.confirmSignUp(username , confirmeCode ,
                success -> {
                    Log.i(TAG, "your account has been confirmed successfully --> " + success.toString());
                    handler.sendEmptyMessage(1);
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    recordAnEvent("NavigateToSignInActivity");
                    startActivity(intent);
                } ,
                failure -> Log.i(TAG, "failed to verificate the account --> " + failure.toString())
        );
    }

    private void recordAnEvent(String eventName){
        Random random = new Random();
        Integer randomAge = random.nextInt(50) + 15;
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name(eventName)
                .addProperty("Channel", "SMS")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .addProperty("UserAge", randomAge)
                .addProperty("Date" , String.valueOf(new Date()))
                .build();

        Amplify.Analytics.recordEvent(event);
    }
}