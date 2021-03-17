package ak.znetwork.znpcservers.npc.path;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.location.ZLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ZNPCPathImpl {

    /**
     * Reads all the path attributes.
     *
     * @throws IOException If an I/O error occurs
     */
    void initialize(DataInputStream dataInputStream) throws IOException;

    /**
     * Writes all the path attributes.
     *
     * @throws IOException If an I/O error occurs
     */
    void write(DataOutputStream dataOutputStream) throws IOException;

    /**
     * Initializes the task for the path.
     */
    void start();

    /**
     * {@inheritDoc}
     */
    class ZNPCPathDelegator {

        /**
         * The path file.
         */
        private final File file;

        /**
         * Creates a new delegator for a path file.
         *
         * @param file The path file.
         */
        protected ZNPCPathDelegator(File file) {
            this.file = file;
        }

        /**
         * Returns an output stream to write a path.
         *
         * @throws IOException If an I/O error occurs
         */
        public DataOutputStream getOutputStream() throws IOException {
            return new DataOutputStream(new FileOutputStream(file));
        }

        /**
         * Returns an input stream to read a path.
         *
         * @throws IOException If an I/O error occurs
         */
        public DataInputStream getInputStream() throws IOException {
            return new DataInputStream(new FileInputStream(file));
        }

        /**
         * Resolves a path delegator for the path file.
         *
         * @param file The path file.
         * @return A path delegator for the path file.
         */
        public static ZNPCPathDelegator forFile(File file) {
            return new ZNPCPathDelegator(file);
        }

        /**
         * Resolves a path delegator for the path.
         *
         * @param pathAbstract The path file.
         * @return A path delegator for the path.
         */
        public static ZNPCPathDelegator forPath(AbstractZNPCPath pathAbstract) {
            return new ZNPCPathDelegator(pathAbstract.getFile());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Getter
    abstract class AbstractZNPCPath implements ZNPCPathImpl {

        /**
         * The logger.
         */
        private static final Logger LOGGER = Bukkit.getLogger();

        /**
         * A map for storing & identifying a path by its name.
         */
        private static final ConcurrentMap<String, AbstractZNPCPath> PATH_TYPES = new ConcurrentHashMap<>();

        /**
         * Represents how often the locations will be saved.
         */
        private static final int PATH_DELAY = 1;

        /**
         * The path file.
         */
        private final File file;

        /**
         * The recorded path locations.
         */
        private final List<ZLocation> locationList;

        /**
         * Creates a new path for the given path file.
         *
         * @param file The path File.
         */
        public AbstractZNPCPath(File file) {
            this.file = file;
            this.locationList = new ArrayList<>();
        }

        /**
         * Creates a new path for the given path name.
         *
         * @param pathName The path name.
         */
        public AbstractZNPCPath(String pathName) {
            this(new File(ServersNPC.PATH_FOLDER, pathName + ".path"));
        }

        /**
         * Registers the path and load it.
         */
        public void load() {
            try (DataInputStream reader = ZNPCPathDelegator.forFile(file).getInputStream()) {
                initialize(reader);

                // Register path..
                register(this);
            } catch (IOException e) {
                // The path could not be initialized...
                LOGGER.log(Level.WARNING, String.format("The path %s could not be loaded", file.getName()));
            }
        }

        /**
         * Gets the path name.
         *
         * @return The path name.
         */
        public String getName() {
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        }

        /**
         * Registers a new path.
         */
        public static void register(AbstractZNPCPath abstractZNPCPath) {
            PATH_TYPES.put(abstractZNPCPath.getName(), abstractZNPCPath);
        }

        /**
         * Locates a path by its name.
         *
         * @param name The path name.
         * @return The path or {@code null} if no path was found.
         */
        public static AbstractZNPCPath find(String name) {
            return PATH_TYPES.get(name);
        }

        /**
         * A collection of all registered paths.
         *
         * @return A collection of all registered paths.
         */
        public static Collection<AbstractZNPCPath> getPaths() {
            return PATH_TYPES.values();
        }

        /**
         * {@inheritDoc}
         */
        public static class ZNPCMovementPath extends AbstractZNPCPath {

            /**
             * The maximum locations that the path can have.
             */
            private static final int MAX_LOCATIONS = ConfigManager.getByType(ZNConfigType.CONFIG).getValue(ZNConfigValue.MAX_PATH_LOCATIONS);

            /**
             * The player who is creating the path.
             */
            private ZNPCUser npcUser;

            /**
             * The path task.
             */
            private BukkitTask bukkitTask;

            /**
             * {@inheritDoc}
             */
            public ZNPCMovementPath(File file) {
                super(file);
            }

            /**
             * {@inheritDoc}
             */
            public ZNPCMovementPath(String pathName,
                                    ZNPCUser npcUser) {
                super(pathName);
                this.npcUser = npcUser;

                // Start path task
                this.start();
            }

            @Override
            public void initialize(DataInputStream dataInputStream) throws IOException {
                while (dataInputStream.available() > 0) {
                    String worldName = dataInputStream.readUTF();

                    double x = dataInputStream.readDouble();
                    double y = dataInputStream.readDouble();
                    double z = dataInputStream.readDouble();

                    float yaw = dataInputStream.readFloat();
                    float pitch = dataInputStream.readFloat();

                    // Add path location
                    getLocationList().add(new ZLocation(worldName, x, y, z, yaw, pitch));
                }
            }

            @Override
            public void write(DataOutputStream dataOutputStream) throws IOException {
                Preconditions.checkArgument(!getLocationList().isEmpty(), "Location list is empty");

                Iterator<ZLocation> locationIterator = getLocationList().iterator();
                while (locationIterator.hasNext()) {
                    ZLocation location = locationIterator.next();

                    // Location world name
                    dataOutputStream.writeUTF(location.getWorld());

                    // Location x,y,z,yaw,pitch
                    dataOutputStream.writeDouble(location.getX());
                    dataOutputStream.writeDouble(location.getY());
                    dataOutputStream.writeDouble(location.getZ());
                    dataOutputStream.writeFloat(location.getYaw());
                    dataOutputStream.writeFloat(location.getPitch());

                    boolean last = !locationIterator.hasNext();
                    if (last) {
                        npcUser.setHasPath(false);

                        // Register the path...
                        register(this);
                    }
                }
            }

            @Override
            public void start() {
                npcUser.setHasPath(true);

                bukkitTask = ServersNPC.SCHEDULER.runTaskTimerAsynchronously(() -> {
                    if (npcUser.toPlayer() != null && npcUser.isHasPath() && MAX_LOCATIONS > getLocationList().size()) {
                        final Location location = npcUser.toPlayer().getLocation();

                        // Check if location is valid
                        if (isValid(location)) {
                            // Add new location..
                            getLocationList().add(new ZLocation(location));
                        }
                    } else {
                        // Cancel task...
                        bukkitTask.cancel();

                        // Set user path
                        npcUser.setHasPath(false);

                        try (DataOutputStream writer = ZNPCPathDelegator.forFile(getFile()).getOutputStream()) {
                            write(writer);
                        } catch (IOException e) {
                            npcUser.setHasPath(false);

                            LOGGER.log(Level.WARNING, String.format("Path %s could not be created", getName()), e);
                        }
                    }
                }, PATH_DELAY, PATH_DELAY);
            }

            /**
             * Checks if a location can be added to path.
             *
             * @param location The location to add.
             * @return {@code true} If location can be added.
             */
            protected boolean isValid(Location location) {
                if (getLocationList().isEmpty())
                    return true;

                ZLocation last = getLocationList().get(getLocationList().size() - 1);

                double xDiff = Math.abs(last.getX() - location.getX());
                double yDiff = Math.abs(last.getY() - location.getY());
                double zDiff = Math.abs(last.getZ() - location.getZ());

                return (xDiff + yDiff + zDiff) > 0.01;
            }
        }
    }
}
