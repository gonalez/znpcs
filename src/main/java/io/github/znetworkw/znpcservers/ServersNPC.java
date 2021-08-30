package io.github.znetworkw.znpcservers;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.commands.list.DefaultCommand;
import io.github.znetworkw.znpcservers.configuration.Configuration;
import io.github.znetworkw.znpcservers.listeners.InventoryListener;
import io.github.znetworkw.znpcservers.listeners.PlayerListener;
import io.github.znetworkw.znpcservers.npc.NPCModel;
import io.github.znetworkw.znpcservers.utility.BungeeUtils;
import io.github.znetworkw.znpcservers.utility.itemstack.ItemStackSerializer;
import io.github.znetworkw.znpcservers.utility.location.ZLocation;
import io.github.znetworkw.znpcservers.npc.task.NPCManagerTask;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.npc.NPCType;
import io.github.znetworkw.znpcservers.npc.task.NPCSaveTask;
import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.user.ZUser;
import io.github.znetworkw.znpcservers.utility.MetricsLite;
import io.github.znetworkw.znpcservers.utility.SchedulerUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collections;

import static io.github.znetworkw.znpcservers.npc.NPCPath.AbstractTypeWriter.*;

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
        ImmutableList<File> files =
            ImmutableList.of(PLUGIN_FOLDER, PATH_FOLDER);
        for (File file : files) {
            file.mkdirs();
        }
    }

    /**
     * The plugin metrics id.
     */
    private static final int PLUGIN_ID = 8054;

    /**
     * Creates a new Gson instance with custom type adapters.
     */
    public final static Gson GSON = new GsonBuilder()
        .registerTypeAdapter(ZLocation.class, ZLocation.SERIALIZER)
        .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

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
        loadAllPaths();

        // register BungeeCord channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // setup metrics
        new MetricsLite(this, PLUGIN_ID);

        // create commands
        new DefaultCommand();

        // utils
        SCHEDULER = new SchedulerUtils(this);
        BUNGEE_UTILS = new BungeeUtils(this);

        // setup users again for online players
        Bukkit.getOnlinePlayers().forEach(ZUser::find);

        // init NPC task
        new NPCManagerTask(this);
        new NPCSaveTask(this, ConfigurationConstants.SAVE_DELAY);

        // register listeners
        new PlayerListener(this);
        new InventoryListener(this);
    }

    @Override
    public void onDisable() {
        Configuration.SAVE_CONFIGURATIONS.forEach(Configuration::save);
        Bukkit.getOnlinePlayers().forEach(ZUser::unregister);
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
            if (file.getName().endsWith(".path")) { // check if file is a path
                AbstractTypeWriter abstractTypeWriter = AbstractTypeWriter.forFile(file, TypeWriter.MOVEMENT);
                // load path..
                abstractTypeWriter.load();
            }
        }
    }

    /**
     * Creates a new npc.
     *
     * @param id       The npc identifier.
     * @param npcType  The npc entity type.
     * @param location The npc location.
     * @param name     The npc skin name.
     * @return The created NPC.
     */
    public static NPC createNPC(int id, NPCType npcType, Location location, String name) {
        NPC find = NPC.find(id);
        if (find != null) {
            return find;
        }
        NPCModel pojo = new NPCModel(id)
            .withHologramLines(Collections.singletonList(name))
            .withLocation(new ZLocation(location))
            .withNpcType(npcType);
        ConfigurationConstants.NPC_LIST.add(pojo);
        return new NPC(pojo, true);
    }

    /**
     * Deletes a npc with the given id.
     *
     * @param npcID The npc ID.
     */
    public static void deleteNPC(int npcID) {
        NPC npc = NPC.find(npcID);
        if (npc == null) {
            throw new IllegalStateException("can't find npc:  " + npcID);
        }
        NPC.unregister(npcID);
        ConfigurationConstants.NPC_LIST.remove(npc.getNpcPojo());
    }
}
