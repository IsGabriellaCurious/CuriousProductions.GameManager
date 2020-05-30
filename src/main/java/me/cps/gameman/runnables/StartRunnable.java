package me.cps.gameman.runnables;

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.events.GameStartEvent;
import me.cps.root.scoreboard.ScoreboardCentre;
import me.cps.root.util.ActionBar;
import me.cps.root.util.Message;
import me.cps.root.util.PlaySound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Curious Productions Game Manager
 * Start/Countdown Runnable
 *
 * Runs the countdown, then fires all the needed methods and events to start a game
 *
 * @author  Gabriella Hotten
 * @since   2020-04-08
 */
public class StartRunnable extends BukkitRunnable {

    String message;
    boolean cancel;

    boolean almost;
    boolean full;

    public StartRunnable(int timet, String message) {
        GameManager.getInstance().gameStartTimer = timet;
        this.message = message;
        cancel = false;
        almost = false;
        full = false;
    }

    @Override
    public void run() {

        if (GameManager.getInstance().getGameState() == GameState.LIVE) {
            cancel();
            return;
        }

        if (GameManager.timerPaused) {
            ActionBar.all("§cThe timer has been paused.");
            return;
        }

        if (!GameManager.forceStart) {
            if (GameManager.getInstance().getCurrentGame().getMinPlayers() > GameManager.getInstance().getLivePlayers().size()) {
                ActionBar.all("§c§lCanceled! Waiting for players...");
                Message.broadcast("§c§lCanceled! Waiting for players...");
                PlaySound.all(Sound.ENDERDRAGON_GROWL, 100, 1);
                almost = false;
                full = false;
                GameManager.getInstance().startWaiting();
                cancel();
                return;
            }

            if (GameManager.getInstance().getLivePlayers().size() >= GameManager.getInstance().getCurrentGame().getMaxPlayers()-3 && !(GameManager.getInstance().getLivePlayers().size() >= GameManager.getInstance().getCurrentGame().getMaxPlayers())) {
                if (!almost) {
                    Message.broadcast("§aWe almost have a full server! Shortening timer to 30 seconds!");
                    GameManager.getInstance().gameStartTimer = 31;
                    almost = true;
                }
            }

            if (GameManager.getInstance().getLivePlayers().size() >= GameManager.getInstance().getCurrentGame().getMaxPlayers()) {
                if (!full) {
                    Message.broadcast("§aWe have a full server! Starting in 10 seconds!");
                    GameManager.getInstance().gameStartTimer = 100; //todo change back
                    full = true;
                }

            }
        }

        GameManager.getInstance().gameStartTimer -= 1;

        if (GameManager.getInstance().gameStartTimer == 0) {
            Bukkit.getServer().getScheduler().runTask(GameManager.getInstance().getPlugin(), () -> {
                Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent());
                GameManager.getInstance().setGameState(GameState.LIVE);
                GameManager.getInstance().startSpecRun();
                ScoreboardCentre.getInstance().resetCacheAll();
            });
            boolean show = GameManager.getInstance().getCurrentGame().isShowTeamColour();
            boolean custom = !GameManager.getInstance().getCurrentGame().getCustomPrefix().equals("");
            if (show)
                ScoreboardCentre.getInstance().updatePrefixes(ScoreboardCentre.UpdateAction.UPDATE);
            for (Player p : GameManager.getInstance().getLivePlayers()) {
                if (show && !custom)
                    ScoreboardCentre.getInstance().setPrefix(p, GameManager.getInstance().getPlayerTeams().get(p) + "", ScoreboardCentre.UpdateAction.UPDATE);
                else if (show && custom)
                    ScoreboardCentre.getInstance().setPrefix(p, GameManager.getInstance().getCurrentGame().getCustomPrefix(), ScoreboardCentre.UpdateAction.UPDATE);
                p.setHealth(20);
                p.setFoodLevel(20);
                if (!GameManager.getInstance().isStats())
                    p.sendMessage("§c§lNote: §cYour stats will not be effected this game.");
            }
            cancel();
            return;
        }

        if (GameManager.getInstance().gameStartTimer > 5)
            ActionBar.all(ChatColor.YELLOW + "" + ChatColor.BOLD + message + " " + ChatColor.GREEN + GameManager.getInstance().gameStartTimer + "" + ChatColor.YELLOW + ChatColor.BOLD + " seconds");
        else
            if (GameManager.getInstance().gameStartTimer <= 5)
                PlaySound.all(Sound.NOTE_STICKS, 100, 1);
            if (GameManager.getInstance().gameStartTimer <= 3)
                PlaySound.all(Sound.ORB_PICKUP, 100, 1);

            if (GameManager.getInstance().gameStartTimer == 1)
                ActionBar.all(ChatColor.GREEN + "" + ChatColor.BOLD + message + " " + ChatColor.RED + GameManager.getInstance().gameStartTimer + "" + ChatColor.GREEN + ChatColor.BOLD + " second");
            else if (GameManager.getInstance().gameStartTimer <=5)
                ActionBar.all(ChatColor.GREEN + "" + ChatColor.BOLD + message + " " + ChatColor.RED + GameManager.getInstance().gameStartTimer + "" + ChatColor.GREEN + ChatColor.BOLD + " seconds");
    }
}
