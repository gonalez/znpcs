package ak.znetwork.znpcservers.hologram;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.PlaceholderUtils;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter @Setter
public class Hologram {

    /**
     * A list of entities (Holograms).
     */
    private final List<Object> entityArmorStands;

    /**
     * A set of viewers, represents players who see the hologram.
     */
    private final HashSet<Player> viewers;

    /**
     * The hologram lines.
     */
    private String[] lines;

    /**
     * The hologram location.
     */
    private Location location;

    /**
     * Creates a new hologram.
     *
     * @param location The hologram location.
     * @param lines    The hologram text.
     */
    public Hologram(Location location,
                    String... lines) {
        this.entityArmorStands = new ArrayList<>();
        this.viewers = new HashSet<>();

        this.location = location;
        this.lines = lines;

        this.createHologram();
    }

    /**
     * Creation of the hologram.
     */
    public void createHologram() {
        viewers.forEach(player -> delete(player, false));

        try {
            entityArmorStands.clear();

            double y = 0;
            for (String line : lines) {
                Object armorStand = ClassTypes.ENTITY_CONSTRUCTOR.newInstance(ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(location.getWorld()), location.getX() + 0.5, (location.getY() - 0.15) + (y), location.getZ() + 0.5);

                ClassTypes.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(armorStand, line.length() >= 1);
                if (Utils.versionNewer(13))
                    ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, getStringNewestVersion(null, line));
                else
                    ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, ChatColor.translateAlternateColorCodes('&', line));

                ClassTypes.SET_INVISIBLE_METHOD.invoke(armorStand, true);

                entityArmorStands.add(armorStand);

                y += 0.3;
            }
            viewers.forEach(player -> spawn(player, false));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Show npc for player.
     *
     * @param player                 The player to show the hologram.
     * @param addViewer {@code true} If player should be added to viewers set.
     */
    public void spawn(Player player, boolean addViewer) {
        if (addViewer) viewers.add(player);

        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityPlayerPacketSpawn = ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(entityArmorStand);
                ReflectionUtils.sendPacket(player, entityPlayerPacketSpawn);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
                viewers.remove(player);

                throw new AssertionError(operationException);
            }
        });
    }

    /**
     * Delete/hide hologram for player.
     *
     * @param player The player to remove the hologram.
     */
    public void delete(Player player, boolean remove) {
        if (remove) viewers.remove(player);

        entityArmorStands.forEach(entityArmorStand -> {
            try {
                int armorStandId = (int) ClassTypes.GET_ENTITY_ID.invoke(entityArmorStand);

                ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(new int[]{armorStandId}));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
                throw new AssertionError(operationException);
            }
        });
    }

    /**
     * Updates the hologram text.
     */
    public void updateNames(Player player) {
        for (int i = 0; i < lines.length; i++) {
            if (i >= entityArmorStands.size()) break;

            Object armorStand = entityArmorStands.get(i);
            try {
                String line = lines[i].replace(ServersNPC.getReplaceSymbol(), " ");

                if (Utils.versionNewer(13))
                    ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, getStringNewestVersion(player, lines[i]));
                else
                    ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, Utils.color((ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player, lines[i]) : line)));

                Object dataWatcherObject = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(armorStand);

                int entity_id = (Integer) ClassTypes.GET_ENTITY_ID.invoke(armorStand);
                ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entity_id, dataWatcherObject, true));
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
                throw new AssertionError(operationException);
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
                viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
                throw new AssertionError("An exception occurred while trying to update location for hologram", operationException);
            }
        });
    }

    /**
     * Sets & updates the hologram location.
     *
     * @param location the new location.
     */
    public void setLocation(Location location, double height) {
        this.location = location.clone().add(0, height, 0);

        try {
            double y = 0;
            for (Object o : entityArmorStands) {
                ClassTypes.SET_LOCATION_METHOD.invoke(o, location.getBlockX() + 0.5, (this.location.getY() - 0.15) + y,
                       location.getBlockZ() + 0.5, location.getYaw(),location.getPitch());

                y += 0.3;
            }

            updateLocation();
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Get new hologram line for newer versions.
     *
     * @return The new hologram line.
     */
    public Object getStringNewestVersion(Player player, String text) {
        try {
            text = Utils.color(text);

            return ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance((ServersNPC.isPlaceHolderSupport() && player != null ? PlaceholderUtils.getWithPlaceholders(player, text) : text.replace(ServersNPC.getReplaceSymbol(), " ")));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Used to properly save hologram text in database.
     *
     * @return The hologram lines formatted.
     */
    public String getLinesFormatted() {
        return String.join(":", lines);
    }
}
