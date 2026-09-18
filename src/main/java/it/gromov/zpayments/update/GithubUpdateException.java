package it.gromov.zpayments.update;

public final class GithubUpdateException extends Exception {

    public GithubUpdateException(String message) {
        super(message);
    }

    public GithubUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
