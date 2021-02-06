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
import ak.znetwork.znpcservers.npc.ZNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class NPCTask extends BukkitRunnable {

    private final ServersNPC serversNPC;

    public NPCTask(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.runTaskTimerAsynchronously(this.serversNPC, 60L, 1L);
    }

    @Override
    public void run() {
        for (final ZNPC npc : this.serversNPC.getNpcManager().getNPCs()) {
            npc.handlePath(); // Path

            for (final Player player : Bukkit.getOnlinePlayers()) {
                try {
                    if (player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) <= this.serversNPC.getViewDistance() && !npc.getViewers().contains(player))
                        npc.spawn(player);

                    else if (player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player) || player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player) || player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) > this.serversNPC.getViewDistance() && npc.getViewers().contains(player))
                        npc.delete(player, true);

                    if (npc.getViewers().contains(player) && player.getLocation().distance(npc.getLocation()) <= this.serversNPC.getViewDistance()) {
                        if (npc.isHasLookAt()) npc.lookAt(Optional.of(player), player.getLocation(), false);

                        npc.getHologram().updateNames(player);
                    }
                } catch (Exception exception) {
                    // Nothing
                }
            }
        }
    }
}
