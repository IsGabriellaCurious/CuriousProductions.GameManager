package me.cps.gameman.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.cps.root.util.cpsModule;
import me.cps.root.util.map.GameMapBase;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Curious Productions Game Manager
 * World Manager
 *
 * Manages the world used by the game.
 * TODO: this needs to be finished, its not ready for use. For now, all games must sort out their own worlds (which they do)
 *
 * @author  Gabriella Hotten
 * @version 1.0-ALPHA
 * @since   2020-05-24
 */
public class WorldManager extends cpsModule {

    private ArrayList<String> maps = new ArrayList<>();
    private GameMapBase gameMapBase;

    public WorldManager(JavaPlugin plugin) {
        super("World Manager", plugin, "1.0-alpha", true);

        File file = new File("maps/maps.yaml");
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            gameMapBase = objectMapper.readValue(file, GameMapBase.class);
        } catch (Exception e) {
            e.printStackTrace();
            gameMapBase = null;
        }

        assert gameMapBase != null;
        maps.addAll(gameMapBase.getMaps());

        //prepare to copy a world
        File currentDir;
        File toCopy;
        World world;

        //providing there is no error
        if (maps.size() != 0) {
            //get a random map from the arraylist and copy it
            int rand = (int) (Math.random() * maps.size());
            String worldName = maps.get(rand);
            toCopy = new File("maps/" + worldName);
            currentDir = new File(worldName);
            currentDir.mkdir(); //pray to jesus it returns true :P
            try {
                FileUtils.copyDirectory(toCopy, currentDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            world = getPlugin().getServer().createWorld(new WorldCreator(worldName));
        } else {
            world = null;
        }

        //world shit
    }
}
