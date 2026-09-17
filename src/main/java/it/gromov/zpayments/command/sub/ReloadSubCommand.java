package it.gromov.zpayments.command.sub;

import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class ReloadSubCommand implements SubCommand {

    private final Plugin plugin;
    private final ConfigService configService;
    private final MessageService messageService;
    private final Runnable reloadAction;

    public ReloadSubCommand(Plugin plugin, ConfigService configService, MessageService messageService, Runnable reloadAction) {
        this.plugin = plugin;
        this.configService = configService;
        this.messageService = messageService;
        this.reloadAction = reloadAction;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "zpayments.command.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            reloadAction.run();
            messageService.send(sender, configService.getMessages().getReloadSuccess());
        } catch (Exception exception) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("error", String.valueOf(exception.getMessage()));
            messageService.send(sender, configService.getMessages().getReloadFailed(), placeholders);
            plugin.getLogger().warning("Ошибка перезагрузки конфигурации zPayments: " + exception.getMessage());
        }
    }
}
