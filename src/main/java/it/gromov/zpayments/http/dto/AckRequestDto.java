package it.gromov.zpayments.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class AckRequestDto {

    private final String key;
    private final boolean success;
    private final String output;
}
