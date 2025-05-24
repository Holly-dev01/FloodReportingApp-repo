package com.example.floodreportingapp;

import static androidx.core.content.ContextCompat.startActivity;
import android.content.Intent;
import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.example.floodreportingapp.model.FloodReportDTO;
import com.example.floodreportingapp.utils.SharedPreferencesHelper;
import com.example.floodreportingapp.utils.NotificationHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private EditText etDescription;
    private Spinner spinnerType, spinnerSeverity;
    private Button btnSubmitReport;

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferencesHelper prefsHelper;
    private ApiService apiService;

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
        checkAuthentication();
    }

    private void initializeViews() {
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerSeverity = findViewById(R.id.spinnerSeverity);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
    }

    private void initializeServices() {
        prefsHelper = new SharedPreferencesHelper(this);
        apiService = ApiClient.getApiService();
    }

private void requestLocationPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    } else {
        getCurrentLocation();
    }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                       @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission required", Toast.LENGTH_LONG).show();
        }
    }
}

private void getCurrentLocation() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
        return;
    }

    fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                }
            });
    }

    private void submitReport() {
        String description = etDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString().toLowerCase();
        String severity = spinnerSeverity.getSelectedItem().toString().toLowerCase();

        if (description.isEmpty()) {
            etDescription.setError("Description is required");
            return;
        }

        if (currentLatitude == 0.0 && currentLongitude == 0.0) {
            Toast.makeText(this, "Location not available. Please try again.", Toast.LENGTH_SHORT).show();
            getCurrentLocation();
            return;
        }
        // Create report DTO
        FloodReportDTO reportDTO = new FloodReportDTO();
        reportDTO.setType(type);
        reportDTO.setDescription(description);
        reportDTO.setLatitude(currentLatitude);
        reportDTO.setLongitude(currentLongitude);
        reportDTO.setDeviceId(prefsHelper.getDeviceId());
        reportDTO.setSeverity(severity);

        // Submit report
        btnSubmitReport.setEnabled(false);
        btnSubmitReport.setText("Submitting...");

        Call<FloodReportDTO> call = apiService.createReport(reportDTO);
        call.enqueue(new Callback<FloodReportDTO>() {
            @Override
            public void onResponse(Call<FloodReportDTO> call, Response<FloodReportDTO> response) {
                btnSubmitReport.setEnabled(true);
                btnSubmitReport.setText("Submit Report");

                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
                }

                call.enqueue(new Callback<FloodReportDTO>() {
                    @Override
                    public void onResponse(Call<FloodReportDTO> call, Response<FloodReportDTO> response) {
                        notificationHelper.sendReportNotification(
                                "Report Submitted",
                                "Your " + type + " report has been received."
                        );
                    }
                    // ...
                });
            }
            }

            @Override
            public void onFailure(Call<FloodReportDTO> call, Throwable t) {
                btnSubmitReport.setEnabled(true);
                btnSubmitReport.setText("Submit Report");
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAuthentication() {
       if (!prefsHelper.isLoggedIn()) {
         Intent intent = new Intent(this, LoginActivity.class);
         startActivity(intent);
         finish();
       }
    }


    private void clearForm() {
        etDescription.setText("");
        spinnerType.setSelection(0);
        spinnerSeverity.setSelection(0);
    }
}