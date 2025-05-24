package com.example.floodreportingapp;

import static com.example.floodreportingapp.api.ApiClient.apiService;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.floodreportingapp.model.FloodReportDTO;

import java.util.List;

import retrofit2.Call;

public class ReportListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadReports(String filter) {
        Call<List<FloodReportDTO>> call;
        if (filter.equals("flood")) {
            call = apiService.getReportsByType("flood");
        } else {
            call = apiService.getAllReports();
        }
    }
}