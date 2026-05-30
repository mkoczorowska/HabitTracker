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
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class RegisterActivity extends BaseActivity {

    private EditText etEmail, etPassword, etRepeatPassword;
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new SessionManager(this);
        userDao = new UserDao(DatabaseHelper.getInstance(this));
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);

        // Staggered animacja pól
        int[] offsets = {80, 140, 200};
        EditText[] fields = {etEmail, etPassword, etRepeatPassword};
        for (int i = 0; i < fields.length; i++) {
            Animation a = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            a.setStartOffset(offsets[i]);
            fields[i].startAnimation(a);
        }

        Button btnRegister = findViewById(R.id.btnRegister);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideUp.setStartOffset(260);
        btnRegister.startAnimation(slideUp);

        btnRegister.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                            .withEndAction(this::attemptRegister).start())
                    .start();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        if (TextUtils.isEmpty(email)) { showError("Podaj adres email."); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { showError("Podaj poprawny adres email."); return; }
        if (TextUtils.isEmpty(password)) { showError("Podaj hasło."); return; }
        if (password.length() < 6) { showError("Hasło musi mieć minimum 6 znaków."); return; }
        if (!password.equals(repeatPassword)) { showError("Hasła nie są identyczne."); return; }
        if (userDao.emailExists(email)) { showError("Ten adres email jest już zarejestrowany."); return; }

        long userId = userDao.insertUser(email, password);
        if (userId == -1) { showError("Wystąpił błąd podczas rejestracji. Spróbuj ponownie."); return; }

        session.saveSession((int) userId, email);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}