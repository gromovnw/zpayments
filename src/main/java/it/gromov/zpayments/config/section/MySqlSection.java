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
public final class MySqlSection extends OkaeriConfig {

    private String host = "127.0.0.1";
    private int port = 3306;
    private String database = "zpayments";
    private String username = "root";
    private String password = "";
    private boolean useSsl = false;
    private String tablePrefix = "zpayments_";
}
