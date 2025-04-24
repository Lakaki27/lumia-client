package com.lumia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TokenManager {
    private static final String TOKEN_PATH = "token.properties";

    public static String loadToken() {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(TOKEN_PATH)) {
            properties.load(fis);
            return properties.getProperty("JWT");
        } catch (IOException e) {
            return "";
        }
    }

    public static void saveToken(String token) {
        Properties properties = new Properties();
        properties.setProperty("JWT", token);
        try (FileOutputStream fos = new FileOutputStream(TOKEN_PATH)) {
            properties.store(fos, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyToken() {
        ApiRequest req = new ApiRequest();

        String currentToken = loadToken();

        if (currentToken.isEmpty()) {
            return false;
        }

        return ApiRequest.makeTokenVerificationRequest(currentToken);
    }

    public static String getSystemSerial() {
        return "1423456974855896";
    }
}
