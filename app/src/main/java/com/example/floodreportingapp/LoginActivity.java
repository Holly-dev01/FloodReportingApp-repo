package com.example.floodreportingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.example.floodreportingapp.model.AuthRequestDTO;
import com.example.floodreportingapp.model.AuthResponseDTO;
import com.example.floodreportingapp.utils.SharedPreferencesHelper;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin,btnBiometricLogin;
    private SharedPreferencesHelper prefsHelper;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeServices();
        setupBiometric();
        setupClickListeners();

        // Check if already logged in
        if (prefsHelper.isLoggedIn()) {
            navigateToMain();
        }
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBiometricLogin = findViewById(R.id.btnBiometricLogin);
    }

    private void initializeServices() {
        prefsHelper = new SharedPreferencesHelper(this);
    }

    private void setupBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                setupBiometricPrompt();
                btnBiometricLogin.setEnabled(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                btnBiometricLogin.setEnabled(false);
                btnBiometricLogin.setText("Biometric not available");
                break;
        }
    }

    private void setupBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                // Check if biometric credentials are saved
                if (prefsHelper.hasSavedCredentials()) {
                    String savedUsername = prefsHelper.getSavedUsername();
                    String savedPassword = prefsHelper.getSavedPassword();
                    authenticateWithCredentials(savedUsername, savedPassword, true);
                } else {
                    Toast.makeText(LoginActivity.this, "Please login with username/password first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use your fingerprint to login")
                .setNegativeButtonText("Cancel")
                .build();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        btnBiometricLogin.setOnClickListener(v -> authenticateWithBiometric());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return;
        }

        authenticateWithCredentials(username, password, false););
    }

    private void authenticateWithCredentials(String username, String password, boolean isBiometric) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        AuthRequestDTO authRequest = new AuthRequestDTO(username, password);
        Call<AuthResponseDTO> call = apiService.login(authRequest);

        call.enqueue(new Callback<AuthResponseDTO>() {
            @Override
            public void onResponse(Call<AuthResponseDTO> call, Response<AuthResponseDTO> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponseDTO authResponse = response.body();

                    if (authResponse.isSuccess()) {
                        // Save login state and token
                        prefsHelper.saveLoginState(true);
                        prefsHelper.saveAuthToken(authResponse.getToken());

                        // Save credentials for biometric authentication if not biometric login
                        if (!isBiometric) {
                            prefsHelper.saveCredentials(username, password);
                        }

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDTO> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateWithBiometric() {
        if (biometricPrompt != null && promptInfo != null) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}