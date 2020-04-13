package me.cps.gameman.commands;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.root.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.scoreboard.ScoreboardCentre;
import me.cps.root.staff.StaffHub;
import org.bukkit.entity.Player;

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
