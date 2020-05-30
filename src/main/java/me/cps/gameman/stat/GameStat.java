package me.cps.gameman.stat;

import me.cps.gameman.cpsGame;
import org.bukkit.Material;

/**
 * Curious Productions Game Manager
 * Stat Manager - Game Start Extension
 *
 * Extension for all game stats. Must be registered inside the game class.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-07
 */
public abstract class GameStat<Game extends cpsGame> {

    private String displayName;
    private String mysqlColumnName;
    private String description;
    private Material icon;

    private Game game;

    public GameStat(Game game, String displayName, String mysqlColumnName, String description, Material icon) {
        this.game = game;
        this.displayName = displayName;
        this.mysqlColumnName = mysqlColumnName;
        this.description = description;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMysqlColumnName() {
        return mysqlColumnName;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public Game getGame() {
        return game;
    }
}
