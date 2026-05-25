package com.example.habittracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habittracker.R;
import com.example.habittracker.adapter.HabitAdapter;
import com.example.habittracker.database.Habit;
import com.example.habittracker.database.HabitDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button addHabitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        addHabitBtn = findViewById(R.id.addHabitBtn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Habit> habits = HabitDatabase
                .getInstance(this)
                .habitDao()
                .getAllHabits();

        HabitAdapter adapter = new HabitAdapter(habits);

        recyclerView.setAdapter(adapter);

        addHabitBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(MainActivity.this,
                            AddHabitActivity.class);

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Habit> habits = HabitDatabase
                .getInstance(this)
                .habitDao()
                .getAllHabits();

        HabitAdapter adapter = new HabitAdapter(habits);

        recyclerView.setAdapter(adapter);
    }
}