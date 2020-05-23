package me.cps.gameman.commands;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.root.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

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
