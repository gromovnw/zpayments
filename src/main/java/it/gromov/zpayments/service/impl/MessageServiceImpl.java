package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.util.ColorUtil;
import it.gromov.zpayments.util.PlaceholderUtil;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public final class MessageServiceImpl implements MessageService {

    private final ConfigService configService;

    public MessageServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void enable() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void send(CommandSender receiver, String message) {
        send(receiver, message, java.util.Collections.emptyMap());
    }

    @Override
    public void send(CommandSender receiver, String message, Map<String, String> placeholders) {
        String formatted = format(message, placeholders);
        if (!formatted.isEmpty()) {
            receiver.sendMessage(formatted);
        }
    }

    @Override
    public String format(String message, Map<String, String> placeholders) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        Map<String, String> merged = new HashMap<>(placeholders == null ? java.util.Collections.emptyMap() : placeholders);
        merged.putIfAbsent("prefix", configService.getMessages().getPrefix());
        return ColorUtil.colorize(PlaceholderUtil.apply(message, merged));
    }
}
