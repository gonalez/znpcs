package io.github.gonalez.znpcs.model;

import com.google.common.collect.ComparisonChain;
import org.bukkit.Location;

public class WorldLocation extends Point {
  private final String worldName;
  private final float yaw;
  private final float pitch;

  public WorldLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
    super(x, y, z);
    this.worldName = worldName;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public static WorldLocation from(Location location) {
    return new WorldLocation(
        location.getWorld().getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch());
  }

  public String getWorldName() {
    return worldName;
  }

  public float getYaw() {
    return yaw;
  }

  public float getPitch() {
    return pitch;
  }

  @Override
  public int compareTo(Point o) {
    int result = super.compareTo(o);
    if (!(o instanceof WorldLocation) || result != 0) return result;
    WorldLocation other = (WorldLocation)o;
    return ComparisonChain.start()
        .compare(worldName, other.worldName)
        .compare(yaw, other.yaw)
        .compare(pitch, other.pitch)
        .result();
  }

  @Override
  public WorldLocation clone() {
    return new WorldLocation(worldName, getX(), getY(), getZ(), yaw, pitch);
  }
}
