package me.cps.gameman;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import org.bukkit.Material;
import org.bukkit.entity.Player;

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
