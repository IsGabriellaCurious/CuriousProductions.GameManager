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

import java.sql.Connection;
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

    private boolean levels;
    private double levelExponent;
    private int levelBaseXP;
    private boolean levelHotbar;
    private UUID topPlayer;

    private String name;

    public StatManager(JavaPlugin plugin, boolean levels, double levelExponent, int levelBaseXP, boolean levelHotbar) {
        super("[GM] Stat Manager", plugin, "1.1", true);
        instance = this;
        registerSelf();
        this.levels = levels;
        this.levelExponent = levelExponent;
        this.levelBaseXP = levelBaseXP;
        this.levelHotbar = levelHotbar;
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
    public UUID getTopPlayerUUID() {
        return topPlayer;
    }

    public void setTopPlayerUUID(UUID topPlayer) {
        this.topPlayer = topPlayer;
    }

    public PlayerStat getPlayerStat(Player player) {
        return getStat().get(player);
    }

    public void registerStat(GameStat stat) {
        getAvailableStat().put(stat.getDisplayName(), stat);
        Message.console("[STAT MANAGER] Registered new stat " + stat.getDisplayName());
    }

    public void pushStats(boolean shutdownWhenDone) {
        Message.console("[STAT MANAGER] Stat Push started...");

        if (GameManager.getInstance().isEventMode() && !GameManager.getInstance().isStats()) {
            Message.console("[STAT MANAGER] Server is in event mode, cancelling...");
            if (shutdownWhenDone)
                Bukkit.getServer().shutdown();
            return;
        }

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
            Connection connection = AccountHub.getInstance().createConnection();

            PreparedStatement statement = connection.prepareStatement("UPDATE `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "` SET " + name + "=? WHERE uuid=?");
            int next;
            if (prev == -1)
                next = amount;
            else
                next = prev + amount;
            statement.setInt(1, next);
            statement.setString(2, uuid.toString());

            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPlayer(UUID uuid) {
        try {
            Connection connection = AccountHub.getInstance().createConnection();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO `game." + GameManager.getInstance().getCurrentGame().getMysqlName() +"` (uuid) VALUE (?)");
            statement.setString(1, uuid.toString());

            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try {
            Connection connection = AccountHub.getInstance().createConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() +"` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            boolean exists = resultSet.next();
            connection.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return nullBoolean;
        }
    }

    public int getCurrentStat(UUID uuid, GameStat stat) {
        try {
            Connection connection = AccountHub.getInstance().createConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT `" + stat.getMysqlColumnName() + "` FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "` WHERE uuid=?");
            statement.setString(1, uuid.toString());
            statement.executeQuery();

            ResultSet resultSet = statement.getResultSet();

            int num = 0;
            if (resultSet.next())
                num = resultSet.getInt(stat.getMysqlColumnName());
            else
                num = -1;
            connection.close();
            return num;


        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public UUID getTopLevel() {
        UUID top = null;
        int currentHighest = 0;
        ArrayList<UUID> sameLevelHighest = new ArrayList<>();
        try {
            Connection connection = AccountHub.getInstance().createConnection();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "`");
            statement.executeQuery();

            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {

                int lvl = resultSet.getInt("level");

                if (lvl == currentHighest) {
                    sameLevelHighest.add(UUID.fromString(resultSet.getString("uuid")));
                    if (!sameLevelHighest.contains(top))
                        sameLevelHighest.add(top);

                    continue;
                }

                if (lvl > currentHighest) {
                    top = UUID.fromString(resultSet.getString("uuid"));
                    currentHighest = lvl;
                    sameLevelHighest.clear();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (sameLevelHighest.size() != 0) {
            int xpHighest = 0;
            try {
                Connection connection = AccountHub.getInstance().createConnection();

                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `game." + GameManager.getInstance().getCurrentGame().getMysqlName() + "`");
                statement.executeQuery();

                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {

                    if (sameLevelHighest.contains(UUID.fromString(resultSet.getString("uuid")))) {
                        int xp = resultSet.getInt("xp");

                        if (xp > xpHighest) {
                            xpHighest = xp;
                            top = UUID.fromString(resultSet.getString("uuid"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        Message.console("" + top + " is the top player!");
        return top;
    }

    public int xpRequired(int nextLevel) {
        return (int) Math.floor(levelBaseXP * Math.pow(nextLevel, levelExponent));
    }

    public boolean canLevelUp(UUID uuid, int currentLevel, int currentXp) {
        boolean result;
        Message.console("Is " + currentXp + " greather than or equal to " + xpRequired(currentLevel + 1) + "?");
        if (currentXp >= xpRequired(currentLevel + 1)) {
            sqlSetStat(uuid, "level", -1, currentLevel + 1);
            sqlSetStat(uuid, "xp", -1, 0);
            result = true;
        } else {
            result = false;
        }
        Message.console("" + result);
        return result;
    }

    public int percentTilNextLvl(Player player) {
        UUID uuid = player.getUniqueId();
        int level = getPlayerStat(player).forceGetStat(getAvailableStat().get("level"));
        int xp = getPlayerStat(player).forceGetStat(getAvailableStat().get("xp"));

        int percent = (int) Math.floor(xp / xpRequired(level++));

        return percent * 100;
    }

    public void setXpBar(Player player) {
        int percent = percentTilNextLvl(player);
        int level = getPlayerStat(player).forceGetStat(getAvailableStat().get("level"));

        player.setLevel(level);
        player.setExp(percent / 100);
    }

    public boolean isLevels() {
        return levels;
    }

    public boolean isLevelHotbar() {
        return levelHotbar;
    }
}
