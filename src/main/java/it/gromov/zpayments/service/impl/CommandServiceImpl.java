package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.command.CartCommand;
import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.command.ZPaymentsCommand;
import it.gromov.zpayments.command.sub.HelpSubCommand;
import it.gromov.zpayments.command.sub.ReloadSubCommand;
import it.gromov.zpayments.command.sub.SetupSubCommand;
import it.gromov.zpayments.command.sub.StatusSubCommand;
import it.gromov.zpayments.command.sub.TestConnectionSubCommand;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.CommandService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MenuService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.service.ShopApiService;
import it.gromov.zpayments.util.CommandMapUtil;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public final class CommandServiceImpl implements CommandService {

    private final Plugin plugin;
    private final ConfigService configService;
    private final MessageService messageService;
    private final CartService cartService;
    private final MenuService menuService;
    private final ShopApiService shopApiService;
    private final Runnable reloadAction;

    public CommandServiceImpl(Plugin plugin, ConfigService configService, MessageService messageService,
                               CartService cartService, MenuService menuService, ShopApiService shopApiService,
                               Runnable reloadAction) {
        this.plugin = plugin;
        this.configService = configService;
        this.messageService = messageService;
        this.cartService = cartService;
        this.menuService = menuService;
        this.shopApiService = shopApiService;
        this.reloadAction = reloadAction;
    }

    @Override
    public void enable() {
        registerAdminCommand();
        registerCartCommand();
    }

    @Override
    public void reload() {
        applyCartAliases();
    }

    @Override
    public void disable() {
    }

    private void registerAdminCommand() {
        ZPaymentsCommand zPaymentsCommand = new ZPaymentsCommand(configService, messageService);

        List<SubCommand> registered = new ArrayList<>();
        registered.add(new SetupSubCommand(plugin, configService, messageService));
        registered.add(new ReloadSubCommand(plugin, configService, messageService, reloadAction));
        registered.add(new StatusSubCommand(configService, messageService, shopApiService));
        registered.add(new TestConnectionSubCommand(plugin, configService, messageService, shopApiService));
        registered.add(0, new HelpSubCommand(configService, messageService, registered));

        for (SubCommand subCommand : registered) {
            zPaymentsCommand.register(subCommand);
        }

        PluginCommand command = plugin.getServer().getPluginCommand("zpayments");
        if (command != null) {
            command.setExecutor(zPaymentsCommand);
            command.setTabCompleter(zPaymentsCommand);
        }
    }

    private void registerCartCommand() {
        CartCommand cartCommand = new CartCommand(configService, messageService, menuService, cartService);
        PluginCommand command = plugin.getServer().getPluginCommand("cart");
        if (command != null) {
            command.setExecutor(cartCommand);
        }
        applyCartAliases();
    }

    private void applyCartAliases() {
        PluginCommand command = plugin.getServer().getPluginCommand("cart");
        if (command == null) {
            return;
        }
        CommandMapUtil.applyAliases(command, configService.getConfig().getCart().getAliases(), plugin.getName().toLowerCase(), plugin.getLogger());
    }
}
