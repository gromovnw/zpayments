package it.gromov.zpayments.http.dto;

import lombok.Getter;

@Getter
public final class AckResponseDto {

    private boolean ok;
    private String orderId;
    private String status;
}
