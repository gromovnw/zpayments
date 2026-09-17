package it.gromov.zpayments.api.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public final class PurchaseQueuedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String orderId;
    private final String nickname;
    private final String taskKey;

    public PurchaseQueuedEvent(String orderId, String nickname, String taskKey) {
        this.orderId = orderId;
        this.nickname = nickname;
        this.taskKey = taskKey;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
