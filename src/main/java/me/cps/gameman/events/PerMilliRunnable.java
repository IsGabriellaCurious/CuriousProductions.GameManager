package me.cps.gameman.events;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import org.bukkit.plugin.java.JavaPlugin;

public class PerMilliRunnable implements Runnable {

    private JavaPlugin plugin;

    public PerMilliRunnable(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getServer().getPluginManager().callEvent(new PerMilliEvent());
    }
}
