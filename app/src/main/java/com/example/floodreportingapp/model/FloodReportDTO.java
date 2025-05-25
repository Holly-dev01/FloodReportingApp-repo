package com.example.floodreportingapp.model;

import java.util.Date;

public class FloodReportDTO {
    private String id;
    private String type;
    private String description;
    private double latitude;
    private double longitude;
    private Date timestamp;
    private String deviceId;
    private String severity;

    public FloodReportDTO() {}

    public FloodReportDTO(String id, String type, String description, double latitude,
                          double longitude, Date timestamp, String deviceId, String severity) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.severity = severity;
    }


    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}