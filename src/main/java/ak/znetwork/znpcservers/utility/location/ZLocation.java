package ak.znetwork.znpcservers.utility.location;

import ak.znetwork.znpcservers.types.ClassTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.util.Vector;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZLocation {

    /**
     * The location world name.
     */
    private final String world;

    /**
     * The location x,y,z.
     */
    private final double x,y,z;

    /**
     * The location yaw,pitch.
     */
    private final float yaw,pitch;

    /**
     * The saved location.
     */
    private Location locationCache;

    /**
     * Creates a new cache location off values.
     *
     * @param world The location world name
     * @param x     The location x.
     * @param y     The location y.
     * @param z     The location z.
     * @param yaw   The location yaw.
     * @param pitch The location pitch.
     */
    public ZLocation(String world,
                     double x,
                     double y,
                     double z,
                     float yaw,
                     float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Creates a new cache location off a bukkit-location.
     *
     * @param location The bukkit-location.
     */
    public ZLocation(Location location) {
        this(location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    /**
     * Returns the bukkit location.
     *
     * @return The bukkit-location.
     */
    public Location toBukkitLocation() {
        if (locationCache != null) {
            return locationCache;
        }

        return locationCache = new Location(Bukkit.getWorld(world),
                x,
                y,
                z,
                yaw,
                pitch
        );
    }

    /**
     * @inheritDoc
     */
    public Object getNMSWorld() throws InvocationTargetException, IllegalAccessException {
        return ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(toBukkitLocation().getWorld());
    }

    /**
     * @inheritDoc
     */
    public Vector toVector() {
        return toBukkitLocation().toVector();
    }
}
