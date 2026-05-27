package com.example.habittracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.example.habittracker.R;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.habittracker.utils.NotificationReceiver;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class SettingsActivity extends AppCompatActivity {

    private SessionManager session;
    private Switch switchDark, switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManager(this);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        TextView tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(session.getEmail());

        switchDark = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        switchDark.setChecked(session.isDarkMode());
        switchNotifications.setChecked(session.isNotificationsEnabled());

        if (session.isDarkMode()) {
            applyDark();
        }

        switchDark.setOnCheckedChangeListener((btn, checked) -> {
            session.setDarkMode(checked);
            recreate();
        });

        switchNotifications.setOnCheckedChangeListener((btn, checked) -> {
            session.setNotifications(checked);
            if (checked) {
                requestNotificationPermission();
                NotificationReceiver.scheduleDaily(this);
            } else {
                NotificationReceiver.cancelScheduled(this);
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            session.clearSession();
            NotificationReceiver.cancelScheduled(this);
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNav();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    private void applyDark() {
        findViewById(R.id.settingsRoot).setBackgroundColor(Color.parseColor("#111111"));
    }

    private void setupBottomNav() {
        View nav = findViewById(R.id.bottomNavSettings);
        nav.findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> { /* already here */ });
    }
}