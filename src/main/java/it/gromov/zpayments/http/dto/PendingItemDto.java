package it.gromov.zpayments.http.dto;

import lombok.Getter;

@Getter
public final class PendingItemDto {

    private String title;
    private int count;
    private double price;
}
