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
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

import java.util.Date;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private static final Object TAG = "signup";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        configureAmplify();

        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast.makeText(getApplicationContext() , "signed up successfully " , Toast.LENGTH_LONG).show();
                return false;
            }
        }) ;


        findViewById(R.id.btnsignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameInput = findViewById(R.id.eteusername);
                EditText emailInput = findViewById(R.id.etemail);
                EditText passwordInput = findViewById(R.id.mypass);

                String username = usernameInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                signUp(username, password, email);

                Intent intent = new Intent(getApplicationContext() , Verification.class);
                intent.putExtra("username", username);
                recordAnEvent("NavigateToSignInActivity");
                startActivity(intent);

                preferenceEditor.putString("UserNameSignIn", username);
                preferenceEditor.apply();


            }
        });

        findViewById(R.id.btnsignip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , SignInActivity.class);
                recordAnEvent("NavigateToSignInActivity");
                startActivity(intent);
            }
        });
    }

    private void signUp(String username ,  String password , String email ){
        Amplify.Auth.signUp(username , password ,
                AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email() , email)
                        .build() ,
                success -> {
                    Log.i((String) TAG, "signUp: succeeded --> " + success);
                    handler.sendEmptyMessage(1);
                } ,
                failure -> Log.i((String) TAG, "signUp: failed --> " + failure)
        );
    }

    void configureAmplify(){
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(getApplication()));
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
        } catch(AmplifyException exception){
            Log.e((String) TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }

    }
    public void recordAnEvent(String eventName) {
        Random random = new Random();
        Integer randomAge = random.nextInt(50) + 15;
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name(eventName)
                .addProperty("Channel", "SMS")
                .addProperty("Successful", true)
                .addProperty("ProcessDuration", 792)
                .addProperty("UserAge", randomAge)
                .addProperty("Date", String.valueOf(new Date()))
                .build();

        Amplify.Analytics.recordEvent(event);
    }
}