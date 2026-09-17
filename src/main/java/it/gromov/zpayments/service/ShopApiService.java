package it.gromov.zpayments.service;

public interface ShopApiService extends Service {

    void pollNow();

    long getLastPollTimestamp();

    boolean isLastPollSuccessful();

    String getLastErrorMessage();

    boolean testConnection();
}
