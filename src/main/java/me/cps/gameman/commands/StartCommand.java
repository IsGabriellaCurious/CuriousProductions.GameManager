package me.cps.gameman.commands;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.root.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

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
        Message.broadcast("§a§lThe timer has been shortened to 10 seconds.");
    }
}
