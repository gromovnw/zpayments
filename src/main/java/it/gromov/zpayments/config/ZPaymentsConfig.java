package it.gromov.zpayments.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import it.gromov.zpayments.config.section.CartSection;
import it.gromov.zpayments.config.section.PollingSection;
import it.gromov.zpayments.config.section.ShopSection;
import it.gromov.zpayments.config.section.StorageSection;
import it.gromov.zpayments.config.section.UpdateSection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public final class ZPaymentsConfig extends OkaeriConfig {

    private int configVersion = 1;
    private boolean debug = false;
    private ShopSection shop = new ShopSection();
    private PollingSection polling = new PollingSection();
    private StorageSection storage = new StorageSection();
    private CartSection cart = new CartSection();
    private UpdateSection update = new UpdateSection();
}
