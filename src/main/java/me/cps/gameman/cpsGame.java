package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.events.GameStartEvent;
import me.cps.gameman.runnables.EndRunnable;
import me.cps.gameman.stat.GameStat;
import me.cps.gameman.stat.StatManager;
import me.cps.root.util.PerMilliEvent;
import me.cps.root.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public abstract class cpsGame implements Listener {

    private JavaPlugin plugin;

    private String gameName;
    private String scoreName;
    private String mysqlName;

    private boolean respawn;
    private int respawnTimer;

    private int minPlayers;
    private int maxPlayers;
    private boolean forceMax; //must the game be full to start

    private String defaultKit;
    private HashMap<String, GameKit> kitNames = new HashMap<>();

    private boolean teamJoinMessage;

    private Location hub;

    private String startBarMessage = "The game will start in";

    public cpsGame(JavaPlugin plugin, String gameName, String scoreName, String mysqlName, boolean respawn, int respawnTimer, int minPlayers, int maxPlayers, boolean forceMax, String defaultKit, boolean teamJoinMessage) {
        Message.console("§aGame file is being initialized!");
        this.plugin = plugin;
        this.gameName = gameName;
        this.mysqlName = mysqlName;
        this.scoreName = scoreName;
        this.respawn = respawn;
        this.respawnTimer = respawnTimer;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.forceMax = forceMax;
        this.defaultKit = defaultKit;
        this.teamJoinMessage = teamJoinMessage;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Message.console("§aDone!");
    }

    public void addKit(GameKit kit) {
        GameManager.getInstance().getAvailableKits().add(kit);
        kitNames.put(kit.getName(), kit);
    }

    public void setHub(Location location) {
        this.hub = location;
    }


    public abstract void addStats();

    public abstract void addTeams();

    public abstract void assignTeams(); //MUST BE RUN INSIDE OF startGame()

    public abstract void addKits();

    public abstract void giveKitItems(); //MUST BE RUN INSIDE OF startGame()

    @EventHandler
    public abstract void startGame(GameStartEvent event); //all kits, teleports, runnables, etc should be started here.

    public abstract void scoreboard(Player player);

    public abstract void handlePlayerQuit(Player player);

    public abstract void endCheck();

    public abstract void announceWinner(Player player, ChatColor color);

    public abstract void giveGameRewards();

    public void addTeam(String name, ChatColor color) {
        GameManager.getInstance().getTeamNames().put(color, name);
    }

    public void addStat(GameStat stat) {
        StatManager.getInstance().registerStat(stat);

    }

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

    public String getMysqlName() {
        return mysqlName;
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

    public boolean isTeamJoinMessage() {
        return teamJoinMessage;
    }

    public Location getHub() {
        return hub;
    }

    //Now the methods

    public void endGame(Player pWinner, ChatColor cWinner) {
        GameManager.getInstance().setGameState(GameState.ENDING);
        new EndRunnable(pWinner, cWinner, 10).runTaskAsynchronously(plugin);
    }

    public void endGame(Player pWinner, ChatColor cWinner, int toHubSecs) {
        GameManager.getInstance().setGameState(GameState.ENDING);
        new EndRunnable(pWinner, cWinner, toHubSecs).runTaskAsynchronously(plugin);
    }

}
