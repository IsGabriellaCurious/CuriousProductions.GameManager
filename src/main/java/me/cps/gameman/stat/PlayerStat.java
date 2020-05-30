package me.cps.gameman.stat;

import me.cps.gameman.GameManager;
import me.cps.root.util.Message;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Curious Productions Game Manager
 * Stat Manager - Player Stat Base
 *
 * Holds all the info about a player's stats.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-09
 */
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

    @Deprecated
    public void awardStat(GameStat stat, int amount) {
        if (hashGetEarnedStat().containsKey(stat)) {
            int i = amount + getEarnedStat().get(stat);
            hashGetEarnedStat().remove(stat);
            hashGetEarnedStat().put(stat, i);
            hashGetCurrentStat().put(stat, i);
        } else
            hashGetEarnedStat().put(stat, amount);
        Message.console("awared " + amount +" of " + stat.getDisplayName());
    }

    public void awardStat(String gameStat, int amount) {
        GameStat stat = statManager.getAvailableStat().get(gameStat);
        if (hashGetEarnedStat().containsKey(stat)) {
            int i = amount + getEarnedStat().get(stat);
            hashGetEarnedStat().remove(stat);
            hashGetEarnedStat().put(stat, i);
            hashGetCurrentStat().put(stat, i);
        } else
            hashGetEarnedStat().put(stat, amount);
        Message.console("awared " + amount +" of " + stat.getDisplayName());
    }

    public int forceGetStat(GameStat stat) {
        return StatManager.getInstance().getCurrentStat(player.getUniqueId(), stat);
    }





}
