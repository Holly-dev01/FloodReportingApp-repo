package com.example.floodreportingapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.floodreportingapp.model.FloodReportDTO;
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

import retrofit2.Call;
import retrofit2.Callback;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    // MapsActivity.java
    private void loadFloodReports() {
        Call<List<FloodReportDTO>> call = apiService.getAllReports();
        call.enqueue(new Callback<List<FloodReportDTO>>() {
            @Override
            public void onResponse(Call<List<FloodReportDTO>> call, Response<List<FloodReportDTO>> response) {
                for (FloodReportDTO report : response.body()) {
                    LatLng position = new LatLng(report.getLatitude(), report.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(position).title(report.getType()));
                }
            }
        });
    }

    private BitmapDescriptor getMarkerColor(String severity) {
        switch (severity.toLowerCase()) {
            case "high": return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            case "medium": return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            default: return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        }
    }

    private void addSeverityCircle(LatLng position, String severity) {
        int radius = severity.equals("high") ? 1000 : 500;
        int color = severity.equals("high") ? Color.RED : Color.YELLOW;

        mMap.addCircle(new CircleOptions()
                .center(position)
                .radius(radius)
                .strokeColor(color)
                .fillColor(Color.argb(70, Color.red(color), Color.green(color), Color.blue(color))));
    }
}