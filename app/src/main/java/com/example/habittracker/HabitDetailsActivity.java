package com.example.habittracker;

import android.graphics.Color;
import android.os.Bundle;
import com.example.habittracker.R;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.HabitDao;
import com.example.habittracker.models.Habit;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HabitDetailsActivity extends AppCompatActivity {

    private HabitDao habitDao;
    private SessionManager session;
    private int habitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_details);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        session = new SessionManager(this);
        habitId = getIntent().getIntExtra("habitId", -1);

        if (habitId == -1) { finish(); return; }

        loadHabit();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnDelete).setOnClickListener(v -> confirmDelete());
    }

    private void loadHabit() {
        Habit habit = habitDao.getHabitById(habitId);
        if (habit == null) { finish(); return; }

        TextView tvTitle = findViewById(R.id.tvHabitTitle);
        TextView tvDesc = findViewById(R.id.tvHabitDesc);
        TextView tvStreakDays = findViewById(R.id.tvStreakDays);
        TextView tvStreakStart = findViewById(R.id.tvStreakStart);

        tvTitle.setText(habit.getTitle());
        tvDesc.setText(habit.getDescription() != null && !habit.getDescription().isEmpty()
                ? habit.getDescription() : "Brak opisu.");
        tvStreakDays.setText(String.valueOf(habit.getStreak()));

        // Calculate days since start
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date created = sdf.parse(habit.getCreatedAt());
            Date now = new Date();
            long diff = now.getTime() - created.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            tvStreakStart.setText("Śledzony od " + days + " dni (od " + habit.getCreatedAt() + ")");
        } catch (ParseException e) {
            tvStreakStart.setText("Śledzony od " + habit.getCreatedAt());
        }

        if (session.isDarkMode()) {
            applyDark(tvTitle, tvDesc, tvStreakDays, tvStreakStart);
        }
    }

    private void applyDark(TextView... views) {
        findViewById(R.id.detailsRoot).setBackgroundColor(Color.parseColor("#111111"));
        for (TextView tv : views) tv.setTextColor(Color.parseColor("#F0F0F0"));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Usuń nawyk")
                .setMessage("Czy na pewno chcesz usunąć ten nawyk? Tej operacji nie można cofnąć.")
                .setPositiveButton("Usuń", (d, w) -> {
                    habitDao.deleteHabit(habitId);
                    Toast.makeText(this, "Nawyk usunięty.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }
}