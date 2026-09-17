package it.gromov.zpayments.http.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public final class PendingPurchasesResponse {

    private List<PendingOrderDto> orders;

    public List<PendingOrderDto> getOrders() {
        return orders == null ? Collections.emptyList() : orders;
    }
}
