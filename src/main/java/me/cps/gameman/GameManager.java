package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.wrapper.Wrapper;
import me.cps.gameman.chat.GMChatHub;
import me.cps.gameman.commands.StaffScoreboardCommand;
import me.cps.gameman.commands.StartCommand;
import me.cps.gameman.events.GameStateChangeEvent;
import me.cps.gameman.runnables.SpectatorRunnable;
import me.cps.gameman.stat.GameStat;
import me.cps.gameman.stat.PlayerStat;
import me.cps.gameman.stat.StatManager;
import me.cps.root.scoreboard.ScoreboardCentre;
import me.cps.root.scoreboard.cpsScoreboard;
import me.cps.root.staff.StaffHub;
import me.cps.root.staff.StaffModeUpdateEvent;
import me.cps.root.staff.StaffOptionUpdateEvent;
import me.cps.root.staff.StaffOptions;
import me.cps.root.util.PerMilliEvent;
import me.cps.root.util.PerMilliRunnable;
import me.cps.gameman.runnables.StartRunnable;
import me.cps.gameman.runnables.WaitingRunnable;
import me.cps.root.Rank;
import me.cps.root.cpsModule;
import me.cps.root.proxy.ProxyManager;
import me.cps.root.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
    private ArrayList<Player> specAfterRespawn = new ArrayList<>();
    private HashMap<Player, ChatColor> playerTeams = new HashMap<>();
    private HashMap<ChatColor, String> teamNames = new HashMap<>();

    private ArrayList<Player> defaultPlayers = new ArrayList<>();
    private ArrayList<Player> wasInGame = new ArrayList<>();

    private ArrayList<GameKit> availableKits = new ArrayList<>();

    private HashMap<Player, GameKit> playerKit = new HashMap<>();

    public int gameStartTimer;

    //TODO make it so this is definded through cpsGame
    private boolean resPack = false;
    private boolean packRequired = false;

    private ServiceId serviceId = Wrapper.getInstance().getServiceId();

    public static String serverName;

    private ArrayList<Player> staffscore = new ArrayList<>();

    public GameManager(JavaPlugin plugin, cpsGame game) {
        super("Game Manager", plugin, "1.3.2", true);
        instance = this;
        registerSelf();
        this.currentGame = game;
        serverName = serviceId.getName();
        setGameState(GameState.LOADING);

        setupGame();
        registerCommand(new StartCommand(this));
        registerCommand(new StaffScoreboardCommand(this));
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
        if (gameState == GameState.LIVE)
            Message.console("Live players: " + getLivePlayers().toString());
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
        if (getCurrentGame().isTeamJoinMessage())
            player.sendMessage(colour + "You have been put on the " + colour + "" + ChatColor.BOLD + getTeamNames().get(colour) + " Team!");
    }

    public HashMap<ChatColor, String> getTeamNames() {
        return teamNames;
    }

    public ArrayList<GameKit> getAvailableKits() {
        return availableKits;
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

    public ArrayList<Player> getWasInGame() {
        return wasInGame;
    }

    public HashMap<Player, GameKit> getPlayerKit() {
        return playerKit;
    }

    public ArrayList<Player> getStaffscore() {
        return staffscore;
    }

    //will load everything the game needs. TODO still need improving probably
    private void setupGame() {
        Message.console("§aOooooh now the fun is starting! Loading game " + getCurrentGame().getGameName());
        getCurrentGame().addKits();
        getCurrentGame().addTeams();
        getCurrentGame().addStats();
        Message.console("§aI think we are ready to rumble!");
        //Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new PerMilliRunnable(getPlugin()), 0, 1);
        setGameState(GameState.WAITING);
        startWaiting();
    }

    public void startWaiting() {
        new WaitingRunnable().runTaskTimerAsynchronously(getPlugin(), 0, 10);
    }

    public void makeSpectator(Player player, boolean specAfterRespawn) {
        if (specAfterRespawn) {
            this.specAfterRespawn.add(player);
            return;
        }
        getSpectators().add(player);
        player.sendMessage("§7You are now a spectator.");
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.getInventory().clear();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.hidePlayer(player);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (specAfterRespawn.contains(event.getPlayer())) {
            specAfterRespawn.remove(event.getPlayer());
            makeSpectator(event.getPlayer(), false);
        }
    }

    public void startSpecRun() {
        new SpectatorRunnable().runTaskTimerAsynchronously(getPlugin(), 0, 20);
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
                    ProxyManager.getInstance().sendToLobby(toKick, false);
                    ProxyManager.getInstance().sendPlayerMessage(toKick, "§c§lYou have been kicked to make room for a §d§lPREMIUM Player§r\n§eBuy a rank today to join full lobbies and secure your place in a game!");
                    //toKick.kickPlayer("§c§lYou have been kicked to make room for a §d§lPREMIUM Player§r\n§eBuy a rank today to join full lobbies and secure your place in a game!");
                    event.allow();
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "§cThis game is currently full! Buy §d§lPREMIUM §cto join full lobbies!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (getCurrentGame().getHub() != null)
            event.getPlayer().teleport(getCurrentGame().getHub());

        if (StaffHub.getInstance().getInStaffMode().contains(event.getPlayer())) {
            if (getGameState() == GameState.WAITING)
                lobbyScoreboard(event.getPlayer());

            event.setJoinMessage("");

            return;
        }

        for (Player p : StaffHub.getInstance().getVanished()) {
            if (!Rank.hasRank(event.getPlayer().getUniqueId(), Rank.HELPER))
                event.getPlayer().hidePlayer(p);
        }

        getLivePlayers().add(event.getPlayer());
        Message.console(getLivePlayers().toString());
        if (getGameState() == GameState.WAITING)
            lobbyScoreboard(event.getPlayer());

        if (Rank.getRank(event.getPlayer().getUniqueId()) == Rank.DEFAULT)
            getDefaultPlayers().add(event.getPlayer());

        event.setJoinMessage("");
        Message.broadcast(Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getName() + " §7has joined the game.");
        if (getLivePlayers().size() == getCurrentGame().getMinPlayers()) {
            getPlugin().getServer().broadcastMessage("§eWe have reached the minimum amount of player's required start!");
            new StartRunnable(60, getCurrentGame().getStartBarMessage()).runTaskTimerAsynchronously(getPlugin(), 0 , 20);
        }
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event) {
        if (getDefaultPlayers().contains(event.getPlayer()))
            getDefaultPlayers().remove(event.getPlayer());

        if (getGameState() == GameState.LIVE)
            getCurrentGame().handlePlayerQuit(event.getPlayer());

        getLivePlayers().remove(event.getPlayer());

        event.setQuitMessage("");
    }


    //scoreboard shit

    private void lobbyScoreboard(Player player) {
        cpsScoreboard s = ScoreboardCentre.getInstance().getScoreboards().get(player);

        s.setTitle(getCurrentGame().getScoreName());
        s.clear();
        s.add("§8»§m--------------------§r§8«");
        if (StaffHub.getInstance().getInStaffMode().contains(player)) {
            s.add("§cStaff Mode");
            s.add("Vanish §8» " + (StaffHub.getInstance().getOption(StaffOptions.Vanish, player) ? "§aEnabled" : "§cDisabled"));
            s.add("Anti-Game Chat §8» " + (StaffHub.getInstance().getOption(StaffOptions.GameChat, player) ? "§aEnabled" : "§cDisabled"));
        } else {
            s.add("§aYour Stats");
            for (GameStat stat : StatManager.getInstance().getAvailableStat().values()) {
                PlayerStat playerStat = StatManager.getInstance().getPlayerStat(player);
                s.add("§b" + stat.getDisplayName() + ": §f" + playerStat.getStat(stat));
            }
        }
        s.addEmpty();
        s.add("§ePlayers §8» §f" + getLivePlayers().size() + "/" + getCurrentGame().getMaxPlayers());
        s.addEmpty();
        s.add("§9Server §8» §f" + serverName);
        s.add("§r§8»§m--------------------§r§8«");
        s.add("§bplay.CPS.me");

        s.apply();
    }

    public void staffScoreboard(Player player) {
        cpsScoreboard s = ScoreboardCentre.getInstance().getScoreboards().get(player);

        s.setTitle("§c§lStaff Mode");
        s.clear();
        s.add("Vanish: " + (StaffHub.getInstance().getOption(StaffOptions.Vanish, player) ? "§aEnabled" : "§cDisabled"));
        s.add("Anti-Game Chat: " + (StaffHub.getInstance().getOption(StaffOptions.GameChat, player) ? "§aEnabled" : "§cDisabled"));
        if (Rank.getRank(player.getUniqueId()) == Rank.HELPER) {
            s.addEmpty();
            s.add("Run §7/staffscore §fto disable.");
        }

        s.apply();
    }

    @EventHandler
    public void scoreboardMilli(PerMilliEvent event) {
        if (getGameState() == GameState.WAITING) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                lobbyScoreboard(p);
            }
        }
        if (getGameState() == GameState.LIVE) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (getStaffscore().contains(p)) {
                    staffScoreboard(p);
                } else
                    getCurrentGame().scoreboard(p);
            }
        }
    }

    @EventHandler
    public void onStaffOption(StaffOptionUpdateEvent event) {
        StaffOptions option = event.getOption();
        Player staff = event.getPlayer();
        boolean toggle = event.isOptionEnabled();
        boolean override = event.isOverride();

        if (option == StaffOptions.GameChat) {
            if (toggle)
                GMChatHub.staffChatDisabled.add(staff);
            else
                GMChatHub.staffChatDisabled.remove(staff);
        }
    }

    @Deprecated
    @EventHandler
    public void onStaffModeUpdate(StaffModeUpdateEvent event) {
        Player staff = event.getPlayer();
        boolean toggle = event.isToggle();

        if (toggle) {
            getStaffscore().add(staff);
            ScoreboardCentre.getInstance().resetCache(staff);
            staff.sendMessage("§8You have been removed from the game and are invisible to others!");
            if (!StaffHub.getInstance().getOption(StaffOptions.Vanish, staff))
                StaffHub.getInstance().toggleVanish(true, true, true, staff);

            if (getLivePlayers().contains(staff))
                getLivePlayers().remove(staff);

            staff.setAllowFlight(true);
            staff.setFlying(true);

            if (getGameState() == GameState.WAITING) {
                //idk
            } else if (getGameState() == GameState.LIVE) {
                getCurrentGame().handlePlayerQuit(staff);
                if (getStaffscore().contains(staff))
                    getStaffscore().remove(staff);
                getStaffscore().add(staff);
                ScoreboardCentre.getInstance().resetCache(staff);
            }
        } else {
            getStaffscore().remove(staff);
           ScoreboardCentre.getInstance().resetCache(staff);
            if (getGameState() == GameState.LIVE) {
                StaffHub.getInstance().staffMode(staff);
                StaffHub.getInstance().toggleVanish(true, true, false, staff);
                staff.sendMessage("§cYou cannot disable staff mode during a game!");
                return;
            }
            if (getGameState() == GameState.WAITING) {
                getLivePlayers().add(staff);
                Message.broadcast(Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getName() + " §7has joined the game.");
                if (getLivePlayers().size() == getCurrentGame().getMinPlayers()) {
                    getPlugin().getServer().broadcastMessage("§eWe have reached the minimum amount of player's required start!");
                    new StartRunnable(60, getCurrentGame().getStartBarMessage()).runTaskTimerAsynchronously(getPlugin(), 0 , 20);
                }
            }
            StaffHub.getInstance().toggleVanish(false, true, false, staff);
            GMChatHub.staffChatDisabled.remove(staff);
            staff.setAllowFlight(false);
            staff.setFlying(false);


        }
    }
}
