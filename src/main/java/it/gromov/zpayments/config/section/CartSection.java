package it.gromov.zpayments.config.section;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public final class CartSection extends OkaeriConfig {

    private boolean enabled = true;
    private boolean onlyForOffline = true;
    private boolean claimOnJoin = true;
    private int openCooldownSeconds = 3;
    private List<String> aliases = new ArrayList<>(java.util.Arrays.asList("shop", "donate", "buy"));
}
