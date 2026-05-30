package com.example.habittracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Switch;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import com.example.habittracker.utils.NotificationReceiver;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class SettingsActivity extends BaseActivity {

    private SessionManager session;
    private Switch switchDark, switchNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManager(this);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        // Ustaw email i awatar (pierwsza litera emaila)
        String email = session.getEmail();
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvAvatarLetter = findViewById(R.id.tvAvatarLetter);
        tvEmail.setText(email);
        if (email != null && !email.isEmpty()) {
            tvAvatarLetter.setText(String.valueOf(email.charAt(0)).toUpperCase());
        }

        // Animacja awatara
        Animation scaleIn = AnimationUtils.loadAnimation(this, R.anim.scale_in);
        tvAvatarLetter.startAnimation(scaleIn);

        // Animacja treści
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(100);
        findViewById(R.id.btnLogout).startAnimation(slideUp);

        switchDark = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);

        switchDark.setChecked(session.isDarkMode());
        switchNotifications.setChecked(session.isNotificationsEnabled());

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
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                    .withEndAction(() -> {
                        session.clearSession();
                        NotificationReceiver.cancelScheduled(this);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }).start();
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

    private void setupBottomNav() {
        View nav = findViewById(R.id.bottomNavSettings);
        nav.findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
        nav.findViewById(R.id.navStatistics).setOnClickListener(v -> {
            startActivity(new Intent(this, StatisticsActivity.class));
            finish();
        });
        nav.findViewById(R.id.navSettings).setOnClickListener(v -> { /* już tu */ });
    }
}