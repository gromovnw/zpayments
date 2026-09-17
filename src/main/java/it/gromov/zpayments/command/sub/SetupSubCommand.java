package it.gromov.zpayments.command.sub;

import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.config.section.ShopSection;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class SetupSubCommand implements SubCommand {

    private final Plugin plugin;
    private final ConfigService configService;
    private final MessageService messageService;

    public SetupSubCommand(Plugin plugin, ConfigService configService, MessageService messageService) {
        this.plugin = plugin;
        this.configService = configService;
        this.messageService = messageService;
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getPermission() {
        return "zpayments.command.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messageService.send(sender, configService.getMessages().getSetupUsage());
            return;
        }

        String shopId = args[0];
        String serverId = args[1];
        String pluginKey = args[2];

        try {
            ShopSection shop = configService.getConfig().getShop();
            shop.setShopId(shopId);
            shop.setServerId(serverId);
            shop.setPluginKey(pluginKey);
            configService.getConfig().save();

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("shop-id", shopId);
            placeholders.put("server-id", serverId);
            messageService.send(sender, configService.getMessages().getSetupSuccess(), placeholders);
        } catch (Exception exception) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("error", String.valueOf(exception.getMessage()));
            messageService.send(sender, configService.getMessages().getSetupFailed(), placeholders);
            plugin.getLogger().warning("Ошибка сохранения настроек zPayments: " + exception.getMessage());
        }
    }
}
