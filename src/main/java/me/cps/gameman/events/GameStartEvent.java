package me.cps.gameman.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Curious Productions Game Manager
 * Game Start Event
 *
 * Fires when the game starts.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-25
 */
public class GameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
