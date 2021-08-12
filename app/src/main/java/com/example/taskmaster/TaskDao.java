package com.example.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void addItem(TaskItem taskItem);

    @Query("SELECT * FROM taskItem where task_title Like :title")
    TaskItem findByName(String title);

    @Query("SELECT * FROM taskItem")
    List<TaskItem> findAll();
}
