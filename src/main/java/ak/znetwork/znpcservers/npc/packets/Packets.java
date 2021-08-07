package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;

import ak.znetwork.znpcservers.npc.ZNPCType;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public abstract class Packets {
    /**
     * The npc.
     */
    private final ZNPC znpc;

    /**
     * The packet equipment values.
     */
    public final Map<String, Object> equipPackets;

    /**
     * The packet values.
     */
    public Object scoreboardSpawnPacket, scoreboardDeletePacket, playerSpawnPacket, playerDestroyPacket;

    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public Packets(ZNPC npc) {
        this.znpc = npc;
        equipPackets = new HashMap<>();
    }

    /**
     * Creates the npc spawn packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public abstract void updateSpawnPacket(Object nmsWorld) throws ReflectiveOperationException;

    /**
     * Updates the scoreboard team with the npc glow color.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public abstract void updateGlowPacket(Object packet) throws ReflectiveOperationException;

    /**
     * Creates the npc equip packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public abstract void updateEquipPacket(ZNPCSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException;

    /**
     * Returns the click type for the given interact packet.
     *
     * @param interactPacket The interact packet.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public abstract Object getClickType(Object interactPacket) throws ReflectiveOperationException;

    /**
     * Determines if the version support glow color.
     *
     * @return {@code true} If the version support glow color.
     */
    public abstract boolean allowGlowColor();

    /**
     * Creates the npc equip packet with no values.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public void updateEquipPacket() throws ReflectiveOperationException {
        updateEquipPacket(null, null);
    }

    /**
     * Returns the destroy packet for the entity id.
     *
     * @param entityId The entity id.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public Object getDestroyPacket(int entityId) throws ReflectiveOperationException {
        return ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getParameterTypes()[0].isArray() ? new int[]{entityId} : entityId);
    }

    /**
     * Updates the packets for the npc.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    public void update() throws ReflectiveOperationException {
        updateScoreboard();
    }

    /**
     * Updates the scoreboard packets for the npc.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    protected void updateScoreboard() throws ReflectiveOperationException {
        // +v1.17
        final boolean isVersion17 = Utils.BUKKIT_VERSION > 16;
        // +v1.9
        final boolean isVersion9 = Utils.BUKKIT_VERSION > 8;
        Object scoreboardTeamPacket = isVersion17 ?
                ClassTypes.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, getNPC().getGameProfile().getName()) : ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
        if (!isVersion17) {
            ReflectionUtils.setValue(scoreboardTeamPacket, "a", getNPC().getGameProfile().getName());
            ReflectionUtils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 1);
        }
        // set new scoreboard delete packet
        scoreboardDeletePacket = isVersion17 ? ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1.invoke(null, scoreboardTeamPacket) : scoreboardTeamPacket;
        if (isVersion17) {
            // new class for scoreboard add packet
            scoreboardTeamPacket = ClassTypes.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, getNPC().getGameProfile().getName());
            ReflectionUtils.setValue(scoreboardTeamPacket, "e", getNPC().getGameProfile().getName());
            ReflectionUtils.setValue(scoreboardTeamPacket, "l", ClassTypes.ENUM_TAG_VISIBILITY_NEVER.get(null));
        } else {
            // new class for scoreboard add packet
            scoreboardTeamPacket = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
            ReflectionUtils.setValue(scoreboardTeamPacket, "a", getNPC().getGameProfile().getName());
            ReflectionUtils.setValue(scoreboardTeamPacket, "e", "never");
            ReflectionUtils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 0);
        }
        // the entity list to update the scoreboard for (npc)
        Collection<String> collection = (Collection<String>) (isVersion17 ?
                ClassTypes.SCOREBOARD_PLAYER_LIST.invoke(scoreboardTeamPacket) : ReflectionUtils.getValue(scoreboardTeamPacket, isVersion9 ? "h" : "g"));
        if (getNPC().getNpcPojo().getNpcType() == ZNPCType.PLAYER) {
            collection.add(getNPC().getGameProfile().getName());
        } else {
            // non-player entities must be added with their uuid
            collection.add(getNPC().getUuid().toString());
        }
        // check if version support glow color and the npc has glow activated
        if (getNPC().getNpcPojo().isHasGlow() && allowGlowColor()) {
            // update scoreboard with glow
            updateGlowPacket(scoreboardTeamPacket);
        }
        scoreboardSpawnPacket = isVersion17 ? ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE.invoke(null, scoreboardTeamPacket, true) : scoreboardTeamPacket;
    }

    /**
     * Returns the npc.
     *
     * @return The npc.
     */
    public ZNPC getNPC() {
        return znpc;
    }

    /**
     * Returns the packet instance for the given version.
     *
     * @param npc     The npc.
     * @param version The current version.
     * @return The packet class for the given version.
     */
    public static Packets getByVersion(ZNPC npc,
                                       int version) {
        if (version > 16) {
            return new PacketsV17(npc);
        } else if (version > 15) {
            return new PacketsV16(npc);
        } else if (version > 8) {
            return new PacketsV9(npc);
        }
        return new PacketsV8(npc);
    }
}