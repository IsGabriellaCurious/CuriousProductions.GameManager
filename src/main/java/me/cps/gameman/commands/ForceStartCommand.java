package me.cps.gameman.commands;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.runnables.StartRunnable;
import me.cps.root.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

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
