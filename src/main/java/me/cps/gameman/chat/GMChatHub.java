package me.cps.gameman.chat;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import me.cps.gameman.GameManager;
import me.cps.root.util.Rank;
import me.cps.root.chat.AnnouncementHandler;
import me.cps.root.chat.commands.AnnounceCommand;
import me.cps.root.util.cpsModule;
import me.cps.root.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * Curious Productions Game Manager
 * Game Manager Chat Hub
 *
 * A copy of Chat Hub made for games.
 *
 * @author  Gabriella Hotten
 * @version 1.2
 * @since   2020-04-09
 */
public class GMChatHub extends cpsModule {

    private static GMChatHub instance;
    private boolean displayPoints; //assuming the stat is called "Points"
    public static ArrayList<Player> staffChatDisabled = new ArrayList<>();
    public boolean chatEnabled = true;

    public GMChatHub(JavaPlugin plugin, boolean displayPoints) {
        super("[GM] Chat Hub", plugin, "1.2", false);
        instance = this;
        this.displayPoints = displayPoints;
        registerSelf();
        CloudNetDriver.getInstance().getEventManager().registerListener(new AnnouncementHandler());
        registerCommand(new AnnounceCommand(null));
    }

    public static GMChatHub getInstance() {
        return instance;
    }

    public void toggleChat(boolean result, boolean bc) {
        if (result) {
            chatEnabled = true;
            if (bc)
                Message.broadcast("§aGame Chat is now enabled.");
        } else {
            chatEnabled = false;
            if (bc)
                Message.broadcast("§cGame Chat is now disabled.");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        if (staffChatDisabled.contains(event.getPlayer())) {
            event.getPlayer().sendMessage("§cYou can't talk whilst you have game chat disabled!");
            return;
        }

        if (!chatEnabled) {
            event.getPlayer().sendMessage("§cGame Chat is currently disabled.");
            return;
        }

        String full = "";
        if (displayPoints)
            if (!GameManager.getInstance().getSpectators().contains(event.getPlayer()))
                GameManager.getInstance().getCurrentGame().chatMessage(event.getPlayer(), event.getMessage(), true);
            else
                full = "§7[SPECTATOR] " + Rank.getRank(event.getPlayer().getUniqueId()).getColor() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
        else
            if (!GameManager.getInstance().getSpectators().contains(event.getPlayer()))
                GameManager.getInstance().getCurrentGame().chatMessage(event.getPlayer(), event.getMessage(), false);
            else
                full = "§7[SPECTATOR] " + Rank.getRank(event.getPlayer().getUniqueId()).getPrefix() + event.getPlayer().getDisplayName() + " " + ChatColor.WHITE + event.getMessage();
        if (!full.equalsIgnoreCase(""))
            Bukkit.broadcastMessage(full);
    }




}
