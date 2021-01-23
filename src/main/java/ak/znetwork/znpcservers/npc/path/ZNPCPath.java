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

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ZNPCPath {

    private final File file;

    private final ByteArrayInputStream byteArrayInputStream;
    private final DataInputStream dataInputStream;

    private final List<Location> locationList;

    public ZNPCPath(File file) throws IOException {
        this.file = file;

        byteArrayInputStream = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
        dataInputStream = new DataInputStream(byteArrayInputStream);

        locationList = new ArrayList<>();

        read();
    }

    public void read() throws IOException {
        while (dataInputStream.available() > 0) {
            int worldNameLength = dataInputStream.readInt();

            byte[] worldNameBytes = new byte[worldNameLength];
            dataInputStream.read(worldNameBytes, 0, worldNameBytes.length);

            String worldName = new String(worldNameBytes, StandardCharsets.UTF_8);

            double x = dataInputStream.readDouble();
            double y = dataInputStream.readDouble();
            double z = dataInputStream.readDouble();

            float yaw = dataInputStream.readFloat();
            float pitch = dataInputStream.readFloat();

            locationList.add(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
        }
    }

    public String getName() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    public List<Location> getLocationList() {
        return locationList;
    }
}
