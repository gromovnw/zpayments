package it.gromov.zpayments.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// Имена полей — ровно как в JSON контракта (см. CLAUDE.md, раздел «Контракт»).
@Getter
@AllArgsConstructor
public final class PlayerGroupsReportDto {

    private final String nickname;
    private final String uuid;
    private final List<String> groups;
}
