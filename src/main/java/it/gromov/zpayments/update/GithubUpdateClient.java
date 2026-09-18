package it.gromov.zpayments.update;

import com.google.gson.Gson;
import it.gromov.zpayments.update.dto.GithubReleaseDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class GithubUpdateClient {

    private static final String LATEST_RELEASE_URL = "https://api.github.com/repos/gromovnw/zpayments/releases/latest";
    private static final String JAR_ASSET_NAME = "zPayments.jar";
    private static final int TIMEOUT_MILLIS = 10000;

    private final Gson gson = new Gson();

    public GithubReleaseDto fetchLatestRelease() throws GithubUpdateException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(LATEST_RELEASE_URL).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", "zPayments-Plugin");

        try {
            int status = connection.getResponseCode();
            String body = readBody(status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream());
            if (status != 200) {
                throw new GithubUpdateException("HTTP " + status + " при запросе последнего релиза: " + trimmed(body));
            }
            GithubReleaseDto release = gson.fromJson(body, GithubReleaseDto.class);
            if (release == null || release.getTagName() == null) {
                throw new GithubUpdateException("Пустой ответ GitHub API");
            }
            return release;
        } finally {
            connection.disconnect();
        }
    }

    public String findJarDownloadUrl(GithubReleaseDto release) throws GithubUpdateException {
        return release.getAssets().stream()
                .filter(asset -> JAR_ASSET_NAME.equals(asset.getName()))
                .map(asset -> asset.getBrowserDownloadUrl())
                .findFirst()
                .orElseThrow(() -> new GithubUpdateException("В релизе " + release.getTagName() + " не найден " + JAR_ASSET_NAME));
    }

    public byte[] downloadJar(String url) throws GithubUpdateException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "zPayments-Plugin");

        try {
            int status = connection.getResponseCode();
            if (status != 200) {
                throw new GithubUpdateException("HTTP " + status + " при скачивании джарника");
            }
            return readBytes(connection.getInputStream());
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

    private byte[] readBytes(InputStream stream) throws IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int read;
        while ((read = stream.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    private String trimmed(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 300 ? body.substring(0, 300) + "..." : body;
    }
}
