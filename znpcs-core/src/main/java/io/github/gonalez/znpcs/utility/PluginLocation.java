package io.github.gonalez.znpcs.utility;

import com.google.common.base.Objects;
import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

/**
 * Simple struct class that hold information for an {@link Location}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class PluginLocation {
    public static final ZLocationSerializer SERIALIZER = new ZLocationSerializer();

    /**
     * The location world name
     */
    private final String worldName;

    /**
     * The location coordinates.
     */
    private final double x, y, z;

    /**
     * The location yaw and pitch.
     */
    private final float yaw, pitch;

    /**
     * Represents the {@link Location bukkit location} that this instance refers to.
     * @see #bukkitLocation()
     */
    private Location bukkitLocation;

    /**
     * Creates a {@link PluginLocation} from the given values.
     *
     * @param worldName the location world name
     * @param x the location x-coordinate.
     * @param y the location y-coordinate.
     * @param z the location z-coordinate.
     * @param yaw the location yaw.
     * @param pitch the location pitch.
     */
    public PluginLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Creates a {@link PluginLocation} from the given bukkit location.
     *
     * @param location the bukkit location.
     */
    public PluginLocation(Location location) {
        this(
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch());
    }

    /**
     * Returns the location world name.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Returns the location x-coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the location y-coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the location z-coordinate.
     */
    public double getZ() {
        return z;
    }

    /**
     *  Returns the location yaw.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     *  Returns the location pitch.
     */
    public float getPitch() {
        return pitch;
    }

    public double distance(PluginLocation pluginLocation) {
        return Math.sqrt((this.x - pluginLocation.x) * 2) + ((this.z - pluginLocation.z) * 2);
    }
    /**
     * Converts this instance to an {@link Location}.
     *
     * @return A {@link Location} with this instance information.
     */
    public Location bukkitLocation() {
        if (bukkitLocation != null) {
            return bukkitLocation;
        }
        return bukkitLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    /**
     * Converts the {@link #bukkitLocation bukkit location} to an {@link Vector}.
     */
    public Vector toVector() {
        return bukkitLocation().toVector();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginLocation that = (PluginLocation) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Objects.equal(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(worldName, x, y, z);
    }

    /**
     * Provides serialization and deserialization for the {@link PluginLocation}.
     */
    static class ZLocationSerializer
            implements JsonSerializer<PluginLocation>, JsonDeserializer<PluginLocation> {
        @Override
        public JsonElement serialize(
            PluginLocation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("world", src.getWorldName());
            jsonObject.addProperty("x", src.getX());
            jsonObject.addProperty("y", src.getY());
            jsonObject.addProperty("z", src.getZ());
            jsonObject.addProperty("yaw", src.getYaw());
            jsonObject.addProperty("pitch", src.getPitch());
            return jsonObject;
        }

        @Override
        public PluginLocation deserialize(
                JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new PluginLocation(
                    jsonObject.get("world").getAsString(),
                    jsonObject.get("x").getAsDouble(),
                    jsonObject.get("y").getAsDouble(),
                    jsonObject.get("z").getAsDouble(),
                    jsonObject.get("yaw").getAsFloat(),
                    jsonObject.get("pitch").getAsFloat());
        }
    }
}
