package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.HabitDao;
import com.example.habittracker.models.Habit;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends BaseActivity {

    private HabitDao habitDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        session = new SessionManager(this);
        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        // Animacja nagłówka
        View header = findViewById(R.id.statsTop);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        header.startAnimation(fadeIn);

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
            Habit top = habits.get(0);
            tvTopName.setText(top.getTitle());
            tvTopStreak.setText("🔥 " + top.getStreak() + " dni z rzędu");
        } else {
            tvTopName.setText("Brak nawyków");
            tvTopStreak.setText("Dodaj swój pierwszy nawyk!");
        }

        allContainer.removeAllViews();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (int i = 0; i < habits.size(); i++) {
            Habit habit = habits.get(i);
            View card = getLayoutInflater().inflate(R.layout.item_habit, allContainer, false);
            TextView tvTitle = card.findViewById(R.id.tvHabitTitle);
            TextView tvStreak = card.findViewById(R.id.tvStreak);
            android.widget.CheckBox cb = card.findViewById(R.id.cbDone);

            tvTitle.setText(habit.getTitle());
            tvStreak.setText(habit.getStreak() + " dni z rzędu");
            cb.setChecked(habitDao.isCompletedOnDate(habit.getId(), today));
            cb.setEnabled(false);
            cb.setClickable(false);

            ThemeHelper.applyToView(card, session.isDarkMode());

            // Staggered slide-up
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideUp.setStartOffset(i * 60L);
            card.startAnimation(slideUp);

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
            finish();
        });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> { /* już tu */ });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });
    }
}