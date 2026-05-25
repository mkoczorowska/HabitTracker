package com.example.habittracker.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Habit.class}, version = 1)
public abstract class HabitDatabase extends RoomDatabase {

    public abstract HabitDao habitDao();

    private static HabitDatabase instance;

    public static synchronized HabitDatabase getInstance(Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    HabitDatabase.class,
                    "habit_database"
            ).allowMainThreadQueries().build();
        }

        return instance;
    }
}