package com.example.habittracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.example.habittracker.R;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.HabitDao;
import com.example.habittracker.models.Habit;
import com.example.habittracker.utils.QuoteApiService;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout habitsContainer;
    private TextView tvEmptyState, tvMotivationalQuote;
    private HabitDao habitDao;
    private SessionManager session;
    private boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        session = new SessionManager(this);
        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        isDark = session.isDarkMode();

        habitsContainer = findViewById(R.id.habitsContainer);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvMotivationalQuote = findViewById(R.id.tvMotivationalQuote);

        applyTheme();
        setupBottomNav();
        setupFab();
        fetchQuote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayHabits();
    }

    private void loadTodayHabits() {
        habitsContainer.removeAllViews();
        int userId = session.getUserId();
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Convert Sunday=1..Saturday=7 to Mon=1..Sun=7
        int adjustedDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<Habit> habits = habitDao.getHabitsForUserAndDay(userId, adjustedDay);

        if (habits.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            for (Habit habit : habits) {
                boolean completed = habitDao.isCompletedOnDate(habit.getId(), today);
                habit.setCompletedToday(completed);
                addHabitCard(habit, today);
            }
        }
    }

    private void addHabitCard(Habit habit, String today) {
        View card = getLayoutInflater().inflate(R.layout.item_habit, habitsContainer, false);

        TextView tvTitle = card.findViewById(R.id.tvHabitTitle);
        TextView tvStreak = card.findViewById(R.id.tvStreak);
        CheckBox cb = card.findViewById(R.id.cbDone);

        tvTitle.setText(habit.getTitle());
        tvStreak.setText(habit.getStreak() + " dni z rzędu");
        cb.setChecked(habit.isCompletedToday());



        cb.setOnClickListener(v -> {
            habitDao.toggleCompletion(habit.getId(), session.getUserId(), today);
            loadTodayHabits();
        });

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, HabitDetailsActivity.class);
            intent.putExtra("habitId", habit.getId());
            startActivity(intent);
        });

        habitsContainer.addView(card);
    }

    private void fetchQuote() {
        QuoteApiService.fetchMotivationalQuote(new QuoteApiService.QuoteCallback() {
            @Override
            public void onSuccess(String quote, String author) {
                tvMotivationalQuote.setText("„" + quote + " — " + author);
            }
            @Override
            public void onError() {
                tvMotivationalQuote.setText("Każdy dzień to nowa szansa.");
            }
        });
    }

    private void setupFab() {
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddHabitActivity.class)));
    }

    private void setupBottomNav() {
        View nav = findViewById(R.id.bottomNav);
        nav.findViewById(R.id.navHome).setOnClickListener(v -> { /* already here */ });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            overridePendingTransition(0, 0);
        });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(0, 0);
        });
    }

    private void applyTheme() {
        if (isDark) {
            findViewById(R.id.homeRoot).setBackgroundColor(Color.parseColor("#111111"));
        }
    }
}