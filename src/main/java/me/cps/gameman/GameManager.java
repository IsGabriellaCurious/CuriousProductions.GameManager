package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.commands.StartCommand;
import me.cps.gameman.events.GameStateChangeEvent;
import me.cps.gameman.events.PerMilliRunnable;
import me.cps.gameman.runnables.StartRunnable;
import me.cps.root.Rank;
import me.cps.root.cpsModule;
import me.cps.root.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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

    private HashMap<Player, GameKit> playerKit = new HashMap<>();

    public int gameStartTimer;

    //TODO make it so this is definded through cpsGame
    private boolean resPack = false;
    private boolean packRequired = false;

    public GameManager(JavaPlugin plugin, cpsGame game) {
        super("Game Manager", plugin, "1.0-alpha", true);
        instance = this;
        registerSelf();
        this.currentGame = game;
        setGameState(GameState.LOADING);

        setupGame();
        registerCommand(new StartCommand(this));
    }

    //All the setter getters for the vars above.


    public static GameManager getInstance() {
        return instance;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        GameStateChangeEvent event = new GameStateChangeEvent(); //this is an event so other things can know when the game state's changed
        Bukkit.getPluginManager().callEvent(event);
        this.gameState = gameState;
        Message.console("GAME STATE UPDATE: " + gameState.toString());
    }

    public cpsGame getCurrentGame() {
        return currentGame;
    }

    public ArrayList<Player> getDefaultPlayers() {
        return defaultPlayers;
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

    public void assignTeam(Player player, ChatColor colour) {
        getPlayerTeams().put(player, colour);
        player.sendMessage(colour + "You have been put on the " + colour + "" + ChatColor.BOLD + getTeamNames().get(colour) + " Team!");
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

    public HashMap<Player, GameKit> getPlayerKit() {
        return playerKit;
    }

    //will load everything the game needs. TODO still need improving probably
    private void setupGame() {
        Message.console("§aOooooh now the fun is starting! Loading game " + getCurrentGame().getGameName());
        getCurrentGame().addKits();
        getCurrentGame().addTeams();
        Message.console("§aI think we are ready to rumble!");
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new PerMilliRunnable(getPlugin()), 0, 1);
        setGameState(GameState.WAITING);
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
            if (Rank.forceHasRank(event.getUniqueId(), Rank.DONATOR)) {
                if (getDefaultPlayers().isEmpty()) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cSorry! This lobby is full and space cannot be made.");
                } else {
                    Random random = new Random();

                    Player toKick = getDefaultPlayers().get(random.nextInt(getDefaultPlayers().size()));
                    toKick.kickPlayer("§c§lYou have been kicked to make room for a §d§lPREMIUM Player§r\n§eBuy a rank today to join full lobbies and secure your place in a game!");
                    event.allow();
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cThis game is currently full! Buy §d§lPREMIUM §cto join full lobbies!");
            }
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        getLivePlayers().add(event.getPlayer());

        if (Rank.getRank(event.getPlayer().getUniqueId()) == Rank.DEFAULT)
            getDefaultPlayers().add(event.getPlayer());

        event.setJoinMessage("");
        getPlugin().getServer().broadcastMessage(Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getName() + " §6has joined the game.");
        if (getLivePlayers().size() == getCurrentGame().getMinPlayers()) {
            getPlugin().getServer().broadcastMessage("§e§lWe have reached the minimum amount of player's required start!");
            new StartRunnable(60, getCurrentGame().getStartBarMessage()).runTaskTimerAsynchronously(getPlugin(), 0 , 20);
        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        if (getDefaultPlayers().contains(event.getPlayer()))
            getDefaultPlayers().remove(event.getPlayer());

        if (getGameState() == GameState.LIVE)
            getCurrentGame().handlePlayerQuit();

        getLivePlayers().remove(event.getPlayer());

        event.setQuitMessage("");
    }
}
