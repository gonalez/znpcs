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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    protected final ServersNPC serversNPC;

    public PlayerListeners(final ServersNPC serversNPC)  {
        this.serversNPC = serversNPC;

        this.serversNPC.getServer().getPluginManager().registerEvents(this , serversNPC);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        this.serversNPC.setupNetty(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.serversNPC.getPlayerNetties().stream().filter(playerNetty -> playerNetty.getUuid() == event.getPlayer().getUniqueId()).findFirst().ifPresent(playerNetty -> {
            playerNetty.ejectNetty();

            this.serversNPC.getPlayerNetties().remove(playerNetty);
        });

        this.serversNPC.getNpcManager().getNpcs().stream().filter(npc -> npc.getViewers().contains(event.getPlayer())).forEach(npc -> {
            try {
                npc.delete(event.getPlayer() , true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
