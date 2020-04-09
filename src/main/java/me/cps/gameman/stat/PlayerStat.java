package me.cps.gameman.stat;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerStat {

    private Player player;

    private HashMap<GameStat, Integer> currentStat; //this is the stat before the game
    private HashMap<GameStat, Integer> earnedStat; //this is the stat the player will be given at the end of the game

    private GameManager gameManager;
    private StatManager statManager;

    public PlayerStat(GameManager gameManager, StatManager statManager, Player player) {
        this.gameManager = gameManager;
        this.statManager = statManager;
        this.player = player;
        currentStat = new HashMap<>();
        earnedStat = new HashMap<>();

        for (GameStat stat : statManager.getAvailableStat().values()) {
            int result = statManager.getCurrentStat(player.getUniqueId(), stat);
            hashGetCurrentStat().put(stat, result);
        }
    }

    public Player getPlayer() {
        return player;
    }

    private HashMap<GameStat, Integer> hashGetEarnedStat() {
        return earnedStat;
    }

    private HashMap<GameStat, Integer> hashGetCurrentStat() {
        return currentStat;
    }

    public int getStat(GameStat stat) {
        return hashGetCurrentStat().get(stat);
    }

    public HashMap<GameStat, Integer> getEarnedStat() {
        return earnedStat;
    }

    public void awardStat(GameStat stat, int amount) {
        hashGetEarnedStat().put(stat, amount);
    }





}
