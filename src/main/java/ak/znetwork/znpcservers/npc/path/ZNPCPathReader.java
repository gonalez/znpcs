package ak.znetwork.znpcservers.npc.path;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZNPCPathReader {

    /**
     * The path file.
     */
    private final File file;

    /**
     * A list of locations.
     *
     * Represents loaded path locations.
     */
    private final List<Location> locationList;

    /**
     * Creates a reader to read a path.
     *
     * @param file The path file.
     * @throws IOException If the path cannot be loaded.
     */
    public ZNPCPathReader(File file) throws IOException {
        this.file = file;
        this.locationList = new ArrayList<>();

        this.read();
    }

    /**
     * Reads path file.
     */
    public void read() {
        try {
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
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get path name.
     *
     * @return The path name.
     */
    public String getName() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }
}
