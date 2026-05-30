package com.example.habittracker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean dark = getSharedPreferences("HabitTrackerSession", MODE_PRIVATE)
                .getBoolean("darkMode", false);

        // Ustaw motyw przed super.onCreate
        if (dark) {
            setTheme(R.style.Theme_HabitTracker_Dark);
        } else {
            setTheme(R.style.Theme_HabitTracker);
        }

        super.onCreate(savedInstanceState);

        // Ustaw tło okna natychmiast po super.onCreate — eliminuje czarny flash
        int bgColor = dark
                ? Color.parseColor("#0D0E1A")
                : Color.parseColor("#F7F8FC");
        getWindow().setBackgroundDrawable(new ColorDrawable(bgColor));
    }
}