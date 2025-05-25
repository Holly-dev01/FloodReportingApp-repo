package com.example.floodreportingapp;

import static com.example.floodreportingapp.api.ApiClient.apiService;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.floodreportingapp.adapter.ReportAdapter;
import com.example.floodreportingapp.api.ApiClient;
import com.example.floodreportingapp.api.ApiService;
import com.example.floodreportingapp.model.FloodReportDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReportAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private List<FloodReportDTO> reportList;

    private String currentFilter = "all"; // all, flood, blocked_road, low, medium, high

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        initializeViews();
        initializeServices();
        setupRecyclerView();
        setupSwipeRefresh();
        loadReports();

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Flood Reports");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void initializeServices() {
        apiService = ApiClient.getApiService();
        reportList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new ReportAdapter(reportList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadReports);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void loadReports() {
        swipeRefreshLayout.setRefreshing(true);

        Call<List<FloodReportDTO>> call;
        switch (currentFilter) {
            case "flood":
                call = apiService.getReportsByType("flood");
                break;
            case "blocked_road":
                call = apiService.getReportsByType("blocked_road");
                break;
            case "low":
                call = apiService.getReportsBySeverity("low");
                break;
            case "medium":
                call = apiService.getReportsBySeverity("medium");
                break;
            case "high":
                call = apiService.getReportsBySeverity("high");
                break;
            default:
                call = apiService.getAllReports();
                break;
        }

        call.enqueue(new Callback<List<FloodReportDTO>>() {
            @Override
            public void onResponse(Call<List<FloodReportDTO>> call, Response<List<FloodReportDTO>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    reportList.clear();
                    reportList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (reportList.isEmpty()) {
                        Toast.makeText(ReportListActivity.this, "No reports found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportListActivity.this, "Failed to load reports", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FloodReportDTO>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ReportListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // Handle filter options
        switch (id) {
            case R.id.filter_all:
                currentFilter = "all";
                break;
            case R.id.filter_flood:
                currentFilter = "flood";
                break;
            case R.id.filter_blocked_road:
                currentFilter = "blocked_road";
                break;
            case R.id.filter_low:
                currentFilter = "low";
                break;
            case R.id.filter_medium:
                currentFilter = "medium";
                break;
            case R.id.filter_high:
                currentFilter = "high";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        loadReports();
        return true;
    }
}