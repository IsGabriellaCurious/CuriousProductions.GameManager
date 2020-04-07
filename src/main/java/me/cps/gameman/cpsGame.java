package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.events.PerMilliEvent;
import me.cps.root.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class cpsGame implements Listener {

    private JavaPlugin plugin;

    private String gameName;

    private boolean respawn;
    private int respawnTimer;

    private int minPlayers;
    private int maxPlayers;
    private boolean forceMax; //must the game be full to start

    private GameKit defaultKit;

    public cpsGame(JavaPlugin plugin, String gameName, boolean respawn, int respawnTimer, int minPlayers, int maxPlayers, boolean forceMax, GameKit defaultKit) {
        Message.console("§aGame file is being initialized!");
        this.plugin = plugin;
        this.gameName = gameName;
        this.respawn = respawn;
        this.respawnTimer = respawnTimer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.forceMax = forceMax;
        this.defaultKit = defaultKit;
        Message.console("§aDone!");
    }

    private void addKit(GameKit kit) {
        GameManager.getInstance().getAvailableKits().add(kit);
    }

    public abstract void addKits();

    private void addTeam(String name, ChatColor color) {
        GameManager.getInstance().getTeamNames().put(color, name);
    }

    public abstract void addTeams();

    //all kits, teleports, runnables, etc should be started here.
    public abstract void startGame();

    public abstract void endCheck();

    public abstract void assignTeams();

    public abstract void giveGameRewards();

    @EventHandler
    public void onUpdate(PerMilliEvent event) {
        if (GameManager.getInstance().getGameState() != GameState.LIVE)
            return;

        endCheck();
    }

    //Setter getters


    public String getGameName() {
        return gameName;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public boolean isRespawn() {
        return respawn;
    }

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isForceMax() {
        return forceMax;
    }

    public GameKit getDefaultKit() {
        return defaultKit;
    }
}
