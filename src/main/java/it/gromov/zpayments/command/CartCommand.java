package it.gromov.zpayments.command;

import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MenuService;
import it.gromov.zpayments.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public final class CartCommand implements CommandExecutor {

    private final ConfigService configService;
    private final MessageService messageService;
    private final MenuService menuService;
    private final CartService cartService;

    public CartCommand(ConfigService configService, MessageService messageService, MenuService menuService, CartService cartService) {
        this.configService = configService;
        this.messageService = messageService;
        this.menuService = menuService;
        this.cartService = cartService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            messageService.send(sender, configService.getMessages().getPlayerOnly());
            return true;
        }
        Player player = (Player) sender;

        if (cartService.isCartEmpty(player.getName())) {
            messageService.send(player, configService.getMessages().getCartEmpty());
            return true;
        }

        if (menuService.isOnCooldown(player)) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("seconds", String.valueOf(menuService.getRemainingCooldownSeconds(player)));
            messageService.send(player, configService.getMessages().getCartOnCooldown(), placeholders);
            return true;
        }

        if (menuService.openCart(player)) {
            messageService.send(player, configService.getMessages().getCartOpened());
        }
        return true;
    }
}
