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
package ak.znetwork.znpcservers.listeners;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/*
 TODO:
  - NPC disappearing after world unload
 */
public class NPCListeners implements Listener {

    private final ServersNPC serversNPC;

    public NPCListeners(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.serversNPC.getServer().getPluginManager().registerEvents(this, serversNPC);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (NPC npc : serversNPC.getNpcManager().getNpcs()) {

        }
    }
}
