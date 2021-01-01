/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers;

import ak.znetwork.znpcservers.cache.ClazzCache;
import ak.znetwork.znpcservers.cache.exception.ClassLoadException;
import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.list.*;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.deserializer.NPCDeserializer;
import ak.znetwork.znpcservers.listeners.PlayerListeners;
import ak.znetwork.znpcservers.manager.CommandsManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.manager.tasks.NPCTask;
import ak.znetwork.znpcservers.netty.PlayerNetty;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.tasks.NPCSaveTask;
import ak.znetwork.znpcservers.utils.JSONUtils;
import ak.znetwork.znpcservers.utils.LocationSerialize;
import ak.znetwork.znpcservers.utils.MetricsLite;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ServersNPC extends JavaPlugin {

    protected CommandsManager commandsManager;
    protected NPCManager npcManager;

    protected LinkedHashSet<PlayerNetty> playerNetties;

    private static boolean placeHolderSupport;

    private static Executor executor;

    private static Gson gson;

    private File data;

    public ZNConfig config;
    public ZNConfig messages;

    public static final int MILLI_SECOND = 20;

    private int viewDistance;

    private static String replaceSymbol;

    public long startTimer;

    @Override
    public void onEnable() {
        startTimer = System.currentTimeMillis();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        data = new File(getDataFolder() , "data.json");
        try {
            data.createNewFile();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "An exception occurred while trying to create data.json file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load configuration
        try {
            config = new ZNConfig(ZNConfigType.CONFIG, getDataFolder().toPath().resolve("config.yml"));
            messages = new ZNConfig(ZNConfigType.MESSAGES, getDataFolder().toPath().resolve("messages.yml"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        // End load config

        viewDistance = (Integer.parseInt(config.getValue(ZNConfigValue.VIEW_DISTANCE)));
        replaceSymbol = (config.getValue(ZNConfigValue.REPLACE_SYMBOL));

        gson = new GsonBuilder().
                registerTypeAdapter(Location.class, new LocationSerialize()). // Add custom serializer since for Location class doesn't support
                registerTypeAdapter(NPC.class, new NPCDeserializer())
                .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

        placeHolderSupport = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        playerNetties = new LinkedHashSet<>();

        npcManager = new NPCManager();

        commandsManager = new CommandsManager("znpcs", this);
        commandsManager.getZnCommands().add(new ZNCommand(new DefaultCommand(this)));

        int pluginId = 8054;
        new MetricsLite(this, pluginId);

        // Load reflection cache
        Arrays.stream(ClazzCache.values()).forEach(clazzCache -> {
            try {
                clazzCache.load();
            } catch (ClassLoadException e) {
                e.printStackTrace();
            }
        });

        // Load entity type cache
        Arrays.stream(NPCType.values()).forEach(NPCType::load);

        executor = r -> this.getServer().getScheduler().scheduleSyncDelayedTask(this, r , MILLI_SECOND * (3));

        // Load all npc from data
        executor.execute(() -> {
            System.out.println("Loading npcs...");

            long startMs = System.currentTimeMillis();
            try {
                String zNPCdata = Files.toString(data, Charsets.UTF_8);

                List<NPC> npcList = gson.fromJson(zNPCdata, new TypeToken<List<NPC>>(){}.getType());
                this.npcManager.getNpcs().addAll(npcList);

                System.out.println("(Loaded " + npcList.size() + " znpcs in " +  NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");
            } catch (IOException e) {
                getServer().getPluginManager().disablePlugin(this);

                getLogger().log(Level.WARNING, "Can't load npc", e);
                return;
            }

            new NPCSaveTask(this, (Integer.parseInt(config.getValue(ZNConfigValue.SAVE_NPCS_DELAY_SECONDS))));

            // Setup netty again for online players
            Bukkit.getOnlinePlayers().forEach(ServersNPC.this::setupNetty);
        });

        // Init task for all npc
        new NPCTask(this);

        new PlayerListeners(this);
    }

    @Override
    public void onDisable() {
        npcManager.getNpcs().forEach(npc -> npc.getViewers().forEach(player -> {
            try {
                npc.delete(player , false);
            } catch (Exception e) {}
        }));

        Bukkit.getOnlinePlayers().forEach(o -> getPlayerNetties().stream().filter(playerNetty -> playerNetty.getUuid().equals(o.getUniqueId())).findFirst().ifPresent(PlayerNetty::ejectNetty));

        // Save values on config (???)
        saveAllNpcs();
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public LinkedHashSet<PlayerNetty> getPlayerNetties() {
        return playerNetties;
    }

    public int getViewDistance() {
        return viewDistance;
    }

    // Default Utils
    public static Executor getExecutor() {
        return executor;
    }

    public static boolean isPlaceHolderSupport() {
        return placeHolderSupport;
    }

    public static Gson getGson() {
        return gson;
    }

    public static String getReplaceSymbol() {
        return replaceSymbol;
    }
    // End

    public void saveAllNpcs() {
        if (System.currentTimeMillis() - startTimer <= (1000) * 5) return;

        try (FileWriter writer = new FileWriter(data)) {
            gson.toJson(getNpcManager().getNpcs().stream().filter(NPC::isSave).collect(Collectors.toList()), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setup netty for player
     *
     * @param player receiver
     */
    public void setupNetty(final Player player) {
        try {
            final PlayerNetty playerNetty = new PlayerNetty(this , player);
            playerNetty.injectNetty(player);

            this.getPlayerNetties().add(playerNetty);
        } catch (Exception exception) {
            throw new RuntimeException("An exception occurred while trying to setup netty for player " + player.getName(), exception);
        }
    }

    /**
     * Creation of a new npc
     *
     * @param id the npc id
     * @param commandSender the creator of the npc
     * @return val
     */
    public final boolean createNPC(int id , final Optional<CommandSender> commandSender, final Location location, final String skin, final String holo_lines, boolean save) throws Exception {
        final SkinFetch skinFetch = JSONUtils.getSkin(skin);

        boolean found = this.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == id);
        if (found) return false;

        this.getNpcManager().getNpcs().add(new NPC(id , holo_lines, skinFetch.value, skinFetch.signature, location, NPCType.PLAYER, new EnumMap<>(NPCItemSlot.class), save));

        commandSender.ifPresent(sender -> messages.sendMessage(commandSender.get(), ZNConfigValue.SUCCESS));
        return true;
    }

    /**
     * Delete a npc
     *
     * @param id the npc id
     * @return val
     */
    public final boolean deleteNPC(int id) throws Exception {
        final NPC npc = this.npcManager.getNpcs().stream().filter(npc1 -> npc1.getId() == id).findFirst().orElse(null);

        // Try find
        if (npc == null) {
            return false;
        }

        getNpcManager().getNpcs().remove(npc);

        final Iterator<Player> it = npc.getViewers().iterator();

        while (it.hasNext())  {
            final Player player = it.next();

            npc.delete(player, false);

            it.remove();
        }
        return true;
    }

    /**
     * Send player to server bungee
     *
     * @param p receiver
     * @param server target
     */
    public void sendPlayerToServer(Player p, String server){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
    }
}
