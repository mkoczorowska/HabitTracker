package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import com.example.habittracker.R;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.database.DatabaseHelper;
import com.example.habittracker.database.UserDao;
import com.example.habittracker.models.User;
import com.example.habittracker.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private UserDao userDao;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = new UserDao(DatabaseHelper.getInstance(this));
        session = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

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

        User user = userDao.getUserByEmail(email);
        if (user == null) {
            showError("Nie znaleziono konta z tym adresem email.");
            return;
        }
        if (!user.getPassword().equals(password)) {
            showError("Nieprawidłowe hasło.");
            return;
        }

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