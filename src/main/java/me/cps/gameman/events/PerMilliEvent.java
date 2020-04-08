package me.cps.gameman.events;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*

so this event works using a runnables. every millisecond this event will fire. (almost, anyway)

potential usage:
this is so you can check for things like state changes to check for a game to end, etc.

 */

public class PerMilliEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
