package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.UserDao;
import com.example.habittracker.models.User;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);
        userDao = new UserDao(DatabaseHelper.getInstance(this));
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Animacja formularza
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(100);
        etEmail.startAnimation(slideUp);
        etPassword.startAnimation(slideUp);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnBack = findViewById(R.id.btnBack);

        Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp2.setStartOffset(200);
        btnLogin.startAnimation(slideUp2);

        btnLogin.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                            .withEndAction(this::attemptLogin).start())
                    .start();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) { showError("Podaj adres email."); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { showError("Podaj poprawny adres email."); return; }
        if (TextUtils.isEmpty(password)) { showError("Podaj hasło."); return; }
        if (password.length() < 6) { showError("Hasło musi mieć minimum 6 znaków."); return; }

        User user = userDao.getUserByEmail(email);
        if (user == null) { showError("Nie znaleziono konta z tym adresem email."); return; }
        if (!user.getPassword().equals(password)) { showError("Nieprawidłowe hasło."); return; }

        session.saveSession(user.getId(), user.getEmail());
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}