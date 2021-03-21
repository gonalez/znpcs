package ak.znetwork.znpcservers;

import ak.znetwork.znpcservers.commands.list.DefaultCommand;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.listeners.PlayerListeners;
import ak.znetwork.znpcservers.utility.BungeeUtils;
import ak.znetwork.znpcservers.utility.location.ZLocation;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.tasks.NPCManagerTask;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.tasks.NPCSaveTask;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.location.ZLocationSerialize;
import ak.znetwork.znpcservers.utility.MetricsLite;
import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;

import ak.znetwork.znpcservers.utility.SchedulerUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ak.znetwork.znpcservers.npc.path.ZNPCPathImpl.AbstractZNPCPath.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ServersNPC extends JavaPlugin {

    /**
     * The plugin name.
     */
    private static final String PLUGIN_NAME = "ServersNPC";

    /**
     * The plugin folder.
     */
    public static final File PLUGIN_FOLDER = new File("plugins/" + PLUGIN_NAME);

    /**
     * The path folder.
     */
    public static final File PATH_FOLDER = new File("plugins/" + PLUGIN_NAME + "/paths");

    static {
        // Create the folder if it doesn't exist.
        PLUGIN_FOLDER.mkdirs();
        PATH_FOLDER.mkdirs();
    }

    /**
     * The plugin metrics id.
     */
    private static final int PLUGIN_ID = 8054;

    /**
     * Creates a new Gson instance with
     * custom type adapters.
     */
    public final static Gson GSON = new GsonBuilder().
            registerTypeAdapter(ZLocation.class, new ZLocationSerialize()).
            excludeFieldsWithoutExposeAnnotation().
                    setPrettyPrinting().
                    create();

    /**
     * The scheduler instance.
     */
    public static SchedulerUtils SCHEDULER;

    /**
     * The bungee utils.
     */
    public static BungeeUtils BUNGEE_UTILS;

    @Override
    public void onEnable() {
        // Load entity type cache
        for (NPCType npcType : NPCType.values()) {
            npcType.load();
        }

        // Load paths
        loadAllPaths();

        // Register BungeeCord channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Setup metrics
        new MetricsLite(this, PLUGIN_ID);

        // Register commands
        new DefaultCommand("znpcs");

        // Default executor
        SCHEDULER = new SchedulerUtils(this);

        // Bungee Utils
        BUNGEE_UTILS = new BungeeUtils(this);

        // Setup netty again for online players
        Bukkit.getOnlinePlayers().forEach(ZNPCUser::registerOrGet);

        // Init NPC task
        new NPCManagerTask(this);
        new NPCSaveTask(this, ConfigTypes.SAVE_DELAY);

        // Register listeners
        new PlayerListeners(this);
    }

    @Override
    public void onDisable() {
        // Save configurations
        ConfigManager.getConfigurations().forEach(ZNConfig::save);

        // Delete all npc for viewers
        removeAllViewers();
    }

    /**
     * Loads all npc paths.
     */
    public void loadAllPaths() {
        File[] listFiles = PATH_FOLDER.listFiles();
        if (listFiles == null)
            return;

        for (File file : listFiles) {
            // Check if file is path
            if (file.getName().endsWith(".path")) {
                ZNPCMovementPath pathAbstract = new ZNPCMovementPath(file);
                // Load path..
                pathAbstract.load();
            }
        }
    }

    /**
     * Deletes all NPC for viewers.
     */
    public void removeAllViewers() {
        ConfigTypes.NPC_LIST.forEach(ZNPC::deleteViewers);
    }

    /**
     * Creates a new npc.
     *
     * @param id       The npc identifier.
     * @param npcType  The npc entity type.
     * @param location The npc location.
     * @param name     The npc skin name.
     * @return         The created zNPC.
     */
    public static ZNPC createNPC(int id, NPCType npcType, Location location, String name) {
        if (ConfigTypes.NPC_LIST.stream().anyMatch(npc -> npc.getId() == id))
            return null;

        ZNPC znpc = new ZNPC(id, name, ZNPCSkin.forName(name), new ZLocation(location), npcType);
        ConfigTypes.NPC_LIST.add(znpc);

        return znpc;
    }

    /**
     * Deletes a npc.
     *
     * @param npcID The npc ID.
     */
    public static void deleteNPC(int npcID) {
        ZNPC npc = ConfigTypes.NPC_LIST.stream().filter(npc1 -> npc1.getId() == npcID).findFirst().orElse(null);

        if (npc == null)
            return;

        ConfigTypes.NPC_LIST.remove(npc);

        npc.deleteViewers();
    }
}
