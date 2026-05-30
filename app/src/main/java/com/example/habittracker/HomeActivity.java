package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class HomeActivity extends BaseActivity {

    private LinearLayout habitsContainer;
    private LinearLayout emptyState;
    private TextView tvMotivationalQuote;
    private TextView tvProgressLabel;
    private TextView tvProgressPercent;
    private View progressFill;
    private HabitDao habitDao;
    private SessionManager session;
    private boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // WAŻNE: inicjalizacja session PRZED użyciem
        session = new SessionManager(this);
        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        isDark = session.isDarkMode();

        ThemeHelper.apply(this, findViewById(android.R.id.content), isDark);

        habitsContainer = findViewById(R.id.habitsContainer);
        emptyState = findViewById(R.id.tvEmptyState);
        tvMotivationalQuote = findViewById(R.id.tvMotivationalQuote);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressFill = findViewById(R.id.progressFill);

        updateGreeting();
        setupBottomNav();
        setupFab();
        fetchQuote();

        // Animacja wejścia nagłówka
        View header = findViewById(R.id.headerSection);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        header.startAnimation(fadeIn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodayHabits();
    }

    private void updateGreeting() {
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12) greeting = "Dzień dobry! ☀️";
        else if (hour < 18) greeting = "Dzisiejsze nawyki";
        else greeting = "Dobry wieczór! 🌙";
        tvGreeting.setText(greeting);
    }

    private void loadTodayHabits() {
        habitsContainer.removeAllViews();

        int userId = session.getUserId();
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int adjustedDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        List<Habit> habits = habitDao.getHabitsForUserAndDay(userId, adjustedDay);

        if (habits.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            updateProgress(0, 0);
        } else {
            emptyState.setVisibility(View.GONE);
            int completed = 0;
            for (int i = 0; i < habits.size(); i++) {
                Habit habit = habits.get(i);
                boolean done = habitDao.isCompletedOnDate(habit.getId(), today);
                habit.setCompletedToday(done);
                if (done) completed++;
                addHabitCard(habit, today, i);
            }
            updateProgress(completed, habits.size());
        }
    }

    private void updateProgress(int done, int total) {
        if (total == 0) {
            tvProgressLabel.setText("Postęp dziś: 0/0");
            tvProgressPercent.setText("0%");
            progressFill.getLayoutParams().width = 0;
            progressFill.requestLayout();
            return;
        }
        int pct = (int) ((done / (float) total) * 100);
        tvProgressLabel.setText("Postęp dziś: " + done + "/" + total);
        tvProgressPercent.setText(pct + "%");

        // Animuj pasek postępu
        progressFill.post(() -> {
            int maxWidth = progressFill.getParent() instanceof View
                    ? ((View) progressFill.getParent()).getWidth() : 0;
            int targetWidth = (int) (maxWidth * pct / 100f);
            android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofInt(0, targetWidth);
            anim.setDuration(600);
            anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
            anim.addUpdateListener(a -> {
                progressFill.getLayoutParams().width = (int) a.getAnimatedValue();
                progressFill.requestLayout();
            });
            anim.start();
        });
    }

    private void addHabitCard(Habit habit, String today, int index) {
        View card = getLayoutInflater().inflate(R.layout.item_habit, habitsContainer, false);

        TextView tvTitle = card.findViewById(R.id.tvHabitTitle);
        TextView tvStreak = card.findViewById(R.id.tvStreak);
        CheckBox cb = card.findViewById(R.id.cbDone);

        tvTitle.setText(habit.getTitle());
        tvStreak.setText(habit.getStreak() + " dni z rzędu");
        cb.setChecked(habit.isCompletedToday());

        // Przekreśl tytuł jeśli ukończony
        if (habit.isCompletedToday()) {
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            tvTitle.setAlpha(0.55f);
        }

        cb.setOnClickListener(v -> {
            habitDao.toggleCompletion(habit.getId(), session.getUserId(), today);
            // Animacja scale na checkboxie
            Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
            cb.startAnimation(scaleIn);
            loadTodayHabits();
        });

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, HabitDetailsActivity.class);
            intent.putExtra("habitId", habit.getId());
            startActivity(intent);
        });

        ThemeHelper.applyToView(card, isDark);

        // Staggered slide-up dla każdej karty
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(index * 60L);
        card.startAnimation(slideUp);

        habitsContainer.addView(card);
    }

    private void fetchQuote() {
        QuoteApiService.fetchMotivationalQuote(new QuoteApiService.QuoteCallback() {
            @Override
            public void onSuccess(String quote, String author) {
                tvMotivationalQuote.setText("„" + quote + "” — " + author);
            }
            @Override
            public void onError() {
                tvMotivationalQuote.setText("Każdy dzień to nowa szansa na lepszą wersję siebie.");
            }
        });
    }

    private void setupFab() {
        View fab = findViewById(R.id.fabAdd);

        // Bounce animacja na start
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        scaleIn.setStartOffset(400);
        fab.startAnimation(scaleIn);

        fab.setOnClickListener(v -> {
            // Krótki scale down/up przy tapie
            v.animate().scaleX(0.88f).scaleY(0.88f).setDuration(80)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(120)
                            .withEndAction(() -> {
                                startActivity(new Intent(this, AddHabitActivity.class));
                            }).start())
                    .start();
        });
    }

    private void setupBottomNav() {
        View nav = findViewById(R.id.bottomNav);
        nav.findViewById(R.id.navHome).setOnClickListener(v -> { /* już tu */ });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
        });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }
}