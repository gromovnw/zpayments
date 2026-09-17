package it.gromov.zpayments.http.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public final class PendingOrderDto {

    private String orderId;
    private String nickname;
    private double total;
    private List<PendingItemDto> items;
    private List<PendingTaskDto> tasks;

    public List<PendingItemDto> getItems() {
        return items == null ? Collections.emptyList() : items;
    }

    public List<PendingTaskDto> getTasks() {
        return tasks == null ? Collections.emptyList() : tasks;
    }
}
