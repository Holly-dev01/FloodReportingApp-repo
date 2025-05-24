package com.example.floodreportingapp.model;

public class AuthResponseDTO {
    private boolean success;
    private String token;
    private String message;

    public AuthResponseDTO() {}

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}