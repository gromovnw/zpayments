package it.gromov.zpayments.command.sub;

import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.service.ShopApiService;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public final class StatusSubCommand implements SubCommand {

    private final ConfigService configService;
    private final MessageService messageService;
    private final ShopApiService shopApiService;

    public StatusSubCommand(ConfigService configService, MessageService messageService, ShopApiService shopApiService) {
        this.configService = configService;
        this.messageService = messageService;
        this.shopApiService = shopApiService;
    }

    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getPermission() {
        return "zpayments.command.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long lastPoll = shopApiService.getLastPollTimestamp();
        long agoSeconds = lastPoll == 0 ? -1 : (System.currentTimeMillis() - lastPoll) / 1000;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("shop-id", configService.getConfig().getShop().getShopId());
        placeholders.put("server-id", configService.getConfig().getShop().getServerId());
        placeholders.put("last-poll-ago", agoSeconds < 0 ? "?" : String.valueOf(agoSeconds));
        placeholders.put("last-poll-status", shopApiService.isLastPollSuccessful() ? "OK" : "ERROR");

        messageService.send(sender, configService.getMessages().getStatusLine(), placeholders);
    }
}
