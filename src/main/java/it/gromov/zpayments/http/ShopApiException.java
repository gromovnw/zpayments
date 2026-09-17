package it.gromov.zpayments.http;

import lombok.Getter;

@Getter
public final class ShopApiException extends Exception {

    private final int statusCode;
    private final String responseBody;

    public ShopApiException(int statusCode, String responseBody) {
        super("HTTP " + statusCode + ": " + trimmed(responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public ShopApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = "";
    }

    private static String trimmed(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 300 ? body.substring(0, 300) + "..." : body;
    }
}
