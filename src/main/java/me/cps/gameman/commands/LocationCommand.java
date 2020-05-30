package me.cps.gameman.commands;

import me.cps.gameman.GameManager;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Location Command
 *
 * A way to get your location. This is mainly for debugging.
 *
 * @author  Gabriella Hotten
 * @since   2020-05-17
 */
public class LocationCommand extends cpsCommand<GameManager> {

    public LocationCommand(GameManager mod) {
        super("myloc", Rank.DEVELOPER, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        caller.sendMessage(caller.getLocation().toString());
    }
}
