package it.gromov.zpayments.command;

import org.bukkit.command.CommandSender;

public interface SubCommand {

    String getName();

    String getPermission();

    void execute(CommandSender sender, String[] args);
}
