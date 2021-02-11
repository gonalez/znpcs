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
package ak.znetwork.znpcservers.npc.path.writer;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.user.ZNPCUser;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;

public final class ZNPCPathWriter {

    @Getter private final String name;

    @Getter private final ZNPCUser npcUser;
    @Getter private final File file;
    @Getter private final List<Location> locationsCache;

    private final ServersNPC serversNPC;

    private final int MAX_LOCATIONS = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.MAX_PATH_LOCATIONS);

    public ZNPCPathWriter(ServersNPC serversNPC, ZNPCUser znpcUser, String name) {
        this.serversNPC = serversNPC;

        this.name = name;

        this.npcUser = znpcUser;

        this.file = new File(serversNPC.getDataFolder().getAbsolutePath() + "/paths", name + ".path");

        this.locationsCache = new ArrayList<>();
        try {
            this.file.createNewFile();

            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the path task
     */
    public void start() {
        // Set path
        this.getNpcUser().setHasPath(true);

        // Schedule npc path task
        ServersNPC.getExecutorService().execute(() -> {
            // This while loop will continue recording new locations for path & blocking the current thread,
            // As long the player is connected & the locations size hasn't reached the limit,
            // Once the loop is broken the thread will write the recorded locations to the path file.
            while (this.getNpcUser().toPlayer() != null && this.getNpcUser().isHasPath() && this.MAX_LOCATIONS > locationsCache.size()) {
                Location location = this.getNpcUser().toPlayer().getLocation();

                // Check if location is valid
                if (checkEntry(location)) {
                    locationsCache.add(location);
                }

                // Lock current thread for 20 MILLISECONDS
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ServersNPC.MILLI_SECOND));
            }

            // Write locations to file
            try {
                write();
            } catch (IOException e) {
                getNpcUser().setHasPath(false);

                serversNPC.getLogger().log(Level.WARNING, String.format("Path %s could not be created", this.name), e);
            }
        });
    }

    /**
     * Write recorded locations to path file
     *
     * @throws IOException When could not write to file
     */
    public void write() throws IOException {
        if (locationsCache.isEmpty()) return;

        try(FileOutputStream inputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(inputStream)) {

            Iterator<Location> locationIterator = this.locationsCache.iterator();
            while (locationIterator.hasNext()) {
                Location location = locationIterator.next();

                // Location world name
                dataOutputStream.writeUTF(location.getWorld().getName());

                // Location x,y,z,yaw,pitch
                dataOutputStream.writeDouble(location.getX());
                dataOutputStream.writeDouble(location.getY());
                dataOutputStream.writeDouble(location.getZ());
                dataOutputStream.writeFloat(location.getYaw());
                dataOutputStream.writeFloat(location.getPitch());

                boolean last = !locationIterator.hasNext();
                if (last) {
                    getNpcUser().setHasPath(false);

                    // Add path
                    serversNPC.getNpcManager().getNpcPaths().add(new ZNPCPathReader(file));
                }
            }
        }
    }

    public boolean checkEntry(Location location) {
        if (this.locationsCache.isEmpty()) return true;

        Location last = this.locationsCache.get(this.locationsCache.size() - 1);

        double xDiff = Math.abs(last.getX() - location.getX());
        double yDiff = Math.abs(last.getY() - location.getY());
        double zDiff = Math.abs(last.getZ() - location.getZ());

        return (xDiff + yDiff + zDiff) > 0;

    }

    public Player getPlayer() {
        return this.getNpcUser().toPlayer();
    }
}
