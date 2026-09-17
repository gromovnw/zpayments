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
public final class ShopSection extends OkaeriConfig {

    private String shopId = "YOUR-SHOP-ID";
    private String serverId = "YOUR-SERVER-ID";
    private String pluginKey = "YOUR-PLUGIN-KEY";
}
