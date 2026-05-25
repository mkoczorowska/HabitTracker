package com.example.habittracker.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public int streak;
    public boolean completed;

    public Habit(String title, String description, int streak, boolean completed) {
        this.title = title;
        this.description = description;
        this.streak = streak;
        this.completed = completed;
    }
}