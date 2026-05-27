package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import com.example.habittracker.R;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.UserDao;
import com.example.habittracker.utils.SessionManager;
import com.example.habittracker.utils.ThemeHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etRepeatPassword;
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ThemeHelper.apply(this, findViewById(android.R.id.content), session.isDarkMode());

        userDao = new UserDao(DatabaseHelper.getInstance(this));
        session = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);

        findViewById(R.id.btnRegister).setOnClickListener(v -> attemptRegister());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();

        // Validation
        if (TextUtils.isEmpty(email)) {
            showError("Podaj adres email.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Podaj poprawny adres email.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("Podaj hasło.");
            return;
        }
        if (password.length() < 6) {
            showError("Hasło musi mieć minimum 6 znaków.");
            return;
        }
        if (!password.equals(repeatPassword)) {
            showError("Hasła nie są identyczne.");
            return;
        }
        if (userDao.emailExists(email)) {
            showError("Ten adres email jest już zarejestrowany.");
            return;
        }

        long userId = userDao.insertUser(email, password);
        if (userId == -1) {
            showError("Wystąpił błąd podczas rejestracji. Spróbuj ponownie.");
            return;
        }

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