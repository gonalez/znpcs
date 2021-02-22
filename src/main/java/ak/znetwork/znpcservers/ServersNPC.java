package ak.znetwork.znpcservers;

import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.list.DefaultCommand;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.deserializer.ZNPCDeserializer;
import ak.znetwork.znpcservers.listeners.PlayerListeners;
import ak.znetwork.znpcservers.manager.CommandsManager;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.tasks.NPCManagerTask;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.tasks.NPCSaveTask;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.LocationSerialize;
import ak.znetwork.znpcservers.utility.MetricsLite;
import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ServersNPC extends JavaPlugin {

    public static final int MILLI_SECOND = 20;

    @Getter private static String replaceSymbol;
    @Getter private static boolean placeHolderSupport;

    @Getter private static Executor executor;
    @Getter private static ExecutorService executorService;

    @Getter private static Gson gson;

    @Getter private static File pluginFolder;

    private CommandsManager commandsManager;
    private NPCManager npcManager;

    private LinkedHashSet<ZNPCUser> npcUsers;

    private int viewDistance;
    private long startTimer;

    private File data, npcPaths;

    @Override
    public void onEnable() {
        startTimer = System.currentTimeMillis();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        pluginFolder = getDataFolder();

        npcPaths = pluginFolder.toPath().resolve("paths").toFile();
        if (!npcPaths.exists()) npcPaths.mkdirs();

        data = new File(pluginFolder, "data.json");
        try {
            data.createNewFile();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "An exception occurred while trying to create file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        viewDistance = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.VIEW_DISTANCE);
        replaceSymbol = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.REPLACE_SYMBOL);

        placeHolderSupport = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        gson = new GsonBuilder().
                registerTypeAdapter(Location.class, new LocationSerialize()). // Add custom serializer since for Location class doesn't support
                registerTypeAdapter(ZNPC.class, new ZNPCDeserializer(this))
                .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        npcUsers = new LinkedHashSet<>();

        npcManager = new NPCManager();

        commandsManager = new CommandsManager(this, "znpcs");
        commandsManager.addCommand(new ZNCommand(new DefaultCommand(this)));

        int pluginId = 8054;
        new MetricsLite(this, pluginId);

        // Load entity type cache
        for (NPCType npcType : NPCType.values()) {
            npcType.load();
        }

        executor = r -> getServer().getScheduler().scheduleSyncDelayedTask(this, r, MILLI_SECOND * 2);
        executorService = Executors.newSingleThreadExecutor();

        // Load all NPC from data.
        executor.execute(() -> {
            System.out.println("Loading npcs...");

            long startMs = System.currentTimeMillis();
            try {
                // Load all paths...
                loadAllPaths();

                // Load all NPCs...
                List<ZNPC> npcList = gson.fromJson(Files.toString(data, Charsets.UTF_8), new TypeToken<List<ZNPC>>(){}.getType());
                if (npcList != null) {
                    getNpcManager().getNpcList().addAll(npcList);

                    System.out.println("(Loaded " + npcList.size() + " znpcs in " + NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");
                }
            } catch (IOException e) {
                getServer().getPluginManager().disablePlugin(this);
                throw new RuntimeException("Data could not be loaded", e);
            }

            new NPCSaveTask(this, ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.SAVE_NPCS_DELAY_SECONDS));

            // Setup netty again for online players
            Bukkit.getOnlinePlayers().forEach(ServersNPC.this::setupNetty);
        });

        // Init NPC task
        new NPCManagerTask(this);

        // Register listeners
        new PlayerListeners(this);
    }

    @Override
    public void onDisable() {
        // Save NPC on database
        saveAllNPC();

        // Delete all npc for viewers
        removeAllViewers();
    }

    /**
     * Saves all NPC in database.
     */
    public void saveAllNPC() {
        if (System.currentTimeMillis() - startTimer <= (1000) * 5) return;

        try (FileWriter writer = new FileWriter(data)) {
            gson.toJson(getNpcManager().getNpcList().stream().filter(ZNPC::isSave).collect(Collectors.toList()), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all paths.
     */
    public void loadAllPaths() {
        File[] listFiles = getNpcPaths().listFiles();
        if (listFiles == null) return;

        for (File file : listFiles) {
            // Check if file is path
            if (file.getName().endsWith(".path")) {
                try {
                    getNpcManager().getNpcPaths().add(new ZNPCPathReader(file));
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, String.format("The path %s could not be loaded", file.getName()));
                }
            }
        }
    }

    /**
     * Deletes all NPC for npc viewers.
     */
    public void removeAllViewers() {
        getNpcManager().getNpcList().forEach(ZNPC::deleteViewers);
    }

    /**
     * Setup netty for player.
     *
     * @param player The player.
     */
    public void setupNetty(Player player) {
        try {
            getNpcUsers().add(new ZNPCUser(this, player));
        } catch (Exception exception) {
            throw new RuntimeException("An exception occurred while trying to setup netty for player " + player.getName(), exception);
        }
    }

    /**
     * Creates a new npc.
     *
     * @param id The npc identifier.
     * @return   {@code true} If the npc was created correctly.
     */
    public boolean createNPC(int id, Location location, String skin, String holo_lines, boolean save) {
        if (getNpcManager().getNpcList().stream().anyMatch(npc -> npc.getId() == id)) return false;

        ZNPCSkin skinFetch = ZNPCSkin.forName(skin);
        return getNpcManager().getNpcList().add(new ZNPC(this, id, holo_lines, skinFetch.getValue(), skinFetch.getSignature(), location, NPCType.PLAYER, new EnumMap<>(NPCItemSlot.class), save));
    }

    /**
     * Deletes a npc.
     *
     * @param npcID         The npc ID.
     * @return {@code true} If the npc was removed successfully.
     */
    public void deleteNPC(int npcID) {
        ZNPC npc = npcManager.getNpcList().stream().filter(npc1 -> npc1.getId() == npcID).findFirst().orElse(null);

        if (npc == null)
            return;

        getNpcManager().getNpcList().remove(npc);

        npc.deleteViewers();
    }

    /**
     * Sends a player to a bungee server.
     *
     * @param player The player to send to the server.
     * @param server The server name.
     */
    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }
}
