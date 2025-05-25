package com.example.floodreportingapp;

import android.graphics.Color;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.floodreportingapp.model.FloodReportDTO;
import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback  {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Initialize API service
        apiService = ApiClient.getApiService();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable location if permission is granted
        enableMyLocation();

        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Load and display flood reports
        loadFloodReports();

        // Set default location (you can change this to your preferred location)
        LatLng defaultLocation = new LatLng(33.5731, -7.5898); // Casablanca, Morocco
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    // MapsActivity.java
    private void loadFloodReports() {
        Call<List<FloodReportDTO>> call = apiService.getAllReports();
        call.enqueue(new Callback<List<FloodReportDTO>>() {
            @Override
            public void onResponse(Call<List<FloodReportDTO>> call, Response<List<FloodReportDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayReportsOnMap(response.body());
                } else {
                    Toast.makeText(MapsActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FloodReportDTO>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayReportsOnMap(List<FloodReportDTO> reports) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

        for (FloodReportDTO report : reports) {
            LatLng position = new LatLng(report.getLatitude(), report.getLongitude());

            // Choose marker color based on severity
            BitmapDescriptor markerColor = getMarkerColorBySeverity(report.getSeverity());

            // Create marker title and snippet
            String title = report.getType().toUpperCase() + " - " + report.getSeverity().toUpperCase();
            String snippet = report.getDescription() + "\n" +
                    "Reported: " + dateFormat.format(report.getTimestamp());

            // Add marker
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(title)
                    .snippet(snippet)
                    .icon(markerColor));

            // Add circle to show affected area based on severity
            int radius = getRadiusBySeverity(report.getSeverity());
            int fillColor = getFillColorBySeverity(report.getSeverity());

            mMap.addCircle(new CircleOptions()
                    .center(position)
                    .radius(radius)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2)
                    .fillColor(fillColor));
        }
    }

    private BitmapDescriptor getMarkerColor(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            case "medium":
                return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            case "low":
                default:
                    return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
    }

    private int getRadiusBySeverity(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return 1000; // 1 km
            case "medium":
                return 500;  // 500 m
            case "low":
            default:
                return 200;  // 200 m
        }
    }

    private int getFillColorBySeverity(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return Color.argb(50, 255, 0, 0); // Semi-transparent red
            case "medium":
                return Color.argb(50, 255, 165, 0); // Semi-transparent orange
            case "low":
            default:
                return Color.argb(50, 255, 255, 0); // Semi-transparent yellow
        }
    }
}