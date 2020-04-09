package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.root.util.PerMilliEvent;
import me.cps.root.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public abstract class cpsGame implements Listener {

    private JavaPlugin plugin;

    private String gameName;
    private String scoreName;

    private boolean respawn;
    private int respawnTimer;

    private int minPlayers;
    private int maxPlayers;
    private boolean forceMax; //must the game be full to start

    private String defaultKit;
    private HashMap<String, GameKit> kitNames = new HashMap<>();

    private String startBarMessage = "The game will start in";

    public cpsGame(JavaPlugin plugin, String gameName, String scoreName, boolean respawn, int respawnTimer, int minPlayers, int maxPlayers, boolean forceMax, String defaultKit) {
        Message.console("§aGame file is being initialized!");
        this.plugin = plugin;
        this.gameName = gameName;
        this.scoreName = scoreName;
        this.respawn = respawn;
        this.respawnTimer = respawnTimer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.forceMax = forceMax;
        this.defaultKit = defaultKit;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Message.console("§aDone!");
    }

    public void addKit(GameKit kit) {
        GameManager.getInstance().getAvailableKits().add(kit);
        kitNames.put(kit.getName(), kit);
    }

    public abstract void addKits();

    public void addTeam(String name, ChatColor color) {
        GameManager.getInstance().getTeamNames().put(color, name);
    }

    public abstract void addTeams();

    //all kits, teleports, runnables, etc should be started here.
    public abstract void startGame();

    public abstract void endCheck();

    public abstract void assignTeams();

    public abstract void giveKitItems(); //MUST BE RUN INSIDE OF startGame()

    public abstract void giveGameRewards();

    public abstract void handlePlayerQuit();


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

    public String getScoreName() {
        return scoreName;
    }

    public void setScoreName(String scoreName) {
        this.scoreName = scoreName;
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

    public String getDefaultKit() {
        return defaultKit;
    }

    public HashMap<String, GameKit> getKitNames() {
        return kitNames;
    }

    public String getStartBarMessage() {
        return startBarMessage;
    }

    public void setStartBarMessage(String startBarMessage) {
        this.startBarMessage = startBarMessage;
    }

    //Now the methods

    public void endGame() {
        GameManager.getInstance().setGameState(GameState.ENDING);
        //run the end runnable
    }

    public abstract void announceWinner(Player player, ChatColor color);
}
