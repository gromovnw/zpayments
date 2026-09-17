package it.gromov.zpayments.service.impl;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import it.gromov.zpayments.config.MenuConfig;
import it.gromov.zpayments.config.MessagesConfig;
import it.gromov.zpayments.config.ZPaymentsConfig;
import it.gromov.zpayments.service.ConfigService;
import org.bukkit.plugin.Plugin;

import java.io.File;

public final class ConfigServiceImpl implements ConfigService {

    private final Plugin plugin;

    private ZPaymentsConfig config;
    private MessagesConfig messages;
    private MenuConfig menu;

    public ConfigServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        reload();
    }

    @Override
    public void reload() {
        this.config = load(ZPaymentsConfig.class, "config.yml");
        this.messages = load(MessagesConfig.class, "messages.yml");
        this.menu = load(MenuConfig.class, "menu.yml");
    }

    @Override
    public void disable() {
    }

    @Override
    public ZPaymentsConfig getConfig() {
        return config;
    }

    @Override
    public MessagesConfig getMessages() {
        return messages;
    }

    @Override
    public MenuConfig getMenu() {
        return menu;
    }

    private <T extends eu.okaeri.configs.OkaeriConfig> T load(Class<T> type, String fileName) {
        return ConfigManager.create(type, it -> {
            it.configure(options -> {
                options.configurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                options.bindFile(new File(plugin.getDataFolder(), fileName));
                options.removeOrphans(true);
            });
            it.saveDefaults();
            it.load(true);
        });
    }
}
