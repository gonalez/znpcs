package io.github.gonalez.znpcs.npc.hologram;

import io.github.gonalez.znpcs.UnexpectedCallException;
import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.hologram.replacer.LineReplacer;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.Utils;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Hologram {
  private static final String WHITESPACE = " ";
  
  private static final boolean NEW_METHOD = (Utils.BUKKIT_VERSION > 12);

  private final List<HologramLine> hologramLines = new ArrayList<>();
  
  private final NPC npc;

  public Hologram(NPC npc) {
    this.npc = npc;
  }

  /**
   * Called when creating a {@link Hologram}.
   */
  public void createHologram() {
    npc.getViewers().forEach(this::delete);
    try {
      hologramLines.clear();
      double y = 0;
      final Location location = npc.getLocation();
      for (String line : npc.getNpcPojo().getHologramLines()) {
        boolean visible = !line.equalsIgnoreCase("%space%"); // determine if the line should be seen
        Object armorStand = CacheRegistry.ENTITY_CONSTRUCTOR.load().newInstance(CacheRegistry.GET_HANDLE_WORLD_METHOD.load().invoke(location.getWorld()),
            location.getX(), (location.getY() - 0.15) + (y), location.getZ());
        if (visible) {
          CacheRegistry.SET_CUSTOM_NAME_VISIBLE_METHOD.load().invoke(armorStand, true); // entity name is not visible by default
          updateLine(line, armorStand, null);
        }
        CacheRegistry.SET_INVISIBLE_METHOD.load().invoke(armorStand, true);
        hologramLines.add(new HologramLine(line.replace(ZNPConfigUtils.getConfig(ConfigConfiguration.class).replaceSymbol, WHITESPACE),
            armorStand, (Integer) CacheRegistry.GET_ENTITY_ID.load().invoke(armorStand)));
        y+= ZNPConfigUtils.getConfig(ConfigConfiguration.class).lineSpacing;
      }
      setLocation(location, 0);
      npc.getPackets().flushCache("getHologramSpawnPacket");
      npc.getViewers().forEach(this::spawn);
    } catch (ReflectiveOperationException operationException) {
      throw new UnexpectedCallException(operationException);
    }
  }

  /**
   * Spawns the hologram for the given player.
   *
   * @param user The player to spawn the hologram for.
   */
  public void spawn(ZUser user) {
    hologramLines.forEach(hologramLine -> {
      try {
        Object entityPlayerPacketSpawn = npc.getPackets().getProxyInstance()
            .getHologramSpawnPacket(hologramLine.armorStand);
        Utils.sendPackets(user, entityPlayerPacketSpawn);
      } catch (ReflectiveOperationException operationException) {
        delete(user);
      }
    });
  }

  /**
   * Deletes the hologram for the given player.
   *
   * @param user The player to remove the hologram for.
   */
  public void delete(ZUser user) {
    hologramLines.forEach(hologramLine -> {
      try {
        Utils.sendPackets(user, npc.getPackets().getProxyInstance().getDestroyPacket(hologramLine.id));
      } catch (ReflectiveOperationException operationException) {
        throw new UnexpectedCallException(operationException);
      }
    });
  }

  /**
   * Updates the hologram text for the given player.
   *
   * @param user The player to update the hologram for.
   */
  public void updateNames(ZUser user) {
    for (HologramLine hologramLine : hologramLines) {
      try {
        updateLine(hologramLine.line, hologramLine.armorStand, user);
        // update the line
        Object metaData = npc.getPackets().getProxyInstance().getMetadataPacket(hologramLine.id, hologramLine.armorStand);
        Utils.sendPackets(
            user,
            metaData);
      } catch (ReflectiveOperationException operationException) {
        throw new UnexpectedCallException(operationException);
      }
    }
  }

  /**
   * Updates the hologram location.
   */
  public void updateLocation() {
    hologramLines.forEach(hologramLine -> {
      try {
        Object packet = CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.load().newInstance(hologramLine.armorStand);
        npc.getViewers().forEach(player -> Utils.sendPackets(player, packet));
      } catch (ReflectiveOperationException operationException) {
        throw new UnexpectedCallException(operationException);
      }
    });
  }

  /**
   * Sets & updates the hologram location.
   *
   * @param location The new location.
   */
  public void setLocation(Location location, double height) {
    location = location.clone().add(0, height, 0);
    try {
      double y = npc.getNpcPojo().getHologramHeight();
      for (HologramLine hologramLine : hologramLines) {
        CacheRegistry.SET_LOCATION_METHOD.load().invoke(hologramLine.armorStand,
            location.getX(), (location.getY() - 0.15) + y,
            location.getZ(), location.getYaw(), location.getPitch());
        y+=ZNPConfigUtils.getConfig(ConfigConfiguration.class).lineSpacing;
      }
      updateLocation();
    } catch (ReflectiveOperationException operationException) {
      throw new UnexpectedCallException(operationException);
    }
  }

  /**
   * Updates a hologram line.
   *
   * @param line The new hologram line.
   * @param armorStand The hologram entity line.
   * @param user The player to update the line for.
   * @throws InvocationTargetException If cannot invoke method.
   * @throws IllegalAccessException If the method cannot be accessed.
   */
  private void updateLine(String line,
                          Object armorStand,
                          @Nullable ZUser user) throws InvocationTargetException, IllegalAccessException {
    if (NEW_METHOD) {
      CacheRegistry.SET_CUSTOM_NAME_NEW_METHOD.load().invoke(armorStand, CacheRegistry.CRAFT_CHAT_MESSAGE_METHOD.load().invoke(null, LineReplacer.makeAll(user, line)));
    } else {
      CacheRegistry.SET_CUSTOM_NAME_OLD_METHOD.load().invoke(armorStand, LineReplacer.makeAll(user, line));
    }
  }

  /**
   * Used to create new lines for a {@link Hologram}.
   */
  private static class HologramLine {
    /** The hologram line string. */
    private final String line;
    /** The hologram line entity. */
    private final Object armorStand;
    /** The hologram line entity id. */
    private final int id;

    /**
     * Creates a new line for the hologram.
     *
     * @param line The hologram line string.
     * @param armorStand The hologram entity.
     * @param id The hologram entity id.
     */
    protected HologramLine(String line,
                           Object armorStand,
                           int id) {
      this.line = line;
      this.armorStand = armorStand;
      this.id = id;
    }
  }
}
