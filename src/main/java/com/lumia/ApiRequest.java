package com.lumia;

import org.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
                .header("Client-Serial", TokenManager.getSystemSerial())
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

        String serial = "1423456974855896";

        Map<String, String> formData = Map.of(
                "serial", serial,
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

        bodyBuilder.append("--").append(boundary).append("--\r\n");

        String body = bodyBuilder.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .header("Client-Serial", TokenManager.getSystemSerial())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Php-Auth-Digest", "Bearer ".concat(TokenManager.loadToken()))
                .header("Client-Serial", TokenManager.getSystemSerial())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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

    public boolean confirmBasket(List<Map<String, String>> products, boolean isAcquired) {
        String isSoldOrAcquiredUrl = "sell";

        if (isAcquired) {
            isSoldOrAcquiredUrl = "acquire";
        }

        String apiUrl = apiUrlPrefix.concat("/products/".concat(isSoldOrAcquiredUrl));

        JSONArray jsonArray = new JSONArray();
        for (Map<String, String> product : products) {
            JSONObject jsonObject = new JSONObject(product);
            jsonArray.put(jsonObject);
        }

        JSONObject requestBody = new JSONObject();
        requestBody.put("products", jsonArray);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Php-Auth-Digest", "Bearer ".concat(TokenManager.loadToken()))
                .header("Client-Serial", TokenManager.getSystemSerial())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.body());
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
