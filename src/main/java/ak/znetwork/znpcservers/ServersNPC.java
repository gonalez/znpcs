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
import ak.znetwork.znpcservers.commands.list.*;
import ak.znetwork.znpcservers.configuration.Configuration;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.listeners.PlayerListeners;
import ak.znetwork.znpcservers.manager.CommandsManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.manager.tasks.NPCTask;
import ak.znetwork.znpcservers.netty.PlayerNetty;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.serializer.NPCSerializer;
import ak.znetwork.znpcservers.utils.JSONUtils;
import ak.znetwork.znpcservers.utils.MetricsLite;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ServersNPC extends JavaPlugin {

    protected Configuration messages;

    protected CommandsManager commandsManager;
    protected NPCManager npcManager;

    protected LinkedHashSet<PlayerNetty> playerNetties;

    protected boolean placeHolderSupport;

    protected Executor executor;

    private Gson gson;

    private File data;

    private int viewDistance;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        data = new File(getDataFolder() , "data.json");
        try {
            data.createNewFile();
        } catch (IOException e) {
            //throw new RuntimeException("An exception occurred while trying to create data.json file" , e);
            getLogger().log(Level.WARNING, "An exception occurred while trying to create data.json file", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        viewDistance = (Bukkit.getViewDistance() << 2);

        gson = new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(NPC.class , new NPCSerializer(this)).setPrettyPrinting().create();

        placeHolderSupport = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        playerNetties = new LinkedHashSet<>();

        npcManager = new NPCManager();

        this.messages = new Configuration(this , "messages");

        commandsManager = new CommandsManager("znpcs", this);
        commandsManager.addCommands(new DefaultCommand(this) , new CreateCommand(this) , new DeleteCommand(this) , new ListCommand(this), new ActionCommand(this) , new ToggleCommand(this), new TypeCommand(this), new MoveCommand(this) , new EquipCommand(this) , new LinesCommand(this) , new SkinCommand(this));

        int pluginId = 8054;
        new MetricsLite(this, pluginId);

        // Load reflection cache
        try { ClazzCache.load();} catch (NoSuchMethodException | ClassNotFoundException e) {e.printStackTrace();}

        this.executor = r -> this.getServer().getScheduler().scheduleSyncDelayedTask(this, r , 40);

        // Load all npc from data
        this.executor.execute(() -> {
            System.out.println("Loading npcs...");

            long startMs = System.currentTimeMillis();

            int size = 0;
            try {
                final FileReader fileReader = new FileReader(data);

                final JsonReader reader = new JsonReader(new FileReader(data));

                // Empty check
                if (fileReader.read() == -1 ||!reader.hasNext()) return;

                reader.beginArray();
                while (reader.hasNext()) {
                    final NPC npc = gson.fromJson(reader, NPC.class);

                    this.npcManager.getNpcs().add(npc);

                    size++;
                }
                System.out.println("(Loaded " + size + " npcs in " +  NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");
            } catch (IOException e) {
                //throw new RuntimeException("An exception occurred while trying to load npcs" , e);
            }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        Bukkit.getOnlinePlayers().forEach(o -> getPlayerNetties().stream().filter(playerNetty -> playerNetty.getUuid() == o.getUniqueId()).findFirst().ifPresent(PlayerNetty::ejectNetty));

        // Save values on config (???)
        long startMs = System.currentTimeMillis();
        try {
            final String json = gson.toJson(getNpcManager().getNpcs().stream().filter(NPC::isSave).collect(Collectors.toList()), new TypeToken<LinkedHashSet<NPC>>(){}.getType());
            try(FileWriter writer = new FileWriter(data)) {
                writer.append(json);
            }

            System.out.println("(Saved " +  getNpcManager().getNpcs().size() + "npcs in " +  NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred while trying to save npcs" , e);
        }
    }

    public Configuration getMessages() {
        return messages;
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

    public boolean isPlaceHolderSupport() {
        return placeHolderSupport;
    }

    public Executor getExecutor() {
        return executor;
    }

    public int getViewDistance() {
        return viewDistance;
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
     * @param player the creator of the npc
     * @return val
     */
    public final boolean createNPC(int id , final Player player , final String skin, final String holo_lines) {
        try {
            final SkinFetch skinFetcher = JSONUtils.getSkin(skin);

            this.getNpcManager().getNpcs().add(new NPC(this , id , skinFetcher.value, skinFetcher.signature, player.getLocation(), NPCType.PLAYER,  new Hologram(this , player.getLocation(), holo_lines.split(":")) , true));

            player.sendMessage(Utils.tocolor(getMessages().getConfig().getString("success")));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while creating npc " + id, e);
        }
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
