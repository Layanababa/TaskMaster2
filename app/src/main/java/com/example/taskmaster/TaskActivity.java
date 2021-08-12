package com.example.taskmaster;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    private List<TaskItem> taskItemList;
    private TaskAdapter adapter;

    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";

    private TaskDao taskDao;
    private AppDataBase db;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        db = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, AddTask.TASK_COLLECTION).allowMainThreadQueries().build();

        taskDao= db.taskDao();
        taskItemList = taskDao.findAll();

        RecyclerView taskRecyclerView = findViewById(R.id.list);

//        taskItemList= new ArrayList<>();
//
//        taskItemList.add(new TaskItem("task 1","body 1 ","complete"));
//        taskItemList.add(new TaskItem("task 2","body 2 ","new"));
//        taskItemList.add(new TaskItem("task 3","body 3 ","assigned"));
//        taskItemList.add(new TaskItem("task 4","body 4 ","new"));
//        taskItemList.add(new TaskItem("task 5","body 5 ","progress"));
//        taskItemList.add(new TaskItem("task 6","body 6 ","progress"));
//        taskItemList.add(new TaskItem("task 7","body 7 ","new"));
//        taskItemList.add(new TaskItem("task 8","body 8 ","complete"));
//        taskItemList.add(new TaskItem("task 9","body 9 ","new"));
//        taskItemList.add(new TaskItem("task 10","body 10 ","assigned"));

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


}
