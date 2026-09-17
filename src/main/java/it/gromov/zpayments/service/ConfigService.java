package it.gromov.zpayments.service;

import it.gromov.zpayments.config.MenuConfig;
import it.gromov.zpayments.config.MessagesConfig;
import it.gromov.zpayments.config.ZPaymentsConfig;

public interface ConfigService extends Service {

    ZPaymentsConfig getConfig();

    MessagesConfig getMessages();

    MenuConfig getMenu();
}
