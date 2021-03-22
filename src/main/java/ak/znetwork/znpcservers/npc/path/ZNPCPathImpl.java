package ak.znetwork.znpcservers.npc.path;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.ZNPC;
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
import org.bukkit.util.Vector;

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
     * Returns a new path handler for the specified npc.
     *
     * @param npc The npc.
     * @return A new path the current type.
     */
    ZNPCPath getPath(ZNPC npc);

    /**
     * {@inheritDoc}
     */
    interface ZNPCPath {

        /**
         * Handles the path for the npc.
         */
        void handle();

        /**
         * Returns the current path location for the npc.
         *
         * @return The current path location for the npc.
         */
        ZLocation getLocation();

        /**
         * {@inheritDoc}
         */
        abstract class AbstractPath implements ZNPCPathImpl.ZNPCPath {

            /**
             * The npc in which the path will be handled.
             */
            private final ZNPC npc;

            /**
             * The path type.
             */
            private final AbstractTypeWriter typeWriter;

            /**
             * The current path location.
             */
            @Setter private ZLocation location;

            /**
             * Creates a new path handler for an npc.
             *
             * @param npc The npc.
             * @param typeWriter The path type.
             */
            public AbstractPath(ZNPC npc,
                                AbstractTypeWriter typeWriter) {
                this.npc = npc;
                this.typeWriter = typeWriter;
            }

            /**
             * Returns the npc in which the path will be handled.
             *
             * @return The npc in which the path will be handled.
             */
            public ZNPC getNpc() {
                return npc;
            }

            /**
             * Returns the path type.
             *
             * @return The path type.
             */
            public AbstractTypeWriter getPath() {
                return typeWriter;
            }

            @Override
            public ZLocation getLocation() {
                return location;
            }
        }
    }

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
        public static ZNPCPathDelegator forPath(AbstractTypeWriter pathAbstract) {
            return new ZNPCPathDelegator(pathAbstract.getFile());
        }
    }

    /**
     * {@inheritDoc}
     */
    abstract class AbstractTypeWriter implements ZNPCPathImpl {

        /**
         * The logger.
         */
        private static final Logger LOGGER = Bukkit.getLogger();

        /**
         * A map for storing & identifying a path by its name.
         */
        private static final ConcurrentMap<String, AbstractTypeWriter> PATH_TYPES = new ConcurrentHashMap<>();

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
        public AbstractTypeWriter(File file) {
            this.file = file;
            this.locationList = new ArrayList<>();
        }

        /**
         * Creates a new path for the given path name.
         *
         * @param pathName The path name.
         */
        public AbstractTypeWriter(String pathName) {
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
         * Writes the path attributes.
         */
        public void write() {
            try (DataOutputStream writer = ZNPCPathDelegator.forFile(getFile()).getOutputStream()) {
                write(writer);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, String.format("Path %s could not be created", getName()), e);
            }
        }

        /**
         * Returns the path file.
         *
         * @return The path file
         */
        public File getFile() {
            return file;
        }

        /**
         * Returns the saved path locations.
         *
         * @return The saved path locations.
         */
        public List<ZLocation> getLocationList() {
            return locationList;
        }

        /**
         * Returns the path name.
         *
         * @return The path name.
         */
        public String getName() {
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        }

        /**
         * Registers a new path.
         */
        public static void register(AbstractTypeWriter abstractZNPCPath) {
            PATH_TYPES.put(abstractZNPCPath.getName(), abstractZNPCPath);
        }

        /**
         * Locates a path by its name.
         *
         * @param name The path name.
         * @return The path or {@code null} if no path was found.
         */
        public static AbstractTypeWriter find(String name) {
            return PATH_TYPES.get(name);
        }

        /**
         * A collection of all registered paths.
         *
         * @return A collection of all registered paths.
         */
        public static Collection<AbstractTypeWriter> getPaths() {
            return PATH_TYPES.values();
        }

        /**
         * {@inheritDoc}
         */
        public static class TypeMovement extends AbstractTypeWriter {

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
            public TypeMovement(File file) {
                super(file);
            }

            /**
             * {@inheritDoc}
             */
            public TypeMovement(String pathName,
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

                        // Write path
                        write();
                    }
                }, PATH_DELAY, PATH_DELAY);
            }

            @Override
            public MovementPath getPath(ZNPC npc) {
                return new MovementPath(npc, this);
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

            /**
             * {@inheritDoc}
             */
            protected static class MovementPath extends ZNPCPath.AbstractPath {

                /**
                 * The current path location.
                 */
                private int currentEntryPath = 0;

                /**
                 * Determines if path is running backwards or forwards.
                 */
                private boolean pathReverse = false;

                /**
                 * Creates a new path for an npc.
                 *
                 * @param npc The npc that will be handled.
                 * @param path The path that will handle the npc.
                 */
                public MovementPath(ZNPC npc,
                                    TypeMovement path) {
                    super(npc, path);
                }

                @Override
                public void handle() {
                    final int currentEntry = currentEntryPath;
                    final boolean reversePath = getNpc().isReversePath();;

                    if (reversePath) {
                        if (currentEntry <= 0) pathReverse = false;
                        else if (currentEntry >= getPath().getLocationList().size() - 1) pathReverse = true;
                    }

                    setLocation(getPath().getLocationList().get(Math.min(getPath().getLocationList().size() - 1, currentEntry)));

                    if (!pathReverse) currentEntryPath = currentEntry + 1;
                    else currentEntryPath = currentEntry - 1;

                    updatePathLocation(getLocation());
                }

                /**
                 * Updates the new npc location according to current path index.
                 *
                 * @param location The npc path location.
                 */
                protected void updatePathLocation(ZLocation location) {
                    int pathIndex = getPath().getLocationList().indexOf(getLocation());

                    Vector vector = (pathReverse ? getPath().getLocationList().get(Math.max(0, Math.min(getPath().getLocationList().size() - 1, pathIndex + 1))) : getPath().getLocationList().get(Math.min(getPath().getLocationList().size() - 1, (Math.max(0, pathIndex - 1))))).toBukkitLocation().toVector();
                    double yDiff = (location.getY() - vector.getY());

                    Location direction = getLocation().toBukkitLocation().clone().setDirection(location.toBukkitLocation().clone().subtract(vector.clone().add(new Vector(0, yDiff, 0))).toVector());

                    getNpc().setLocation(direction);
                    getNpc().lookAt(null, direction, true);
                }
            }
        }
    }
}
