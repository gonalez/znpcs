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
package ak.znetwork.znpcservers.npc.path;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ZNPCPathReader {

    @Getter private final File file;
    @Getter private final List<Location> locationList;

    public ZNPCPathReader(File file) throws IOException {
        this.file = file;

        locationList = new ArrayList<>();

        read();
    }

    public void read() throws IOException {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
            DataInputStream dataOutputStream = new DataInputStream(byteArrayInputStream)) {
            while (dataOutputStream.available() > 0) {
                String worldName = dataOutputStream.readUTF();

                double x = dataOutputStream.readDouble();
                double y = dataOutputStream.readDouble();
                double z = dataOutputStream.readDouble();

                float yaw = dataOutputStream.readFloat();
                float pitch = dataOutputStream.readFloat();

                locationList.add(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
            }
        }
    }

    public String getName() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }
}
