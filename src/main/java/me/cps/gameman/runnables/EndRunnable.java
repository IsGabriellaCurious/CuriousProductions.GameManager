package me.cps.gameman.runnables;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.stat.StatManager;
import me.cps.root.Rank;
import me.cps.root.proxy.ProxyManager;
import me.cps.root.util.PlaySound;
import me.cps.root.util.center.Centered;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class EndRunnable extends BukkitRunnable {

    Player pWinner;
    ChatColor cWinner;
    int toHubSecs;

    public EndRunnable(Player pWinner, ChatColor cWinner, int toHubSecs) {
        this.pWinner = pWinner;
        this.cWinner = cWinner;
        this.toHubSecs = toHubSecs;
    }


    @Override
    public void run() {
        if (pWinner == null)
            GameManager.getInstance().getCurrentGame().announceWinner(null, cWinner);
        else if (cWinner == null)
            GameManager.getInstance().getCurrentGame().announceWinner(pWinner, null);
        else
            GameManager.getInstance().getCurrentGame().announceWinner(null, null);

        GameManager.getInstance().getCurrentGame().giveGameRewards(); //this is only awarding them in statman
        StatManager.getInstance().pushStats(false); //is actually giving the stat

        if (StatManager.getInstance().isLevels()) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                int currentLevel = StatManager.getInstance().getPlayerStat(p).forceGetStat(StatManager.getInstance().getAvailableStat().get("Level"));
                int currentXp = StatManager.getInstance().getPlayerStat(p).forceGetStat(StatManager.getInstance().getAvailableStat().get("XP"));
                if (StatManager.getInstance().canLevelUp(p.getUniqueId(), currentLevel, currentXp)) {
                    p.sendMessage("§6§k11 §aYou leveled up to §bLevel " + (currentLevel + 1) + "§a! §6§k11");
                    PlaySound.play(p, Sound.LEVEL_UP, 100f, 1f);
                }
            }
        }

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Rank rank = Rank.getRank(p.getUniqueId());
            if (Rank.hasRank(p.getUniqueId(), Rank.DONATOR)) {
                p.sendMessage(" ");
                p.sendMessage(Centered.create("§6Thanks for playing, " + rank.getColor() + p.getName()));
                p.sendMessage(" ");
                p.sendMessage(Centered.create("§bWant to play §f" + GameManager.getInstance().getCurrentGame().getGameName() + " §bwith just you and your friends?"));
                p.sendMessage(Centered.create("§cWell too bad, you can't §ryet.")); //TODO lmao make it so you can!!
                p.sendMessage(" ");
            } else {
                p.sendMessage(" ");
                p.sendMessage("§6Thanks for playing, " + rank.getColor() + p.getName());
                p.sendMessage(" ");
                p.sendMessage("§aWant to join §d§lFULL LOBBIES§a and other §5amazing perks§a?");
                p.sendMessage("§eBuy a donator rank and help support the server!");
                p.sendMessage(" ");
            }
            p.sendMessage("§7§lGoing to the Hub in " + toHubSecs + " seconds...");
            if (toHubSecs >= 60) {
                p.sendMessage("§8" + ChatColor.ITALIC + "Want to leave earlier? Run /hub");
            }
        }
        try {
            TimeUnit.SECONDS.sleep(toHubSecs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            ProxyManager.getInstance().sendToLobby(p, false);
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getServer().shutdown();
    }
}
