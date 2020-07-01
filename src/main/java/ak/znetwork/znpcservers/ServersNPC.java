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

import ak.znetwork.znpcservers.commands.list.CreateCommand;
import ak.znetwork.znpcservers.commands.list.DefaultCommand;
import ak.znetwork.znpcservers.commands.list.DeleteCommand;
import ak.znetwork.znpcservers.commands.list.ToggleCommand;
import ak.znetwork.znpcservers.configuration.Configuration;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.listeners.PlayerListeners;
import ak.znetwork.znpcservers.manager.CommandsManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.manager.tasks.NPCTask;
import ak.znetwork.znpcservers.netty.PlayerNetty;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServersNPC extends JavaPlugin {

    protected Configuration data;

    protected CommandsManager commandsManager;
    protected NPCManager npcManager;

    final ExecutorService executor = Executors.newFixedThreadPool(2);

    protected LinkedHashSet<PlayerNetty> playerNetties;

    @Override
    public void onEnable() {
        playerNetties = new LinkedHashSet<>();

        npcManager = new NPCManager();

        commandsManager = new CommandsManager("znservers", this);
        commandsManager.addCommands(new DefaultCommand(this) , new CreateCommand(this) , new DeleteCommand(this) , new ToggleCommand(this));

        this.data = new Configuration(this , "data");

        // Check if data contains any npc
        if (this.data.getConfig().contains("znpcs")) {
            int size = this.data.getConfig().getConfigurationSection("znpcs").getKeys(false).size();

            long startMs = System.currentTimeMillis();

            System.out.println("Loading " + size + " npcs...");

            // Load all npc from config (Async)
            CompletableFuture.runAsync(() -> {
                for (final String keys : this.data.getConfig().getConfigurationSection("znpcs").getKeys(false)) {
                    final Location location = LocationUtils.getLocationString(this.data.getConfig().getString("znpcs." + keys + ".location"));

                    final String[] strings = new String[this.data.getConfig().getString("znpcs." + keys + ".lines").split(":").length];

                    for (int i=0; i <= strings.length - 1; i++)
                        strings[i] = this.data.getConfig().getString("znpcs." + keys + ".lines").split(":")[i];

                    npcManager.getNpcs().add(new NPC(Integer.parseInt(keys) , location , new Hologram(location , strings)));
                }
            });

            System.out.println("(Loaded " + size + "npcs in " +  NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");

            // Init task for all npc
            new NPCTask(this);
        }

        new PlayerListeners(this);
    }

    @Override
    public void onDisable() {
        npcManager.getNpcs().forEach(npc -> npc.getViewers().forEach(uuid -> {
            if (Bukkit.getPlayer(uuid) != null)
                npc.delete(Bukkit.getPlayer(uuid) , true);
        }));
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

    /**
     * Creation of a new npc
     *
     * @param id the npc id
     * @param player the creator of the npc
     * @return val
     */
    public final boolean createNPC(int id , final Player player , final String holo_lines) {
        final NPC npc = this.npcManager.getNpcs().stream().filter(npc1 -> npc1.getId() == id).findFirst().orElse(null);

        // Try find
        if (npc != null) {
            return false;
        }

        final String[] strings = new String[holo_lines.split(":").length];

        for (int i=0; i <= strings.length - 1; i++)
            strings[i] = holo_lines.split(":")[i];

        this.getNpcManager().getNpcs().add(new NPC(id , player.getLocation() , new Hologram(player.getLocation()  , strings)));

        this.data.getConfig().set("znpcs." + id + ".location" , LocationUtils.getStringLocation(player.getLocation()));
        this.data.getConfig().set("znpcs." + id + ".lines" , holo_lines);
        this.data.save();
        return true;
    }

    /**
     * Delete a npc
     *
     * @param id the npc id
     * @return val
     */
    public final boolean deleteNPC(int id) {
        CompletableFuture.supplyAsync(() -> {
            final NPC npc = this.npcManager.getNpcs().stream().filter(npc1 -> npc1.getId() == id).findFirst().orElse(null);

            // Try find
            if (npc == null) {
                return false;
            }

            getNpcManager().getNpcs().remove(npc);

            final Iterator<UUID> it = npc.getViewers().iterator();

            while (it.hasNext())  {
                final UUID uuid = it.next();

                npc.delete(Bukkit.getPlayer(uuid) , false);

                it.remove();
            }

            this.data.getConfig().set("znpcs." + id , null);
            this.data.save();
            return true;
        }, executor);

        return false;
    }
}
