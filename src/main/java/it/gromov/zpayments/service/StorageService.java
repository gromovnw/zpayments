package it.gromov.zpayments.service;

import it.gromov.zpayments.storage.entity.CartEntry;

import java.util.List;

public interface StorageService extends Service {

    void addEntry(CartEntry entry);

    boolean hasEntry(String orderId, String taskKey);

    List<CartEntry> findByNickname(String nickname);

    void removeEntry(CartEntry entry);

    void removeEntries(List<CartEntry> entries);

    boolean isReady();
}
