package com.example.floodreportingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.floodreportingapp.R;
import com.example.floodreportingapp.model.FloodReportDTO;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private List<FloodReportDTO> reports;

    public ReportAdapter(List<FloodReportDTO> reports) {
        this.reports = reports;
    }

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
