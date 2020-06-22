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
import ak.znetwork.znpcservers.configuration.Configuration;
import ak.znetwork.znpcservers.manager.CommandsManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.manager.tasks.NPCTask;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;

public class ServersNPC extends JavaPlugin {

    protected Configuration data;

    protected CommandsManager commandsManager;
    protected NPCManager npcManager;

    @Override
    public void onEnable() {
        npcManager = new NPCManager();

        commandsManager = new CommandsManager("znservers", this);
        commandsManager.addCommands(new DefaultCommand(this) , new CreateCommand(this) , new DeleteCommand(this));

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

                    npcManager.getNpcs().add(new NPC(Integer.parseInt(keys) , location));
                }
            });

            System.out.println("(Loaded " + size + "npcs in " +  NumberFormat.getInstance().format(System.currentTimeMillis() - startMs) + "ms)");

            // Init task for all npc
            new NPCTask(this);
        }
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    /**
     * Creation of a new npc
     *
     * @param id the npc id
     * @param player the creator of the npc
     * @return val
     */
    public final boolean createNPC(int id , final Player player) {
        final NPC npc = this.npcManager.getNpcs().stream().filter(npc1 -> npc1.getId() == id).findFirst().orElse(null);

        // Try find
        if (npc != null) {
            return false;
        }

        this.getNpcManager().getNpcs().add(new NPC(id , player.getLocation()));

        this.data.getConfig().set("znpcs." + id + ".location" , LocationUtils.getStringLocation(player.getLocation()));
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
        final NPC npc = this.npcManager.getNpcs().stream().filter(npc1 -> npc1.getId() == id).findFirst().orElse(null);

        // Try find
        if (npc == null) {
            return false;
        }

        npc.getViewers().forEach(uuid -> npc.delete(Bukkit.getPlayer(uuid)));

        this.data.getConfig().set("znpcs." + id , null);
        this.data.save();
        return true;
    }
}
