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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NPCTask extends BukkitRunnable {

    protected final ServersNPC serversNPC;

    public NPCTask(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.runTaskTimerAsynchronously(this.serversNPC , 0L , 20L);
    }

    @Override
    public void run() {
        for (final NPC npc : this.serversNPC.getNpcManager().getNpcs()) {
            final List<UUID> spawn = npc.getLocation().getWorld().getNearbyEntities(npc.getLocation() , 5 ,5 , 5).stream().filter(entity -> entity instanceof Player && !npc.getViewers().contains(entity.getUniqueId())).map(Entity::getUniqueId).collect(Collectors.toList());
            final List<UUID> despawn = npc.getViewers().stream().filter(uuid -> Bukkit.getPlayer(uuid) == null || Bukkit.getPlayer(uuid).getWorld() != npc.getLocation().getWorld() || Bukkit.getPlayer(uuid).getLocation().distanceSquared(npc.getLocation()) >= 5).collect(Collectors.toList());

            spawn.forEach(uuid1 -> npc.spawn(Bukkit.getPlayer(uuid1)));
            despawn.forEach(uuid1 -> npc.delete(Bukkit.getPlayer(uuid1)));
        }
    }
}
