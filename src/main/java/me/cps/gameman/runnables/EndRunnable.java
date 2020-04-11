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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class EndRunnable extends BukkitRunnable {

    Player pWinner;
    ChatColor cWinner;

    public EndRunnable(Player pWinner, ChatColor cWinner) {
        this.pWinner = pWinner;
        this.cWinner = cWinner;
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
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            Rank rank = Rank.getRank(p.getUniqueId());
            if (Rank.hasRank(p.getUniqueId(), Rank.DONATOR)) {
                p.sendMessage(" ");
                p.sendMessage("§6Thanks for playing, " + rank.getColor() + p.getName());
                p.sendMessage(" ");
                p.sendMessage("§bWant to play §f" + GameManager.getInstance().getCurrentGame().getGameName() + " §bwith just you and your friends?");
                p.sendMessage("§cWell too bad, you can't §ryet."); //TODO lmao make it so you can!!
                p.sendMessage(" ");
            } else {
                p.sendMessage(" ");
                p.sendMessage("§6Thanks for playing, " + rank.getColor() + p.getName());
                p.sendMessage(" ");
                p.sendMessage("§aWant to join §d§lFULL LOBBIES§a and other §5amazing perks§a?");
                p.sendMessage("§eBuy a donator rank and help support the server!");
                p.sendMessage(" ");
            }
            p.sendMessage("§7§lGoing to the Hub in 10 seconds...");
        }
        try {
            TimeUnit.SECONDS.sleep(10);
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
        StatManager.getInstance().pushStats(true); //is actually giving the stat
    }
}
