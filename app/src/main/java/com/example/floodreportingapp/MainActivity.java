package com.example.floodreportingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.example.floodreportingapp.model.FloodReportDTO;
import com.example.floodreportingapp.utils.LocationHelper;
import com.example.floodreportingapp.utils.NotificationHelper;
import com.example.floodreportingapp.utils.SharedPreferencesHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.example.floodreportingapp.model.FloodReportDTO;
import com.example.floodreportingapp.utils.LocationHelper;
import com.example.floodreportingapp.utils.NotificationHelper;
import com.example.floodreportingapp.utils.SharedPreferencesHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText etDescription;
    private Spinner spinnerType, spinnerSeverity;
    private Button btnSubmitReport, btnViewMap, btnViewReports;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationHelper locationHelper;
    private NotificationHelper notificationHelper;
    private SharedPreferencesHelper prefsHelper;
    private ApiService apiService;

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeServices();
        checkAuthentication();
        setupSpinners();
        setupClickListeners();
        requestLocationPermission();

        // Create notification channel
        notificationHelper.createNotificationChannel();
    }

    private void initializeViews() {
        etDescription = findViewById(R.id.etDescription);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerSeverity = findViewById(R.id.spinnerSeverity);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);
        btnViewMap = findViewById(R.id.btnViewMap);
        btnViewReports = findViewById(R.id.btnViewReports);
    }

    private void initializeServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationHelper = new LocationHelper(this);
        notificationHelper = new NotificationHelper(this);
        prefsHelper = new SharedPreferencesHelper(this);
        apiService = ApiClient.getApiService();
    }

    private void checkAuthentication() {
        if (!prefsHelper.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupSpinners() {
        // Type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.report_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Severity spinner
        ArrayAdapter<CharSequence> severityAdapter = ArrayAdapter.createFromResource(
                this, R.array.severity_levels, android.R.layout.simple_spinner_item);
        severityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeverity.setAdapter(severityAdapter);
    }

    private void setupClickListeners() {
        btnSubmitReport.setOnClickListener(v -> submitReport());
        btnViewMap.setOnClickListener(v -> openMapsActivity());
        btnViewReports.setOnClickListener(v -> openReportsActivity());
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
                Toast.makeText(this, "Location permission required for reporting", Toast.LENGTH_LONG).show();
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

                    // Send notification
                    notificationHelper.sendReportNotification("Report Submitted",
                            "Your " + type + " report has been submitted successfully.");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to submit report", Toast.LENGTH_SHORT).show();
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

    private void clearForm() {
        etDescription.setText("");
        spinnerType.setSelection(0);
        spinnerSeverity.setSelection(0);
    }

    private void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void openReportsActivity() {
        Intent intent = new Intent(this, ReportListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        prefsHelper.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}