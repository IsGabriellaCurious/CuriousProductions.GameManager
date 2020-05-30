package me.cps.gameman.commands;

import me.cps.gameman.GameManager;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.scoreboard.ScoreboardCentre;
import me.cps.root.staff.StaffHub;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Staff Scoreboard Command
 *
 * Will show the custom staff mode scoreboard to the toggler.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-12
 */
public class StaffScoreboardCommand extends cpsCommand<GameManager> {

    public StaffScoreboardCommand(GameManager mod) {
        super("staffscore", Rank.HELPER, mod, "staffsb");
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (!StaffHub.getInstance().getInStaffMode().contains(caller)) {
            caller.sendMessage("§cYou must be in Staff Mode to do this!");
            return;
        }

        if (getPlugin().getStaffscore().contains(caller)) {
            ScoreboardCentre.getInstance().resetCache(caller);
            getPlugin().getStaffscore().remove(caller);
            caller.sendMessage(StaffHub.prefix + "§cStaff Scoreboard disabled.");
        } else {
            ScoreboardCentre.getInstance().resetCache(caller);
            getPlugin().getStaffscore().add(caller);
            caller.sendMessage(StaffHub.prefix + "§aStaff Scoreboard enabled.");
        }
    }
}
