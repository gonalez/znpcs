package ak.znetwork.znpcservers.hologram;

import ak.znetwork.znpcservers.hologram.replacer.LineReplacer;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.UnexpectedCallException;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class Hologram {
    /**
     * The height between lines.
     */
    private static final double HOLOGRAM_SPACE = 0.3;

    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * Determines if new line method should be used.
     */
    private static final boolean NEW_METHOD = Utils.BUKKIT_VERSION > 12;

    /**
     * A list of hologram lines.
     */
    private final List<HologramLine> hologramLines;

    /**
     * The npc.
     */
    private final ZNPC npc;

    /**
     * The hologram location.
     */
    private Location location;

    /**
     * Creates a new hologram for the given npc.
     *
     * @param npc The npc.
     */
    public Hologram(ZNPC npc) {
        this.npc = npc;
        this.location = npc.getLocation();
        hologramLines = new ArrayList<>();
        createHologram();
    }

    /**
     * Creation of the hologram.
     */
    public void createHologram() {
        npc.getViewers().forEach(this::delete);
        try {
            hologramLines.clear();
            double y = 0;
            for (String line : npc.getNpcPojo().getHologramLines()) {
                // Determine if line should be seen
                boolean visible = !line.equalsIgnoreCase("%space%");
                Object armorStand = ClassTypes.ENTITY_CONSTRUCTOR.newInstance(ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(location.getWorld()),
                        location.getX(), (location.getY() - 0.15) + (y), location.getZ());
                if (visible) {
                    ClassTypes.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(armorStand, true); // Entity name is not visible by default
                    updateLine(line, armorStand, null);
                }
                ClassTypes.SET_INVISIBLE_METHOD.invoke(armorStand, true);
                hologramLines.add(new HologramLine(line.replace(ConfigTypes.SPACE_SYMBOL, WHITESPACE),
                        armorStand, (Integer) ClassTypes.GET_ENTITY_ID.invoke(armorStand)));
                y+=HOLOGRAM_SPACE;
            }
            setLocation(location, 0);
            npc.getViewers().forEach(this::spawn);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Spawn the hologram for the given player.
     *
     * @param player The player to show the hologram.
     */
    public void spawn(Player player) {
        hologramLines.forEach(hologramLine -> {
            try {
                Object entityPlayerPacketSpawn = ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(hologramLine.armorStand);
                ReflectionUtils.sendPacket(player, entityPlayerPacketSpawn);
            } catch (ReflectiveOperationException operationException) {
                delete(player);
            }
        });
    }

    /**
     * Deletes the hologram for the given player.
     *
     * @param player The player to remove the hologram for.
     */
    public void delete(Player player) {
        hologramLines.forEach(hologramLine -> {
            try {
                ReflectionUtils.sendPacket(player, npc.getPackets().getDestroyPacket(hologramLine.id));
            } catch (ReflectiveOperationException operationException) {
                throw new UnexpectedCallException(operationException);
            }
        });
    }

    /**
     * Updates the hologram text.
     */
    public void updateNames(Player player) {
        for (HologramLine hologramLine : hologramLines) {
            try {
                updateLine(hologramLine.line, hologramLine.armorStand, player);
                // Update the new line
                ReflectionUtils.sendPacket(player,
                        ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(hologramLine.id,
                                ClassTypes.GET_DATA_WATCHER_METHOD.invoke(hologramLine.armorStand), true));
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
                Object packet = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(hologramLine.armorStand);
                npc.getViewers().forEach(player -> ReflectionUtils.sendPacket(player, packet));
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
        this.location = location = location.clone().add(0, height, 0);
        try {
            double y = npc.getNpcPojo().getHologramHeight();
            for (HologramLine hologramLine : hologramLines) {
                ClassTypes.SET_LOCATION_METHOD.invoke(hologramLine.armorStand,
                        location.getX(), (location.getY() - 0.15) + y,
                        location.getZ(), location.getYaw(), location.getPitch());
                y+=HOLOGRAM_SPACE;
            }
            updateLocation();
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Updates a hologram line.
     *
     * @param line The line string.
     * @param armorStand The line entity.
     * @param player The player to update the line for.
     * @throws InvocationTargetException If cannot invoke method.
     * @throws IllegalAccessException If the method cannot be accessed.
     */
    private void updateLine(String line,
                            Object armorStand,
                            @Nullable Player player) throws InvocationTargetException, IllegalAccessException {
        Preconditions.checkNotNull(armorStand);
        Preconditions.checkNotNull(line);
        if (NEW_METHOD) {
            ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, ClassTypes.CRAFT_CHAT_MESSAGE_METHOD.invoke(null, LineReplacer.makeAll(player, line)));
        } else {
            ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, LineReplacer.makeAll(player, line));
        }
    }

    /**
     * Used to create new lines for each hologram.
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
