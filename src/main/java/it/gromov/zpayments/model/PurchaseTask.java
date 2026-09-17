package it.gromov.zpayments.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public final class PurchaseTask {

    private final String orderId;
    private final String nickname;
    private final double total;
    private final List<PurchaseItem> items;
    private final String key;
    private final String command;
}
