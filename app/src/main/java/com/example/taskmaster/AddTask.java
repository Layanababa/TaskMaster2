package com.example.taskmaster;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


public class AddTask extends AppCompatActivity  {
    private static final String TAG = "addtask";
    public static final String TASK_COLLECTION = "task_collection";
    private static final int REQUEST_FOR_FILE = 999;
    private static final int PERMISSION_ID = 44;
    private List<Team> teams;
    private ArrayAdapter<String> teamAdapter;
    private String[] teamsNames ;
    private String teamName ;
    private Handler handler;
    private String fileUplouded;
    private String fileName;
    FusedLocationProviderClient mFusedLocationClient;
    Location currentLocation;
    String addressString;
    private double latitude;
    private double longitude;
    GoogleMap googleMap;


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        Intent intent = getIntent();
        String action = intent.getAction();
        String type   = intent.getType();

        if (type != null)
            if (type.equals("image/*"))
                sendImage(intent) ;


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


                getLastLocation();

                if(isNetworkAvailable(getApplicationContext())){
                    Log.i(TAG, "onClick: the network is available");
                }else {
                    Log.i(TAG, "onClick: net down");
                }

                Team team = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    team = teams.stream().filter(team1 -> team1.getName().equals(teamName)).collect(Collectors.toList()).get(0);
                }


                Task task = Task.builder().title(title).body(body).state(state).team(team).fileName(fileName).location(addressString).build();

                saveTaskToAPI(task);
                Toast.makeText(AddTask.this, "Item Saved", Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @SuppressLint("MissingPermission")
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void sendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String path = getPathFromUri( getApplicationContext(), imageUri) ;
        Log.i(TAG, "sendImage: path" + path);
        path = path.replace(" " , "");
        File uploadFile = new File(path);
        try {
            InputStream inputStream = getContentResolver().openInputStream(intent.getData());
            FileUtils.copy(inputStream , new FileOutputStream(uploadFile));

        } catch(Exception exception){
            Log.i(TAG, "sendImage: called" + path);
        }

        uploadFileToS3(uploadFile);
    }

    String getPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void uploadFileToS3(File uploadFile){

        String key =String.format("defaultTask%s.jpg" , new Date().getTime());

        Amplify.Storage.uploadFile(
                key,
                uploadFile ,
                success -> Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey()) ,
                failure -> Log.e(TAG, "uploadFileToS3: failed " + failure.toString())
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            String lon = String.valueOf(longitude);
                            String lat = String.valueOf(latitude);
                            Task taskk = Task.builder().locationLat(lat).locationLon(lon).build();
                            saveTaskToAPI(taskk);

//                            googleMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(latitude, longitude))
//                                    .title("Marker"));
                        }
                    }



                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    private boolean checkPermissions() {
        Log.i(TAG,"permission Checking");
        return ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat
                        .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED;

    }
    private boolean isLocationEnabled() {
        Log.i(TAG,"location Enabled");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            Geocoder geocoder = new Geocoder(AddTask.this, Locale.getDefault());


        }
    };

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

    }

    private void requestPermissions() {
        Log.i(TAG, "requestPermissions:  Lcation data");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }


}