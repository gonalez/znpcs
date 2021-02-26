package ak.znetwork.znpcservers.hologram;

import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.types.ConfigTypes;
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
     * The height between lines.
     */
    private static final double HOLOGRAM_SPACE = 0.3;

    /**
     * The line separator for hologram text.
     */
    private static final String HOLOGRAM_LINE_SEPARATOR = ":";

    /**
     * A list of entities (Holograms).
     */
    private final List<Object> entityArmorStands;

    /**
     * A set of viewers, represents the players who see the hologram.
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
                    String lines) {
        this.lines = lines.split(HOLOGRAM_LINE_SEPARATOR);
        this.location = location;

        this.entityArmorStands = new ArrayList<>();
        this.viewers = new HashSet<>();

        this.createHologram();
    }

    /**
     * Creation of the hologram.
     */
    public void createHologram() {
        getViewers().forEach(player -> delete(player, false));

        try {
            getEntityArmorStands().clear();

            double y = 0;
            for (String line : lines) {
                Object armorStand = ClassTypes.ENTITY_CONSTRUCTOR.newInstance(ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(getLocation().getWorld()), getLocation().getX(), (getLocation().getY() - 0.15) + (y), getLocation().getZ());

                ClassTypes.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(armorStand, line.length() >= 1);
                if (Utils.versionNewer(13))
                    ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, getStringNewestVersion(null, line));
                else
                    ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, ChatColor.translateAlternateColorCodes('&', line));

                ClassTypes.SET_INVISIBLE_METHOD.invoke(armorStand, true);

                getEntityArmorStands().add(armorStand);

                y+=HOLOGRAM_SPACE;
            }

            getViewers().forEach(player -> spawn(player, false));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Show npc for player.
     *
     * @param player                 The player to show the hologram.
     * @param addViewer              {@code true} If player should be added to viewers set.
     */
    public void spawn(Player player, boolean addViewer) {
        if (addViewer) getViewers().add(player);

        getEntityArmorStands().forEach(entityArmorStand -> {
            try {
                Object entityPlayerPacketSpawn = ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(entityArmorStand);
                ReflectionUtils.sendPacket(player, entityPlayerPacketSpawn);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
                getViewers().remove(player);

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
        if (remove) getViewers().remove(player);

        getEntityArmorStands().forEach(entityArmorStand -> {
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
        for (int i = 0; i < getLines().length; i++) {
            if (i >= getEntityArmorStands().size()) break;

            Object armorStand = getEntityArmorStands().get(i);
            try {
                String line = getLines()[i].replace(ConfigTypes.SPACE_SYMBOL, " ");

                if (Utils.versionNewer(13))
                    ClassTypes.SET_CUSTOM_NAME_NEW_METHOD.invoke(armorStand, getStringNewestVersion(player, getLines()[i]));
                else
                    ClassTypes.SET_CUSTOM_NAME_OLD_METHOD.invoke(armorStand, Utils.color(ConfigTypes.PLACEHOLDER_SUPPORT ? PlaceholderUtils.getWithPlaceholders(player, getLines()[i]) : line));

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
        getEntityArmorStands().forEach(o -> {
            try {
                Object packet = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(o);
                getViewers().forEach(player -> ReflectionUtils.sendPacket(player, packet));
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
        setLocation(location.clone().add(0, height, 0));

        try {
            double y = 0;
            for (Object o : entityArmorStands) {
                ClassTypes.SET_LOCATION_METHOD.invoke(o, getLocation().getX(), (getLocation().getY() - 0.15) + y,
                        getLocation().getZ(), getLocation().getYaw(), getLocation().getPitch());

                y+=HOLOGRAM_SPACE;
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

            return ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(ConfigTypes.PLACEHOLDER_SUPPORT && player != null ? PlaceholderUtils.getWithPlaceholders(player, text) : text.replace(ConfigTypes.SPACE_SYMBOL, " "));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }
}
