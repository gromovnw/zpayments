package it.gromov.zpayments.command.sub;

import it.gromov.zpayments.command.SubCommand;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HelpSubCommand implements SubCommand {

    private final ConfigService configService;
    private final MessageService messageService;
    private final List<SubCommand> subCommands;

    public HelpSubCommand(ConfigService configService, MessageService messageService, List<SubCommand> subCommands) {
        this.configService = configService;
        this.messageService = messageService;
        this.subCommands = subCommands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "zpayments.command.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        messageService.send(sender, configService.getMessages().getHelpHeader());
        for (SubCommand subCommand : subCommands) {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("name", subCommand.getName());
                messageService.send(sender, configService.getMessages().getHelpEntryFormat(), placeholders);
            }
        }
    }
}
