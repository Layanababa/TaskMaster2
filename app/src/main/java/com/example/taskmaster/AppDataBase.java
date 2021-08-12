package com.example.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TaskItem.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
