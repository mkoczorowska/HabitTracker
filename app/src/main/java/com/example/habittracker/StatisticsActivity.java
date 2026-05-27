package com.example.habittracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.example.habittracker.R;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.HabitDao;
import com.example.habittracker.models.Habit;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private HabitDao habitDao;
    private SessionManager session;
    private boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        session = new SessionManager(this);
        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        isDark = session.isDarkMode();



        loadStats();
        setupBottomNav();
    }

    private void loadStats() {
        int userId = session.getUserId();
        List<Habit> habits = habitDao.getAllHabitsForUser(userId);

        TextView tvTopName = findViewById(R.id.tvTopHabitName);
        TextView tvTopStreak = findViewById(R.id.tvTopHabitStreak);
        LinearLayout allContainer = findViewById(R.id.allHabitsContainer);

        if (!habits.isEmpty()) {
            Habit top = habits.get(0); // sorted by streak DESC
            tvTopName.setText(top.getTitle());
            tvTopStreak.setText("🔥 " + top.getStreak() + " dni z rzędu");
        } else {
            tvTopName.setText("Brak nawyków");
            tvTopStreak.setText("Dodaj swój pierwszy nawyk!");
        }

        allContainer.removeAllViews();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        for (Habit habit : habits) {
            View card = getLayoutInflater().inflate(R.layout.item_habit, allContainer, false);
            TextView tvTitle = card.findViewById(R.id.tvHabitTitle);
            TextView tvStreak = card.findViewById(R.id.tvStreak);
            android.widget.CheckBox cb = card.findViewById(R.id.cbDone);

            tvTitle.setText(habit.getTitle());
            tvStreak.setText(habit.getStreak() + " dni z rzędu");
            cb.setChecked(habitDao.isCompletedOnDate(habit.getId(), today));



            final int hId = habit.getId();
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, HabitDetailsActivity.class);
                intent.putExtra("habitId", hId);
                startActivity(intent);
            });

            allContainer.addView(card);
        }
    }

    private void setupBottomNav() {
        View nav = findViewById(R.id.bottomNavStats);
        nav.findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> { /* already here */ });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}