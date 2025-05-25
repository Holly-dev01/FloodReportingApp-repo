package com.example.floodreportingapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodreportingapp.R;
import com.example.floodreportingapp.model.FloodReportDTO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<FloodReportDTO> reports;
    private SimpleDateFormat dateFormat;

    public ReportAdapter(List<FloodReportDTO> reports) {
        this.reports = reports;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        FloodReportDTO report = reports.get(position);

        holder.tvType.setText(report.getType().toUpperCase());
        holder.tvDescription.setText(report.getDescription());
        holder.tvSeverity.setText("Severity: " + report.getSeverity().toUpperCase());
        holder.tvTimestamp.setText(dateFormat.format(report.getTimestamp()));
        holder.tvLocation.setText(String.format(Locale.getDefault(),
                "Lat: %.6f, Lng: %.6f", report.getLatitude(), report.getLongitude()));

        // Set severity color
        int severityColor = getSeverityColor(report.getSeverity());
        holder.tvSeverity.setTextColor(severityColor);
        holder.cardView.setCardBackgroundColor(getSeverityBackgroundColor(report.getSeverity()));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    private int getSeverityColor(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return Color.RED;
            case "medium":
                return Color.parseColor("#FF8C00"); // Dark orange
            case "low":
            default:
                return Color.parseColor("#228B22"); // Forest green
        }
    }

    private int getSeverityBackgroundColor(String severity) {
        switch (severity.toLowerCase()) {
            case "high":
                return Color.parseColor("#FFEBEE"); // Light red
            case "medium":
                return Color.parseColor("#FFF3E0"); // Light orange
            case "low":
            default:
                return Color.parseColor("#F1F8E9"); // Light green
        }
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvType, tvDescription, tvSeverity, tvTimestamp, tvLocation;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvType = itemView.findViewById(R.id.tvType);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }

    public ReportViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardView);
        tvType = itemView.findViewById(R.id.tvType);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvSeverity = itemView.findViewById(R.id.tvSeverity);
        tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        tvLocation = itemView.findViewById(R.id.tvLocation);
    }
}
