package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskActivity extends AppCompatActivity {
    private static final String TAG = "task";
    private List<TaskItem> taskItemList;
    private TaskAdapter adapter;
    private Handler handler;

    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        RecyclerView taskRecyclerView = findViewById(R.id.list);

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Objects.requireNonNull(taskRecyclerView.getAdapter()).notifyDataSetChanged();
                return false;
            }
        });

        taskItemList =  new ArrayList<>();

        getDataFromApi() ;



        adapter= new TaskAdapter(taskItemList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(getApplicationContext(), TaskDetailsActivity.class);
                intent.putExtra(TASK_TITLE, taskItemList.get(position).getTitle());
                intent.putExtra(TASK_BODY, taskItemList.get(position).getBody());
                intent.putExtra(TASK_STATE, taskItemList.get(position).getState());

                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);

    }

    void getDataFromApi(){
        Amplify.API.query(ModelQuery.list(Task.class),
                response -> {
                    for (Task task: response.getData()){
                        taskItemList.add(new TaskItem(task.getTitle(), task.getBody(), task.getState()));
                        Log.i(TAG, "getDataFromApi: from api ");
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "getDataFromApi: Failed ",error ));
    }

}