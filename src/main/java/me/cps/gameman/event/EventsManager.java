package me.cps.gameman.event;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.event.commands.SetEventCommand;
import me.cps.root.Rank;
import me.cps.root.cpsModule;
import me.cps.root.util.Message;
import me.cps.root.util.PlaySound;
import me.cps.root.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EventsManager extends cpsModule {

    private GameManager gameManager;

    public static Player host;

    public EventsManager(JavaPlugin plugin, GameManager gameManager) {
        super("Events Manager", plugin, "1.0-alpha", false);
        this.gameManager = gameManager;
        registerSelf();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void commandPreProcess(PlayerCommandPreprocessEvent event) {
        String requestedCommand = event.getMessage().substring(1).split(" ")[0]; //grabs the command from the whole bit entered.
        String[] args = removeCommand(event.getMessage()); //the args!
        Player player = event.getPlayer();

        if (requestedCommand == null)
            return;

        if (!requestedCommand.equalsIgnoreCase("e"))
            return;

        if (!GameManager.getInstance().isEventMode())
            return;

        if (!GameManager.getInstance().getEventStaff().containsKey(player) && !Rank.hasRank(player.getUniqueId(), Rank.DEVELOPER))
            return;

        event.setCancelled(true);

        if (args == null) {
            Help(player);
            return;
        }

        if (args[0].equalsIgnoreCase("tp")) {
            if (args.length == 2) { //either tp to a player or tp all
                if (args[1].equalsIgnoreCase("all")) {
                    player.sendMessage("§eTeleporting all players to your location.");
                    for (Player all : Bukkit.getServer().getOnlinePlayers()) {
                        if (all == player)
                            return;
                        all.teleport(player.getLocation());
                        all.sendMessage("§eYou were teleported to " + player.getName());
                    }
                    return;
                }
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target == null) {
                    Player _t = PlayerUtil.search(args[1]);
                    if (_t == null) {
                        player.sendMessage("§cError searching for " + args[1]);
                        return;
                    }
                    target = _t;
                }

                player.teleport(target.getLocation());
                player.sendMessage("§eTeleported you to " + target.getName());
            } else if (args.length == 3) { //either here or player to player
                if (args[1].equalsIgnoreCase("here")) {
                    Player target = Bukkit.getServer().getPlayer(args[2]);
                    if (target == null) {
                        Player _t = PlayerUtil.search(args[1]);
                        if (_t == null) {
                            player.sendMessage("§cError searching for " + args[1]);
                            return;
                        }
                        target = _t;
                    }
                    target.teleport(player);
                    target.sendMessage("§eYou were teleported to " + player.getName());
                    player.sendMessage("§eTeleported " + target.getName() + " to your location");
                    return;
                }
                Player p1 = Bukkit.getServer().getPlayer(args[1]);
                Player p2 = Bukkit.getServer().getPlayer(args[2]);
                if (p1 == null) {
                    Player _t = PlayerUtil.search(args[1]);
                    if (_t == null) {
                        player.sendMessage("§cError searching for " + args[1]);
                        return;
                    }
                    p1 = _t;
                }
                if (p2 == null) {
                    Player _t = PlayerUtil.search(args[2]);
                    if (_t == null) {
                        player.sendMessage("§cError searching for " + args[2]);
                        return;
                    }
                    p2 = _t;
                }
                p1.teleport(p2.getLocation());
                p1.sendMessage("§eYou were teleported to " + p2.getName());
                p2.sendMessage("§e" + p1.getName() + " was teleported to your location.");
                player.sendMessage("§cTeleported " + p1.getName() + " to " + p2.getName());
            } else {
                Help(player);
            }
        } else if (args[0].equalsIgnoreCase("bc") || args[0].equalsIgnoreCase("broadcast")) {
            if (args.length != 2) {
                Help(player);
                return;
            }

            player.sendMessage("§cwhoops this doesnt work yet");
            //Message.broadcast("§b§lEVENT BROADCAST §8» §f");
        } else if (args[0].equalsIgnoreCase("start")) { //may or may not have been copy pasted from startcommand
            if (GameManager.getInstance().getGameState() != GameState.WAITING) {
                player.sendMessage("§cThe game cannot be started right now.");
                return;
            }

            GameManager.getInstance().gameStartTimer = 10;
            Message.broadcast("§a§lThe timer has been shortened to 10 seconds.");
        } else if (args[0].equalsIgnoreCase("fly")) {
            if (args.length == 2) {
                Player target = Bukkit.getServer().getPlayer(args[1]);
                if (target == null) {
                    Player _t = PlayerUtil.search(args[1]);
                    if (_t == null) {
                        player.sendMessage("§cError searching for " + args[1]);
                        return;
                    }
                    target = _t;
                }

                boolean result;
                if (target.isFlying()) {
                    target.setAllowFlight(false);
                    target.setFlying(false);
                    result = false;
                    target.sendMessage("§cFlight disabled.");
                } else {
                    target.setAllowFlight(true);
                    target.setFlying(true);
                    result = true;
                    target.sendMessage("§aFlight enabled.");
                }
                player.sendMessage("§e" + target.getName() + "'s flight has been " + (result ? "§aEnabled" : "§cDisabled"));
                return;
            }
            if (player.isFlying()) {
                player.setAllowFlight(false);
                player.setFlying(false);
                player.sendMessage("§cFlight disabled.");
            } else {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.sendMessage("§aFlight enabled.");
            }
        } else if (args[0].equalsIgnoreCase("stat")) {
            if (!Rank.hasRank(player.getUniqueId(), Rank.ADMIN)) {
                player.sendMessage("§cYou don't have permission to toggle game stat!");
                return;
            }
            if (GameManager.getInstance().isStats()) {
                player.sendMessage("§cYou disabled stats.");
                Message.broadcast("§cStats have been disabled for this game.");
                GameManager.getInstance().setStats(false);
                PlaySound.all(Sound.VILLAGER_NO, 100, 1);
            } else {
                player.sendMessage("§aYou enabled stats.");
                Message.broadcast("§aStats have been enabled for this game!");
                GameManager.getInstance().setStats(true);
                PlaySound.all(Sound.ORB_PICKUP, 100, 1);
            }
        } else if (args[0].equalsIgnoreCase("gm") || args[0].equalsIgnoreCase("gamemode")) {
            if (!(args.length >= 2)) {
                Help(player);
                return;
            }

            int gm;
            GameMode gamemode;
            try {
                gm = Integer.parseInt(args[1]);
            } catch (Exception e) {
                player.sendMessage("§cMust be a number!");
                return;
            }

            if (gm == 0)
                gamemode = GameMode.SURVIVAL;
            else if (gm == 1)
                gamemode = GameMode.CREATIVE;
            else if (gm == 2)
                gamemode = GameMode.ADVENTURE;
            else if (gm == 3)
                gamemode = GameMode.SPECTATOR;
            else {
                player.sendMessage("§cInvalid gamemode!");
                return;
            }

            if (args.length == 3) {
                if (args[2].equalsIgnoreCase("all")) {
                    for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                        target.setGameMode(gamemode);
                        target.sendMessage("§eYour gamemode was updated to " + gamemode.toString());
                    }
                    player.sendMessage("§eEveryone's gamemode was updated to " + gamemode.toString());
                    return;
                }
                Player target = Bukkit.getServer().getPlayer(args[2]);
                if (target == null) {
                    Player _t = PlayerUtil.search(args[2]);
                    if (_t == null) {
                        player.sendMessage("§cError searching for " + args[2]);
                        return;
                    }
                    target = _t;
                }

                target.setGameMode(gamemode);
                target.sendMessage("§eYour gamemode was updated to " + gamemode.toString());
                player.sendMessage("§e" + target.getName() + "'s gamemode was updated to " + gamemode.toString());
            } else {
                player.setGameMode(gamemode);
                player.sendMessage("§eYour gamemode was updated to " + gamemode.toString());
            }
        }
        else {
            Help(player);
        }

    }

    public void Help(Player player) {
        player.sendMessage("§cError! Correct usage:");
        player.sendMessage("§b/e tp <player>");
        player.sendMessage("§b/e tp <player> <player>");
        player.sendMessage("§b/e tp here <player>");
        player.sendMessage("§b/e tp all");
        player.sendMessage("§c/e bc <message>");
        player.sendMessage("§b/e start");
        player.sendMessage("§b/e stat " + Rank.ADMIN.getPrefix());
        player.sendMessage("§b/e fly [player]");
        player.sendMessage("§b/e gm <gamemode> [player]");
    }

    //thanks Nick
    //dont ask me to explain this i have no fucking clue
    private String[] removeCommand(String command)
    {
        String[] split = command.split(" ");

        if (split.length == 1)
        {
            return new String[0];
        }

        String[] result = new String[split.length - 1];
        System.arraycopy(split, 1, result, 0, split.length - 1);

        return result;
    }


}
