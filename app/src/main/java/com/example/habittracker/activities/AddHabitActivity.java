package com.example.habittracker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.habittracker.R;
import com.example.habittracker.database.Habit;
import com.example.habittracker.database.HabitDatabase;

public class AddHabitActivity extends AppCompatActivity {

    EditText titleInput;
    EditText descriptionInput;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(v -> {

            String title =
                    titleInput.getText().toString();

            String description =
                    descriptionInput.getText().toString();

            Habit habit =
                    new Habit(title, description,
                            0, false);

            HabitDatabase
                    .getInstance(this)
                    .habitDao()
                    .insert(habit);

            finish();
        });
    }
}