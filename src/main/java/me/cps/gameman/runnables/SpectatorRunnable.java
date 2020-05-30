package me.cps.gameman.runnables;

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.root.util.ActionBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Curious Productions Game Manager
 * Spectator Runnable
 *
 * Shows a nice little action bar for all spectators.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-10
 */
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
                ActionBar.send(p, "§dWant §fmore points§d? §cDON'T QUIT §dunitl the game ends!"); //TODO fully intergrate "Participants"
            }
        }


        time +=1;
    }
}
