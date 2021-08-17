package ak.znetwork.znpcservers.npc.hologram;

import ak.znetwork.znpcservers.UnexpectedCallException;
import ak.znetwork.znpcservers.configuration.ConfigKey;
import ak.znetwork.znpcservers.configuration.ConfigValue;
import ak.znetwork.znpcservers.npc.hologram.replacer.LineReplacer;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.cache.CacheRegistry;
import ak.znetwork.znpcservers.configuration.ConfigTypes;
import ak.znetwork.znpcservers.user.ZUser;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hologram.
 */
public class Hologram {
    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * Determines if new line method should be used.
     */
    private static final boolean NEW_METHOD = Utils.BUKKIT_VERSION > 12;

    /**
     * The height between lines.
     */
    static final double LINE_SPACING = ConfigManager.getByType(ConfigKey.CONFIG).getValue(ConfigValue.LINE_SPACING);

    /**
     * A list of hologram lines.
     */
    private final List<HologramLine> hologramLines;

    /**
     * The npc.
     */
    private final NPC npc;

    /**
     * Creates a new {@link Hologram} for the given npc.
     *
     * @param npc The npc.
     */
    public Hologram(NPC npc) {
        this.npc = npc;
        hologramLines = new ArrayList<>();
    }

    /**
     * Called when creating a {@link Hologram}.
     */
    public void createHologram() {
        npc.getNpcViewers().forEach(this::delete);
        try {
            hologramLines.clear();
            double y = 0;
            final Location location = npc.getLocation();
            for (String line : npc.getNpcPojo().getHologramLines()) {
                boolean visible = !line.equalsIgnoreCase("%space%"); // determine if the line should be seen
                Object armorStand = CacheRegistry.ENTITY_CONSTRUCTOR.newInstance(CacheRegistry.GET_HANDLE_WORLD_METHOD.invoke(location.getWorld()),
                        location.getX(), (location.getY() - 0.15) + (y), location.getZ());
                if (visible) {
                    CacheRegistry.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(armorStand, true); // entity name is not visible by default
                    updateLine(line, armorStand, null);
                }
                CacheRegistry.SET_INVISIBLE_METHOD.invoke(armorStand, true);
                hologramLines.add(new HologramLine(line.replace(ConfigTypes.SPACE_SYMBOL, WHITESPACE),
                        armorStand, (Integer) CacheRegistry.GET_ENTITY_ID.invoke(armorStand)));
                y+=LINE_SPACING;
            }
            setLocation(location, 0);
            npc.getNpcViewers().forEach(this::spawn);
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
                Object entityPlayerPacketSpawn = CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(hologramLine.armorStand);
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
                Utils.sendPackets(user, npc.getPackets().getDestroyPacket(hologramLine.id));
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
                Utils.sendPackets(user, CacheRegistry.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(hologramLine.id,
                                CacheRegistry.GET_DATA_WATCHER_METHOD.invoke(hologramLine.armorStand), true));
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
                Object packet = CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(hologramLine.armorStand);
                npc.getNpcViewers().forEach(player -> Utils.sendPackets(player, packet));
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
                CacheRegistry.SET_LOCATION_METHOD.invoke(hologramLine.armorStand,
                        location.getX(), (location.getY() - 0.15) + y,
                        location.getZ(), location.getYaw(), location.getPitch());
                y+=LINE_SPACING;
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
            CacheRegistry.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, CacheRegistry.CRAFT_CHAT_MESSAGE_METHOD.invoke(null, LineReplacer.makeAll(user, line)));
        } else {
            CacheRegistry.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, LineReplacer.makeAll(user, line));
        }
    }

    /**
     * Used to create new lines for a {@link Hologram}.
     */
    static class HologramLine {
        /**
         * The hologram line string.
         */
        private final String line;

        /**
         * The hologram line entity.
         */
        private final Object armorStand;

        /**
         * The hologram line entity id.
         */
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
