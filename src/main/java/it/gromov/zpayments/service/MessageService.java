package it.gromov.zpayments.service;

import org.bukkit.command.CommandSender;

import java.util.Map;

public interface MessageService extends Service {

    void send(CommandSender receiver, String message);

    void send(CommandSender receiver, String message, Map<String, String> placeholders);

    String format(String message, Map<String, String> placeholders);
}
