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
package ak.znetwork.znpcservers.npc.path.creator;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.npc.path.ZNPCPath;
import ak.znetwork.znpcservers.user.ZNPCUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZNPCPathCreator extends BukkitRunnable {

    private final ServersNPC serversNPC;

    private final ZNPCUser znpcUser;

    private final File file;

    private FileOutputStream fileOutputStream;
    private DataOutputStream dataOutputStream;

    private final List<Location> locationsCache;

    private final int MAX_LOCATIONS;

    public ZNPCPathCreator(ServersNPC serversNPC, ZNPCUser znpcUser, String name) {
        this.serversNPC = serversNPC;

        this.MAX_LOCATIONS = Integer.parseInt(serversNPC.getConfiguration().getValue(ZNConfigValue.MAX_PATH_LOCATIONS));

        this.znpcUser = znpcUser;

        this.file = new File(serversNPC.getDataFolder().getAbsolutePath() + "/paths", name + ".path");

        this.locationsCache = new ArrayList<>();
        try {
            this.file.createNewFile();

            fileOutputStream = new FileOutputStream(file);
            dataOutputStream = new DataOutputStream(fileOutputStream);

            runTaskTimer(serversNPC, 0L, 1L);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (this.znpcUser.toPlayer() != null && this.MAX_LOCATIONS > locationsCache.size()) {
            Location location = this.znpcUser.toPlayer().getLocation();

            if (!checkEntry(location)) {
                locationsCache.add(location);
            }
        } else {
            try {
                writeAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeAll() throws IOException {
        cancel();

        znpcUser.setZnpcPathCreator(null);

        if (locationsCache.isEmpty()) return;

        Iterator<Location> locationIterator = this.locationsCache.iterator();
        while (locationIterator.hasNext()) {
            Location location = locationIterator.next();

            byte[] bytes = location.getWorld().getName().getBytes(StandardCharsets.UTF_8);

            // Location world name
            dataOutputStream.writeInt(bytes.length);
            dataOutputStream.write(bytes);

            // Location x,y,z,yaw,pitch
            dataOutputStream.writeDouble(location.getX());
            dataOutputStream.writeDouble(location.getY());
            dataOutputStream.writeDouble(location.getZ());
            dataOutputStream.writeFloat(location.getYaw());
            dataOutputStream.writeFloat(location.getPitch());

            boolean last = !locationIterator.hasNext();
            if (last) {
                dataOutputStream.close();

                serversNPC.getNpcManager().getZnpcPaths().add(new ZNPCPath(file));
            }
        }
    }

    public boolean checkEntry(Location location) {
        if (this.locationsCache.size() <= 0) return false;

        Location last = this.locationsCache.get(this.locationsCache.size() - 1);
        return (Math.abs(last.getX() - location.getX()) <= 0);
    }

    public Player getPlayer() {
        return this.znpcUser.toPlayer();
    }
}
