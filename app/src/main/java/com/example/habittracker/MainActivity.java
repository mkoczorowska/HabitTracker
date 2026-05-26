package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import com.example.habittracker.R;
import androidx.appcompat.app.AppCompatActivity;
import com.example.habittracker.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        applyTheme(session.isDarkMode());
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogin).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.btnRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void applyTheme(boolean dark) {
        if (dark) {
            getWindow().getDecorView().setBackgroundColor(getColor(R.color.dark_bg));
        }
    }
}