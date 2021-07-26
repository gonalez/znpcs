package ak.znetwork.znpcservers.hologram;

import ak.znetwork.znpcservers.hologram.replacer.LineReplacer;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.UnexpectedCallException;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
     * A list of entities (Holograms).
     */
    private final List<Object> entityArmorStands;

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
        entityArmorStands = new ArrayList<>();
        createHologram();
    }

    /**
     * Creation of the hologram.
     */
    public void createHologram() {
        npc.getViewers().forEach(this::delete);

        try {
            entityArmorStands.clear();

            double y = 0;
            for (String line : npc.getNpcPojo().getHologramLines()) {
                final boolean visible = line.equalsIgnoreCase("%space%");

                Object armorStand = ClassTypes.ENTITY_CONSTRUCTOR.newInstance(ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(location.getWorld()), location.getX(), (location.getY() - 0.15) + (y), location.getZ());
                ClassTypes.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(armorStand, !visible);
                if (visible) {
                    if (Utils.versionNewer(13)) {
                        ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, ClassTypes.CRAFT_CHAT_MESSAGE_METHOD.invoke(null, LineReplacer.makeAll(null, line)));
                    } else {
                        ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, LineReplacer.makeAll(null, line));
                    }
                }
                ClassTypes.SET_INVISIBLE_METHOD.invoke(armorStand, true);
                entityArmorStands.add(armorStand);

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
        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityPlayerPacketSpawn = ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(entityArmorStand);
                ReflectionUtils.sendPacket(player, entityPlayerPacketSpawn);
            } catch (ReflectiveOperationException operationException) {
                delete(player);
            }
        });
    }

    /**
     * Deletes the hologram for the given player.
     *
     * @param player The player to remove the hologram.
     */
    public void delete(Player player) {
        entityArmorStands.forEach(entityArmorStand -> {
            try {
                int armorStandId = (int) ClassTypes.GET_ENTITY_ID.invoke(entityArmorStand);

                ReflectionUtils.sendPacket(player, npc.getPackets().getDestroyPacket(armorStandId));
            } catch (ReflectiveOperationException operationException) {
                throw new UnexpectedCallException(operationException);
            }
        });
    }

    /**
     * Updates the hologram text.
     */
    public void updateNames(Player player) {
        final List<String> npcLines = npc.getNpcPojo().getHologramLines();
        for (int i = 0; i < npcLines.size(); i++) {
            final Object armorStand = entityArmorStands.get(i);
            try {
                final String line = npcLines.get(i).replace(ConfigTypes.SPACE_SYMBOL, " ");

                if (Utils.versionNewer(13)) {
                    ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, ClassTypes.CRAFT_CHAT_MESSAGE_METHOD.invoke(null, LineReplacer.makeAll(player, line)));
                } else {
                    ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, LineReplacer.makeAll(player, line));
                }

                Object dataWatcherObject = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(armorStand);

                int entity_id = (Integer) ClassTypes.GET_ENTITY_ID.invoke(armorStand);
                ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entity_id, dataWatcherObject, true));
            } catch (ReflectiveOperationException operationException) {
                throw new UnexpectedCallException(operationException);
            }
        }
    }

    /**
     * Updates the hologram location.
     */
    public void updateLocation() {
        entityArmorStands.forEach(o -> {
            try {
                Object packet = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(o);
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
            for (Object o : entityArmorStands) {
                ClassTypes.SET_LOCATION_METHOD.invoke(o, location.getX(), (location.getY() - 0.15) + y,
                        location.getZ(), location.getYaw(), location.getPitch());

                y+=HOLOGRAM_SPACE;
            }

            updateLocation();
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }
}
