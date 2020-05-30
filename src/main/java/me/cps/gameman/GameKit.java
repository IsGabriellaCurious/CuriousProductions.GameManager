package me.cps.gameman;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Game Kit Extension
 *
 * Extension for all game kits. Must be registered inside the game class.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-07
 */
public abstract class GameKit<Game extends cpsGame> {

    private Game game;
    private String name;
    private Material icon;
    private int price;

    public GameKit(Game game, String name, Material icon, int price) {
        this.game = game;
        this.name = name;
        this.icon = icon;
        this.price = price;
    }

    public abstract void giveKitItems(Player player);

    public abstract String[] getKitDesc();

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public cpsGame getGame() {
        return game;
    }

    public int getPrice() {
        return price;
    }

}
