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
package ak.znetwork.znpcservers.manager.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCTask extends BukkitRunnable {

    protected final ServersNPC serversNPC;

    public NPCTask(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.runTaskTimerAsynchronously(this.serversNPC , 0L , 1L);
    }

    @Override
    public void run() {
        for (final NPC npc : this.serversNPC.getNpcManager().getNpcs()) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) <= 30D && !npc.getViewers().contains(player.getUniqueId()))
                    npc.spawn(player);
                else  if (player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player.getUniqueId()) || player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player.getUniqueId()) ||  player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) > 30D && npc.getViewers().contains(player.getUniqueId()))
                    npc.delete(player , true);

                if (npc.getViewers().contains(player.getUniqueId()) && player.getLocation().distance(npc.getLocation()) <= 30D) {
                    if (npc.isHasLookAt()) npc.lookAt(player , player.getLocation() , false);

                    npc.getHologram().updateNames(player);
                }
            }
        }
    }
}
