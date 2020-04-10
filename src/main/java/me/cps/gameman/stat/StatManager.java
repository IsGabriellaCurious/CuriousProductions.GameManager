package me.cps.gameman.stat;

/*
Hi there! Pls no stealing, unless you were given express
permission to read this. if not, fuck off :)

Copyright (c) IsGeorgeCurious 2020
*/

import me.cps.gameman.GameManager;
import me.cps.root.account.AccountHub;
import me.cps.root.cpsModule;
import me.cps.root.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatManager extends cpsModule {

    private static StatManager instance;

    private boolean nullBoolean;

    private HashMap<String, GameStat> availableStat = new HashMap<>();
    private HashMap<Player, PlayerStat> stat = new HashMap<>();

    public StatManager(JavaPlugin plugin) {
        super("[GM] Stat Manager", plugin, "1.0-alpha", true);
        instance = this;
        registerSelf();
    }

    //events

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        if (!playerExists(event.getPlayer().getUniqueId()))
            createPlayer(event.getPlayer().getUniqueId());
        PlayerStat playerStat = new PlayerStat(GameManager.getInstance(), this, event.getPlayer());
        getStat().put(event.getPlayer(), playerStat);
    }

    //setter getters

    public static StatManager getInstance() {
        return instance;
    }

    public HashMap<String, GameStat> getAvailableStat() {
        return availableStat;
    }

    private HashMap<Player, PlayerStat> getStat() {
        return stat;
    }

    //voids
    public PlayerStat getPlayerStat(Player player) {
        return getStat().get(player);
    }

    public void registerStat(GameStat stat) {
        getAvailableStat().put(stat.getDisplayName(), stat);
        Message.console("[STAT MANAGER] Registered new stat " + stat.getDisplayName());
    }

    public void pushStats(boolean shutdownWhenDone) {
        Message.console("[STAT MANAGER] Stat Push started...");


        for (PlayerStat stat : getStat().values()) {
            Message.console("[STAT MANAGER] Pushing " + stat.getPlayer().getName());
            for (Map.Entry<GameStat, Integer> entry : stat.getEarnedStat().entrySet()) {
                GameStat gameStat = entry.getKey();
                int amount = entry.getValue();

                Message.console("Adding " + amount +" of " + gameStat.getDisplayName());

                int current = getCurrentStat(stat.getPlayer().getUniqueId(), gameStat);
                sqlSetStat(stat.getPlayer().getUniqueId(), gameStat.getMysqlColumnName(), current, amount);
            }
            Message.console("[STAT MANAGER] Player done!");
        }

        Message.console("[STAT MANAGER] Push finished.");
        if (shutdownWhenDone)
            Bukkit.getServer().shutdown();
    }

    //mysql stuff

    public void sqlSetStat(UUID uuid, String name, int prev, int amount) {
        try {
            AccountHub.getInstance().openConnection();

            PreparedStatement statement = AccountHub.getInstance().getConnection().prepareStatement("UPDATE `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "` SET " + name + "=? WHERE uuid=?");
            int next = prev + amount;
            statement.setInt(1, next);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();

            AccountHub.getInstance().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(UUID uuid) {
        try {
            AccountHub.getInstance().openConnection();

            PreparedStatement statement = AccountHub.getInstance().getConnection().prepareStatement("INSERT INTO `game." + GameManager.getInstance().getCurrentGame().getMysqlName() +"` (uuid) VALUE (?)");
            statement.setString(1, uuid.toString());

            statement.executeUpdate();

            AccountHub.getInstance().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try {
            AccountHub.getInstance().openConnection();

            PreparedStatement statement = AccountHub.getInstance().getConnection().prepareStatement("SELECT * FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() +"` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            AccountHub.getInstance().getConnection().close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return nullBoolean;
        }
    }

    public int getCurrentStat(UUID uuid, GameStat stat) {
        try {
            AccountHub.getInstance().openConnection();

            PreparedStatement statement = AccountHub.getInstance().getConnection().prepareStatement("SELECT `" + stat.getMysqlColumnName() + "` FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            statement.executeQuery();

            ResultSet resultSet = statement.getResultSet();

            int num = 0;
            if (resultSet.next())
                num = resultSet.getInt(stat.getMysqlColumnName());
            else
                num = -1;
            AccountHub.getInstance().getConnection().close();
            return num;


        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
