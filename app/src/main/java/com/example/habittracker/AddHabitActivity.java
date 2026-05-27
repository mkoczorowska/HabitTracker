package com.example.habittracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.example.habittracker.R;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class AddHabitActivity extends AppCompatActivity {

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
            dayViews[i].setOnClickListener(v -> toggleDay(idx));
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveHabit());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void toggleDay(int idx) {
        selectedDays[idx] = !selectedDays[idx];
        if (selectedDays[idx]) {
            dayViews[idx].setBackgroundResource(R.drawable.bg_day_selected);
            dayViews[idx].setTextColor(Color.WHITE);
        } else {
            dayViews[idx].setBackgroundResource(R.drawable.bg_day_unselected);
            dayViews[idx].setTextColor(Color.parseColor("#3D3D3D"));
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
            Toast.makeText(this, "Nawyk dodany!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}