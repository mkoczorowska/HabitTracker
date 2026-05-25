package com.example.habittracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert
    void insert(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("SELECT * FROM habits")
    List<Habit> getAllHabits();

    @Query("UPDATE habits SET completed = :completed WHERE id = :id")
    void updateStatus(int id, boolean completed);
}