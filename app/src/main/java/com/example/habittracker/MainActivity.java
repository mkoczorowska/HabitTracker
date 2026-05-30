package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.UserDao;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BaseActivity.onCreate ustawia motyw i wywołuje super — nie rób tego ponownie
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (session.isLoggedIn()) {
            UserDao userDao = new UserDao(DatabaseHelper.getInstance(this));
            boolean userExists = userDao.getUserByEmail(session.getEmail()) != null;
            if (userExists) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return;
            } else {
                session.clearSession();
            }
        }

        setContentView(R.layout.activity_main);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        View root = findViewById(R.id.mainRoot);
        root.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        View btnLogin = findViewById(R.id.btnLogin);
        View btnRegister = findViewById(R.id.btnRegister);

        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(150);
        btnLogin.startAnimation(slideUp);

        Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp2.setStartOffset(220);
        btnRegister.startAnimation(slideUp2);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}