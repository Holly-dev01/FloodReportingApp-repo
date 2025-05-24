package com.example.floodreportingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.floodreportingapp.utils.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeServices();

        if (prefsHelper.isLoggedIn()) {
            navigateToMain();
        }
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void initializeServices() {
        prefsHelper = new SharedPreferencesHelper(this);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}