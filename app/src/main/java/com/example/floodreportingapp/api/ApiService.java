package com.example.floodreportingapp.api;

import com.example.floodreportingapp.model.AuthRequestDTO;
import com.example.floodreportingapp.model.AuthResponseDTO;
import com.example.floodreportingapp.model.FloodReportDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/auth/login")
    Call<AuthResponseDTO> login(@Body AuthRequestDTO authRequest);

    @POST("api/reports")
    Call<FloodReportDTO> createReport(@Body FloodReportDTO reportDTO);

    @GET("api/reports/all")
    Call<List<FloodReportDTO>> getAllReports();

    @GET("api/reports/type/{type}")
    Call<List<FloodReportDTO>> getReportsByType(@Path("type") String type);

    @GET("api/reports/severity/{severity}")
    Call<List<FloodReportDTO>> getReportsBySeverity(@Path("severity") String severity);
}