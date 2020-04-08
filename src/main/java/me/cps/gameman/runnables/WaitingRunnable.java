package me.cps.gameman.runnables;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.root.util.ActionBar;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitingRunnable extends BukkitRunnable {

    double time = 0;
    boolean rest = false;

    @Override
    public void run() {
        if (GameManager.getInstance().getLivePlayers().size() >= GameManager.getInstance().getCurrentGame().getMinPlayers()) {
            cancel();
            return;
        }

        if (rest) {
            time = 0;
            rest = false;
        }

        time += 0.5;

        if (time <= 4.5) {
            int required = GameManager.getInstance().getCurrentGame().getMinPlayers() - GameManager.getInstance().getLivePlayers().size();
            if (required == 1)
                ActionBar.all("§eWaiting for §b" + required + " §eplayer...");
            else
                ActionBar.all("§eWaiting for §b" + required + " §eplayers...");
            return;
        }
        if (time == 5 || time == 6 || time == 6.5) {
            ActionBar.all("§dYou are playing §f§l" + GameManager.getInstance().getCurrentGame().getGameName());
            return;
        }
        if (time == 7 || time == 8 || time == 8.5) {
            ActionBar.all("§don §f§lCPS");
            return;
        }

        if (time == 9) {
            rest = true;
            return;
        }
    }
}
