package com.example.taskmaster;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Expense;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>(){
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            final String token = task.getResult();
                            Log.d("TAG", "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }


    private static final String TAG = "mainActivity";
    public static String TASKTITLE = "taskTitle";
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView userName = findViewById(R.id.homeusername);
        TextView teamName = findViewById(R.id.team);
        TextView acountUser = findViewById(R.id.account);
        userName.setText(preferences.getString("UserName","User Name"));
        teamName.setText(preferences.getString("teamName","Team Name"));
        acountUser.setText(preferences.getString("UserNameSignIn","Account User Name"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        findViewById(R.id.mainsignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplify.Auth.signOut(
                        () -> {
                            Log.i("AuthQuickstart", "Signed out successfully");
                            Intent intent = new Intent(getBaseContext(), SignInActivity.class);
                            startActivity(intent);

                        },
                        error -> {
                           Log.e("AuthQuickstart", error.toString());
                        }

                );
            }
        });

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


}