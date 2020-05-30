package me.cps.gameman.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Curious Productions Game Manager
 * Game State Change Event
 *
 * Fires when the a game's state changes
 *
 * @author  Gabriella Hotten
 * @since   2020-04-07
 */
public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
