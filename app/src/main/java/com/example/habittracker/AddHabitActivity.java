package com.example.habittracker;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.HabitDao;
import com.example.habittracker.models.Habit;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddHabitActivity extends BaseActivity {

    private EditText etTitle, etDesc;
    private final boolean[] selectedDays = new boolean[7];
    private TextView[] dayViews;
    private HabitDao habitDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        session = new SessionManager(this);
        habitDao = new HabitDao(DatabaseHelper.getInstance(this));
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        etTitle = findViewById(R.id.etHabitTitle);
        etDesc = findViewById(R.id.etHabitDesc);

        dayViews = new TextView[]{
                findViewById(R.id.tvMon), findViewById(R.id.tvTue),
                findViewById(R.id.tvWed), findViewById(R.id.tvThu),
                findViewById(R.id.tvFri), findViewById(R.id.tvSat),
                findViewById(R.id.tvSun)
        };

        for (int i = 0; i < dayViews.length; i++) {
            final int idx = i;
            // Staggered wejście day chips
            Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
            scaleIn.setStartOffset(100 + idx * 40L);
            dayViews[i].startAnimation(scaleIn);

            dayViews[i].setOnClickListener(v -> toggleDay(idx));
        }

        // Animacja przycisku save
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(400);
        findViewById(R.id.btnSave).startAnimation(slideUp);

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                            .withEndAction(this::saveHabit).start())
                    .start();
        });

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void toggleDay(int idx) {
        selectedDays[idx] = !selectedDays[idx];

        // Bounce animacja przy wyborze
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        dayViews[idx].startAnimation(bounce);

        if (selectedDays[idx]) {
            dayViews[idx].setBackgroundResource(R.drawable.bg_day_selected);
            dayViews[idx].setTextColor(Color.WHITE);
        } else {
            dayViews[idx].setBackgroundResource(R.drawable.bg_day_unselected);
            dayViews[idx].setTextColor(session.isDarkMode()
                    ? Color.parseColor("#9A9A9A")
                    : Color.parseColor("#6B7196"));
        }
    }

    private void saveHabit() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Podaj tytuł nawyku.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> daysList = new ArrayList<>();
        for (int i = 0; i < selectedDays.length; i++) {
            if (selectedDays[i]) daysList.add(i + 1);
        }

        if (daysList.isEmpty()) {
            Toast.makeText(this, "Wybierz przynajmniej jeden dzień tygodnia.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder daysStr = new StringBuilder();
        for (int i = 0; i < daysList.size(); i++) {
            daysStr.append(daysList.get(i));
            if (i < daysList.size() - 1) daysStr.append(",");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Habit habit = new Habit(session.getUserId(), title, desc, daysStr.toString(), today);
        long id = habitDao.insertHabit(habit);

        if (id == -1) {
            Toast.makeText(this, "Błąd podczas zapisywania nawyku.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "✓ Nawyk dodany!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}