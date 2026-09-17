package it.gromov.zpayments.service;

import it.gromov.zpayments.model.PurchaseTask;
import it.gromov.zpayments.storage.entity.CartEntry;
import org.bukkit.entity.Player;

import java.util.List;

public interface CartService extends Service {

    void process(PurchaseTask task);

    List<CartEntry> getCart(String nickname);

    boolean isCartEmpty(String nickname);

    void claimOnJoin(Player player);

    int claimAll(Player player);

    boolean claimOne(Player player, CartEntry entry);
}
