package me.cps.gameman.commands;

import me.cps.gameman.GameManager;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Pause Timer Command
 *
 * Pauses the countdown timer.
 *
 * @author  Gabriella Hotten
 * @since   2020-05-08
 */
public class PauseTimerCommand extends cpsCommand<GameManager> {

    public PauseTimerCommand(GameManager mod) {
        super("pausetimer", Rank.SENIORMOD, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (GameManager.timerPaused) {
            GameManager.timerPaused = false;
            Message.broadcast("§eThe timer is continuing!");
        } else {
            GameManager.timerPaused = true;
            Message.broadcast("§eThe timer has been paused.");
        }
    }
}
