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
public final class StorageSection extends OkaeriConfig {

    private StorageType type = StorageType.FILE;
    private String fileName = "cart.json";
    private MySqlSection mysql = new MySqlSection();
}
