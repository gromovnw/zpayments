package it.gromov.zpayments.command;

import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ZPaymentsCommand implements CommandExecutor, TabCompleter {

    private final ConfigService configService;
    private final MessageService messageService;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public ZPaymentsCommand(ConfigService configService, MessageService messageService) {
        this.configService = configService;
        this.messageService = messageService;
    }

    public void register(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public List<SubCommand> getSubCommands() {
        return new ArrayList<>(subCommands.values());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SubCommand target = args.length == 0 ? subCommands.get("help") : subCommands.get(args[0].toLowerCase());

        if (target == null) {
            messageService.send(sender, configService.getMessages().getUnknownSubCommand());
            return true;
        }
        if (target.getPermission() != null && !sender.hasPermission(target.getPermission())) {
            messageService.send(sender, configService.getMessages().getNoPermission());
            return true;
        }

        String[] rest = args.length == 0 ? args : Arrays.copyOfRange(args, 1, args.length);
        target.execute(sender, rest);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }
        String partial = args[0].toLowerCase();
        return subCommands.keySet().stream()
                .filter(name -> name.startsWith(partial))
                .collect(Collectors.toList());
    }
}
