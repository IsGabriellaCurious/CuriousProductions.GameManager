package me.cps.gameman.runnables;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.root.util.ActionBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpectatorRunnable extends BukkitRunnable {

    boolean reset = true;
    int time = 0;

    @Override
    public void run() {
        if (GameManager.getInstance().getGameState() != GameState.LIVE) {
            cancel();
            return;
        }

        if (time == 9) {
            time = 0;
        }

        for (Player p : GameManager.getInstance().getSpectators()) {
            if (time <= 4) {
                ActionBar.send(p, "§7You are a Spectator");
            } else {
                ActionBar.send(p, "§dWant §fmore points§d? §cDON'T QUIT §dunitl the game ends!");
            }
        }


        time +=1;
    }
}
