package it.gromov.zpayments.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public final class LookupAnswerDto {

    private final List<String> groups;
    private final String error;
}
