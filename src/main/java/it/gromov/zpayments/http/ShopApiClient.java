package it.gromov.zpayments.http;

import com.google.gson.Gson;
import it.gromov.zpayments.config.section.ShopSection;
import it.gromov.zpayments.http.dto.AckRequestDto;
import it.gromov.zpayments.http.dto.AckResponseDto;
import it.gromov.zpayments.http.dto.PendingPurchasesResponse;
import it.gromov.zpayments.service.ConfigService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class ShopApiClient {

    private static final String API_BASE_URL = "https://api.zdonate.me";

    private final Gson gson = new Gson();
    private final ConfigService configService;

    public ShopApiClient(ConfigService configService) {
        this.configService = configService;
    }

    public PendingPurchasesResponse fetchPending() throws IOException, ShopApiException {
        HttpURLConnection connection = openConnection("GET", "/api/plugin/purchases/pending");
        return execute(connection, null, PendingPurchasesResponse.class);
    }

    public AckResponseDto acknowledge(String orderId, String key, boolean success, String output) throws IOException, ShopApiException {
        HttpURLConnection connection = openConnection("POST", "/api/plugin/purchases/" + orderId + "/ack");
        connection.setDoOutput(true);
        String body = gson.toJson(new AckRequestDto(key, success, output));
        return execute(connection, body, AckResponseDto.class);
    }

    private HttpURLConnection openConnection(String method, String path) throws IOException {
        ShopSection shopSection = configService.getConfig().getShop();
        int timeoutMillis = configService.getConfig().getPolling().getSafeRequestTimeoutSeconds() * 1000;

        URL url = new URL(API_BASE_URL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(timeoutMillis);
        connection.setReadTimeout(timeoutMillis);
        connection.setRequestProperty("X-Shop-Id", shopSection.getShopId());
        connection.setRequestProperty("X-Server-Id", shopSection.getServerId());
        connection.setRequestProperty("X-Plugin-Key", shopSection.getPluginKey());
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "zPayments-Plugin");
        return connection;
    }

    private <T> T execute(HttpURLConnection connection, String body, Class<T> responseType) throws IOException, ShopApiException {
        try {
            if (body != null) {
                connection.setDoOutput(true);
                byte[] payload = body.getBytes(StandardCharsets.UTF_8);
                connection.setFixedLengthStreamingMode(payload.length);
                try (OutputStream stream = connection.getOutputStream()) {
                    stream.write(payload);
                }
            }

            int status = connection.getResponseCode();
            boolean isSuccess = status >= 200 && status < 300;
            String responseBody = readBody(isSuccess ? connection.getInputStream() : connection.getErrorStream());

            if (!isSuccess) {
                throw new ShopApiException(status, responseBody);
            }
            if (responseBody.isEmpty()) {
                return null;
            }
            return gson.fromJson(responseBody, responseType);
        } finally {
            connection.disconnect();
        }
    }

    private String readBody(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
