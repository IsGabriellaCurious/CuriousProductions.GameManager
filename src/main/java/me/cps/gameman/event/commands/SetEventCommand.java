package me.cps.gameman.event.commands;

import me.cps.gameman.GameManager;
import me.cps.gameman.GameState;
import me.cps.gameman.event.EventStaffType;
import me.cps.gameman.event.EventsManager;
import me.cps.root.util.Rank;
import me.cps.root.command.cpsCommand;
import me.cps.root.scoreboard.ScoreboardCentre;
import me.cps.root.scoreboard.cpsScoreboard;
import me.cps.root.util.Message;
import me.cps.root.util.PlaySound;
import me.cps.root.util.center.Centered;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Curious Productions Game Manager
 * Events Manager - Set Event Command
 *
 * Switches your server into an event server.
 *
 * @author  Gabriella Hotten
 * @since   2020-04-28
 */
public class SetEventCommand extends cpsCommand<GameManager> {


    public SetEventCommand(GameManager mod) {
        super("setevent", Rank.JUNIORADMIN, mod);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (GameManager.getInstance().isEventMode()) {
            if (EventsManager.host == null) {
                EventsManager.host = caller;
                caller.sendMessage("§aYou are now the host of this event!");
                Message.broadcast("§a" + caller.getName() + " is now hosting this event!");
            } else {
                Rank current = Rank.getRank(EventsManager.host.getUniqueId());
                Rank my = Rank.getRank(caller.getUniqueId());
                if (Rank.hasRank(caller.getUniqueId(), current)) {
                    caller.sendMessage("§aYou are now the host of this event, after stealing it from " + EventsManager.host.getName());
                    Message.broadcast("§a" + caller.getName() +" is now hosting this event after stealing it form " + EventsManager.host.getName());
                    EventsManager.host = caller;
                }
            }
            return;
        }

        if (GameManager.getInstance().getGameState() != GameState.WAITING) {
            caller.sendMessage("§cYou cannot active Event Mode during a game!");
            return;
        }

        EventsManager eventsManager = new EventsManager(GameManager.getInstance().getPlugin(), GameManager.getInstance());

        EventsManager.host = caller;
        GameManager.getInstance().setEventMode(true);
        GameManager.getInstance().getEventStaff().put(caller, EventStaffType.HOST);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage("§a" + caller.getName() + " has enabled §a§lEVENT MODE!");
            Centered.send(p, "§8»§m--------------------§r§8«");
            Centered.send(p, "§d§lCPS EVENT");
            p.sendMessage(" ");
            Centered.send(p, "Welcome to this event, today we are playing: §e" + GameManager.getInstance().getCurrentGame().getGameName());
            Centered.send(p, "Event Host §8» §e" + (EventsManager.host == null ? "No one!" : EventsManager.host.getName()));
            Centered.send(p, "§8»§m--------------------§r§8«");;
            PlaySound.all(Sound.NOTE_PLING, 100, 2);

            cpsScoreboard s = ScoreboardCentre.getInstance().getScoreboards().get(p);
            s.clearCacheOnNext();
        }
        caller.sendMessage("§aYou started an event server!");
    }
}
