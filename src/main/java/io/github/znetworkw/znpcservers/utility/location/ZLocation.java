package io.github.znetworkw.znpcservers.utility.location;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

/**
 * Simple struct class to hold information for an {@link Location}.
 */
public class ZLocation {
    public static final ZLocationSerializer SERIALIZER = new ZLocationSerializer();

    /** The location world name. */
    private final String worldName;

    /** The location world coordinates. */
    private final double x,y,z;

    /** The location yaw & pitch. */
    private final float yaw,pitch;

    /**
     * Represents the {@link Location} that this instance refers to.
     * @see #bukkitLocation()
     */
    private Location bukkitLocation;

    /**
     * Creates a {@link ZLocation} for the given values.
     *
     * @param worldName The location world name
     * @param x The location x-coordinate.
     * @param y The location y-coordinate.
     * @param z The location z-coordinate.
     * @param yaw The location yaw.
     * @param pitch The location pitch.
     */
    public ZLocation(String worldName,
                     double x,
                     double y,
                     double z,
                     float yaw,
                     float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Creates a {@link ZLocation} for the given bukkit location.
     *
     * @param location The bukkit location.
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
     * Returns the location yaw.
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Returns the location pitch.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Converts this instance to an {@link Location}.
     *
     * @return A {@link org.bukkit.Location} with the instance information.
     */
    public Location bukkitLocation() {
        if (bukkitLocation != null) {
            return bukkitLocation;
        }
        return bukkitLocation = new Location(
                Bukkit.getWorld(worldName),
                x,
                y,
                z,
                yaw,
                pitch
        );
    }

    /**
     * Converts the instance {@link #bukkitLocation} to an {@link Vector}.
     */
    public Vector toVector() {
        return bukkitLocation().toVector();
    }

    /**
     * Provides serialization and deserialization for a {@link ZLocation}.
     */
    static class ZLocationSerializer implements JsonSerializer<ZLocation>, JsonDeserializer<ZLocation> {

        @Override
        public JsonElement serialize(ZLocation src, Type typeOfSrc, JsonSerializationContext context) {
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
        public ZLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new ZLocation(jsonObject.get("world").getAsString(),
                    jsonObject.get("x").getAsDouble(),
                    jsonObject.get("y").getAsDouble(),
                    jsonObject.get("z").getAsDouble(),
                    jsonObject.get("yaw").getAsFloat(),
                    jsonObject.get("pitch").getAsFloat()
            );
        }
    }
}
