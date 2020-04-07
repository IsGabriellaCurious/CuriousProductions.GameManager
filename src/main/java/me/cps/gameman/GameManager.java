package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.events.PerMilliRunnable;
import me.cps.root.Rank;
import me.cps.root.cpsModule;
import me.cps.root.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManager extends cpsModule {

    private static GameManager instance;

    private cpsGame currentGame;
    private GameState gameState;

    private ArrayList<Player> livePlayers = new ArrayList<>();
    private ArrayList<Player> spectators = new ArrayList<>();
    private HashMap<Player, ChatColor> playerTeams = new HashMap<>();
    private HashMap<ChatColor, String> teamNames = new HashMap<>();

    private ArrayList<Player> defaultPlayers = new ArrayList<>();

    private ArrayList<GameKit> availableKits = new ArrayList<>();
    private ArrayList<GameStat> availableStat = new ArrayList<>();

    //TODO make it so this is definded through cpsGame
    private boolean resPack = false;
    private boolean packRequired = false;

    public GameManager(JavaPlugin plugin, cpsGame game) {
        super("Game Manager", plugin, "1.0-alpha", true);
        instance = this;
        registerSelf();
        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new PerMilliRunnable(getPlugin()), 0, 1);
        this.currentGame = game;
        gameState = GameState.LOADING;

        setupGame();
    }

    //All the setter getters for the vars above.


    public static GameManager getInstance() {
        return instance;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        getPlugin().getServer().getPluginManager().callEvent(new GameStateChangeEvent());
        this.gameState = gameState;
    }

    public cpsGame getCurrentGame() {
        return currentGame;
    }

    public ArrayList<Player> getLivePlayers() {
        return livePlayers;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public HashMap<Player, ChatColor> getPlayerTeams() {
        return playerTeams;
    }

    public HashMap<ChatColor, String> getTeamNames() {
        return teamNames;
    }

    public ArrayList<GameKit> getAvailableKits() {
        return availableKits;
    }

    public ArrayList<GameStat> getAvailableStat() {
        return availableStat;
    }

    public boolean isResPack() {
        return resPack;
    }

    public void setResPack(boolean resPack) {
        this.resPack = resPack;
    }

    public boolean isPackRequired() {
        return packRequired;
    }

    public void setPackRequired(boolean packRequired) {
        this.packRequired = packRequired;
    }

    private void setupGame() {
        Message.console("§aOooooh now the fun is starting! Loading game " + getCurrentGame().getGameName());
        getCurrentGame().addKits();
        getCurrentGame().addTeams();
        Message.console("§aI think we are ready to rumble!");
    }

    @EventHandler
    public void playerPreJoin(AsyncPlayerPreLoginEvent event) {
        if (getGameState() == GameState.DEAD || getGameState() == GameState.LOADING || getGameState() == GameState.ENDING) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cThis server is currently not in a joinable state.");
            return;
        }
        if (getGameState() == GameState.LIVE) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cThis server has currently got an active game!");
            return;
        }

        if (getPlugin().getServer().getOnlinePlayers().size() >= getCurrentGame().getMaxPlayers()) {
            //todo remove a default player.
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage("");
        getPlugin().getServer().broadcastMessage(Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getName() + " §6has joined the game.");
        if (getPlugin().getServer().getOnlinePlayers().size() == getCurrentGame().getMinPlayers()) {
            getPlugin().getServer().broadcastMessage("§e§lWe have reached the minimum amount of player's required start!");
            //todo runnable
        }
    }
}
