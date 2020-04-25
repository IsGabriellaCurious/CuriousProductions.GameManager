package me.cps.gameman.chat;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.stat.StatManager;
import me.cps.root.Rank;
import me.cps.root.account.AccountHub;
import me.cps.root.cpsModule;
import me.cps.root.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class GMChatHub extends cpsModule {

    private static GMChatHub instance;
    private boolean displayPoints; //assuming the stat is called "Points"
    public static ArrayList<Player> staffChatDisabled = new ArrayList<>();
    public boolean chatEnabled = true;

    public GMChatHub(JavaPlugin plugin, boolean displayPoints) {
        super("[GM] Chat Hub", plugin, "1.2", false);
        instance = this;
        this.displayPoints = displayPoints;
        registerSelf();
    }

    public static GMChatHub getInstance() {
        return instance;
    }

    public void toggleChat(boolean result, boolean bc) {
        if (result) {
            chatEnabled = true;
            if (bc)
                Message.broadcast("§aGame Chat is now enabled.");
        } else {
            chatEnabled = false;
            if (bc)
                Message.broadcast("§cGame Chat is now disabled.");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (staffChatDisabled.contains(event.getPlayer())) {
            event.getPlayer().sendMessage("§cYou can't talk whilst you have game chat disabled!");
            return;
        }

        if (!chatEnabled) {
            event.getPlayer().sendMessage("§cGame Chat is currently disabled.");
            return;
        }

        String full = "";
        if (displayPoints)
            if (!GameManager.getInstance().getSpectators().contains(event.getPlayer()))
                full = "§6[" + StatManager.getInstance().getPlayerStat(event.getPlayer()).getStat(StatManager.getInstance().getAvailableStat().get("Points")) + "] "
                    + AccountHub.getInstance().getPlayers().get(event.getPlayer().getUniqueId()).getPrefix() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
            else
                full = "§6[" + StatManager.getInstance().getPlayerStat(event.getPlayer()).getStat(StatManager.getInstance().getAvailableStat().get("Points")) + "] "
                        + "§7[SPEC] " + Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
        else
            if (!GameManager.getInstance().getSpectators().contains(event.getPlayer()))
                full = "" + Rank.getRank(event.getPlayer().getUniqueId()).getPrefix() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
            else
                full = "§7[SPEC] " + Rank.getRank(event.getPlayer().getUniqueId()).getPrefix() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
        Bukkit.broadcastMessage(full);
    }




}
