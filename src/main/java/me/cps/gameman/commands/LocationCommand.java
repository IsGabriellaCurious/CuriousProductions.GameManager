package me.cps.gameman.commands;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.root.Rank;
import me.cps.root.command.cpsCommand;
import org.bukkit.entity.Player;

public class LocationCommand extends cpsCommand<GameManager> {

    public LocationCommand(GameManager mod) {
        super("myloc", Rank.DEVELOPER, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        caller.sendMessage(caller.getLocation().toString());
    }
}
