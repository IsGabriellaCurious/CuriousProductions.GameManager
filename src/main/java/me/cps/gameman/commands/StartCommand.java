package me.cps.gameman.commands;

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Start Command
 *
 * Forces the timer to 10 seconds, providing the countdown is active.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-08
 */
public class StartCommand extends cpsCommand<GameManager> {

    public StartCommand(GameManager mod) {
        super("startgame", Rank.SENIORMOD, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (GameManager.getInstance().getGameState() != GameState.WAITING) {
            caller.sendMessage("§cThe game cannot be started right now.");
            return;
        }

        GameManager.getInstance().gameStartTimer = 10;
        Message.broadcast("§aThe timer has been shortened to 10 seconds."); //TODO make it only work during countdowns
    }
}
