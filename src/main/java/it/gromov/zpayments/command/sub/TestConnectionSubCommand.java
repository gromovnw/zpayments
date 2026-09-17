package it.gromov.zpayments.command.sub;

import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.service.ShopApiService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class TestConnectionSubCommand implements SubCommand {

    private final Plugin plugin;
    private final ConfigService configService;
    private final MessageService messageService;
    private final ShopApiService shopApiService;

    public TestConnectionSubCommand(Plugin plugin, ConfigService configService, MessageService messageService, ShopApiService shopApiService) {
        this.plugin = plugin;
        this.configService = configService;
        this.messageService = messageService;
        this.shopApiService = shopApiService;
    }

    @Override
    public String getName() {
        return "testconnection";
    }

    @Override
    public String getPermission() {
        return "zpayments.command.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        messageService.send(sender, configService.getMessages().getTestConnectionRunning());
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean success = shopApiService.testConnection();
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (success) {
                    messageService.send(sender, configService.getMessages().getTestConnectionSuccess());
                } else {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("error", String.valueOf(shopApiService.getLastErrorMessage()));
                    messageService.send(sender, configService.getMessages().getTestConnectionFailed(), placeholders);
                }
            });
        });
    }
}
