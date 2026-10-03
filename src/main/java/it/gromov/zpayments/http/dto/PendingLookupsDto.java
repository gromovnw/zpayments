package it.gromov.zpayments.http.dto;

import java.util.Collections;
import java.util.List;

public final class PendingLookupsDto {

    private List<LookupDto> lookups;

    public List<LookupDto> getLookups() {
        return lookups == null ? Collections.emptyList() : lookups;
    }
}
