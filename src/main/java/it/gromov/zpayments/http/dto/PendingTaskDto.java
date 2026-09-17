package it.gromov.zpayments.http.dto;

import lombok.Getter;

@Getter
public final class PendingTaskDto {

    private String key;
    private String command;
}
