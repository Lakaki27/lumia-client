package com.lumia;

import org.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class ApiRequest {
    private final static String apiUrlPrefix = "http://localhost:8080/api/v1";

    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean makeTokenVerificationRequest(String tokenToVerify) {
        String apiUrl = apiUrlPrefix.concat("/verify-token");

        String postData = "token=" + tokenToVerify;

        // Create the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Php-Auth-Digest", "Bearer ".concat(TokenManager.loadToken()))
                .POST(HttpRequest.BodyPublishers.ofString(postData, StandardCharsets.UTF_8))
                .build();

        try {
            // Synchronous request (blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.body());
                return jsonObject.getBoolean("isValid");
            } else {
                return false;
            }
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }

    public LoginResponse makeLoginRequest(String email, String password) {
        String apiUrl = apiUrlPrefix.concat("/auth/login");

        Map<String, String> formData = Map.of(
                "email", email,
                "password", password
        );

        // Generate a unique boundary for separating parts of the form
        String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString().substring(0, 8);

        StringBuilder bodyBuilder = new StringBuilder();

        // Add form data as individual parts
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            bodyBuilder.append("--").append(boundary).append("\r\n")
                    .append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n")
                    .append("\r\n")
                    .append(entry.getValue()).append("\r\n");
        }

        // Add closing boundary to signify the end of the body
        bodyBuilder.append("--").append(boundary).append("--\r\n");

        // Get the body content as a string
        String body = bodyBuilder.toString();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Send the request and handle the response
        try {
            // Synchronous request (blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.body());
                return new LoginResponse(true, jsonObject.getString("token"));
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                return new LoginResponse(false, jsonObject.getString("message"));
            }
        } catch (InterruptedException | IOException e) {
            return new LoginResponse(false, "Le serveur est inaccessible pour le moment. Merci de réesayer ultérieurement.");
        }
    }

    public ProductResponse makeGetProductRequest(String barcode) {
        String apiUrl = apiUrlPrefix.concat("/products/".concat(barcode));

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Php-Auth-Digest", "Bearer ".concat(TokenManager.loadToken()))
                .GET()
                .build();

        // Send the request and handle the response
        try {
            // Synchronous request (blocking)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.body());
                return new ProductResponse(true, jsonObject.getString("name"), jsonObject.getDouble("price"), jsonObject.getString("barcode"));
            } else {
                JSONObject jsonObject = new JSONObject(response.body());
                return new ProductResponse(false, "", 0, "");
            }
        } catch (InterruptedException | IOException e) {
            return new ProductResponse(false, "", 0, "");
        }
    }
}
