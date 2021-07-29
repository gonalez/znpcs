package ak.znetwork.znpcservers.npc.packets.list;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.npc.packets.PacketsImpl;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public abstract class Packets implements PacketsImpl {
    /**
     * The npc.
     */
    private final ZNPC znpc;

    /**
     * The packet values.
     */
    public Object scoreboardSpawn, scoreboardDelete, playerSpawnPacket, destroyPacket;

    /**
     * The equipment values.
     */
    public final Map<String, Object> equipPackets;

    /**
     * Creates the packets for the given npc.
     *
     * @param znpc The npc.
     */
    public Packets(ZNPC znpc) {
        this.znpc = znpc;
        equipPackets = new HashMap<>();
    }

    /**
     * Returns the npc.
     *
     * @return The npc.
     */
    public ZNPC getNPC() {
        return znpc;
    }

    @Override
    public void update() throws ReflectiveOperationException {
        deleteScoreboard();
        spawnScoreboard();
    }

    @Override
    public void getEquipPacket(ZNPCSlot slot, ItemStack itemStack) throws ReflectiveOperationException {
        throw new IllegalStateException("Not supported for current version.");
    }

    @Override
    public void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException {
        throw new IllegalStateException("Not supported for current version.");
    }

    /**
     * Returns the packet class for the given version.
     *
     * @param znpc    The npc.
     * @param version The current version.
     * @return        The packet class for the given version.
     */
    public static Packets getByVersion(ZNPC znpc,
                                       int version) {
        if (version > 16) {
            return new PacketsV17(znpc);
        }    else if (version > 15) {
            return new PacketsV16(znpc);
        } else if (version > 12) {
            return new PacketsV13(znpc);
        } else if (version > 8) {
            return new PacketsV9(znpc);
        }
        return new PacketsV8(znpc);
    }
}