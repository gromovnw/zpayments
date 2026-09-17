package it.gromov.zpayments.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public final class PurchaseItem {

    private final String title;
    private final int count;
    private final double price;
}
