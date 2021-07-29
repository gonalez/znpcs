package ak.znetwork.znpcservers;

import ak.znetwork.znpcservers.commands.list.DefaultCommand;
import ak.znetwork.znpcservers.configuration.Config;
import ak.znetwork.znpcservers.listeners.PlayerListener;
import ak.znetwork.znpcservers.npc.model.ZNPCPojo;
import ak.znetwork.znpcservers.utility.BungeeUtils;
import ak.znetwork.znpcservers.utility.itemstack.ItemStackSerializer;
import ak.znetwork.znpcservers.utility.location.ZLocationSerializer;
import ak.znetwork.znpcservers.utility.location.ZLocation;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.tasks.NPCManagerTask;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCType;
import ak.znetwork.znpcservers.tasks.NPCSaveTask;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.MetricsLite;
import ak.znetwork.znpcservers.npc.ZNPCSkin;
import ak.znetwork.znpcservers.utility.SchedulerUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collections;

import static ak.znetwork.znpcservers.npc.ZNPCPath.AbstractTypeWriter.*;

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
            registerTypeAdapter(ZLocation.class, new ZLocationSerializer()).
            registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer()).
            setPrettyPrinting().
            disableHtmlEscaping().
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
        new PlayerListener(this);
    }

    @Override
    public void onDisable() {
        // Save configurations
        ConfigManager.all().forEach(Config::save);

        // Unregister netty for online players
        Bukkit.getOnlinePlayers().forEach(ZNPCUser::unregister);

        // Delete all npc for viewers
        removeAllViewers();
    }

    /**
     * Loads all npc paths.
     */
    public void loadAllPaths() {
        File[] listFiles = PATH_FOLDER.listFiles();
        if (listFiles == null) {
            return;
        }

        for (File file : listFiles) {
            // Check if file is path
            if (file.getName().endsWith(".path")) {
                AbstractTypeWriter abstractTypeWriter = AbstractTypeWriter.forFile(file, TypeWriter.MOVEMENT);
                // Load path..
                abstractTypeWriter.load();
            }
        }
    }

    /**
     * Deletes all NPC for viewers.
     */
    public void removeAllViewers() {
        ZNPC.all().forEach(ZNPC::deleteViewers);
    }

    /**
     * Creates a new npc.
     *
     * @param id       The npc identifier.
     * @param npcType  The npc entity type.
     * @param location The npc location.
     * @param name     The npc skin name.
     * @return The created zNPC.
     */
    public static ZNPC createNPC(int id, ZNPCType npcType, Location location, String name) {
        final ZNPC find = ZNPC.find(id);
        if (find != null) {
            return find;
        }

        ZNPCPojo pojo = new ZNPCPojo(id, Collections.singletonList(name), ZNPCSkin.forValues(), new ZLocation(location), npcType);
        ConfigTypes.NPC_LIST.add(pojo);
        return new ZNPC(pojo);
    }

    /**
     * Deletes a npc.
     *
     * @param npcID The npc ID.
     */
    public static void deleteNPC(int npcID) {
        ZNPC npc = ZNPC.find(npcID);
        if (npc == null) {
            return;
        }

        ZNPC.unregister(npcID);
        ConfigTypes.NPC_LIST.remove(npc.getNpcPojo());
    }
}
