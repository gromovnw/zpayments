package it.gromov.zpayments.config.section;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public final class PollingSection extends OkaeriConfig {

    private int requestDelaySeconds = 20;
    private int requestTimeoutSeconds = 5;
    private int ackRetryLimit = 3;

    public int getSafeRequestDelaySeconds() {
        return Math.max(5, requestDelaySeconds);
    }

    public int getSafeRequestTimeoutSeconds() {
        return Math.max(2, requestTimeoutSeconds);
    }
}
