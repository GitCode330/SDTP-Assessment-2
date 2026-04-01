package com.example.softwaretesting.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.softwaretesting.model.Enrollment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // F1: Get enrollments for a specific student
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        try {
            String url = BASE_URL + "/students/" + studentId + "/enrollments";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<Enrollment>>() {});
            } else if (response.statusCode() == 204) {
                return new ArrayList<>(); // No content
            } else {
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("Error fetching enrollments: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Helper method to check if backend is running
    public boolean isBackendRunning() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/students/active"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;

        } catch (Exception e) {
            return false;
        }
    }
}