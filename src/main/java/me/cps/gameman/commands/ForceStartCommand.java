package me.cps.gameman.commands;

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.runnables.StartRunnable;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Force Start Command
 *
 * A way to force the start countdown to begin.
 *
 * @author  Gabriella Hotten
 * @since   2020-05-08
 */
public class ForceStartCommand extends cpsCommand<GameManager> {

    public ForceStartCommand(GameManager mod) {
        super("forcestart", Rank.SENIORMOD, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (GameManager.getInstance().getGameState() != GameState.WAITING) {
            caller.sendMessage("§cThe game cannot be started right now.");
            return;
        }

        if (GameManager.forceStart) {
            caller.sendMessage("§cServer is already being forced to start.");
            return;
        }

        GameManager.getInstance().gameStartTimer = 30;
        Message.broadcast("§aServer has been force started to 30 seconds.");
        GameManager.forceStart = true;
        new StartRunnable(30, GameManager.getInstance().getCurrentGame().getStartBarMessage()).runTaskTimerAsynchronously(GameManager.getInstance().getPlugin(), 0 , 20);
    }
}
