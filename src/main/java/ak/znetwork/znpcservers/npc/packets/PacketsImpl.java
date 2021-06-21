package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.ItemSlot;
import ak.znetwork.znpcservers.npc.packets.list.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface PacketsImpl {

    /**
     * @inheritDoc
     */
    void update() throws ReflectiveOperationException;

    /**
     * Deletes the npc scoreboard.
     */
    void deleteScoreboard() throws ReflectiveOperationException;

    /**
     * Shows the npc scoreboard.
     */
    void spawnScoreboard() throws ReflectiveOperationException;

    /**
     * @inheritDoc
     */
    void spawnPlayerPacket(Object nmsWorld) throws ReflectiveOperationException;

    /**
     * @inheritDoc
     */
    Object getClickType(Object interactPacket) throws ReflectiveOperationException;

    /**
     * Equip the npc.
     */
    void getEquipPacket(ItemSlot slot, ItemStack itemStack) throws ReflectiveOperationException;

    /**
     * @inheritDoc
     */
    default void getEquipPacket() throws ReflectiveOperationException {
        getEquipPacket(null, null);
    }

    /**
     * @inheritDoc
     */
    void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException;

    /**
     * @inheritDoc
     */
    abstract class Default implements PacketsImpl {

        /**
         * The npc.
         */
        private final ZNPC znpc;

        /**
         * @inheritDoc
         */
        public Object scoreboardSpawn, scoreboardDelete, playerSpawnPacket;

        /**
         * @inheritDoc
         */
        public final Map<String, Object> equipPackets;

        /**
         * Creates the packets for the given npc.
         *
         * @param znpc The npc.
         */
        public Default(ZNPC znpc) {
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
        public void getEquipPacket(ItemSlot slot, ItemStack itemStack) throws ReflectiveOperationException {
            throw new IllegalStateException("Not supported for current version.");
        }

        @Override
        public void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException {
            throw new IllegalStateException("Not supported for current version.");
        }

        /**
         * Finds the packet class for the given version.
         *
         * @param znpc The npc.
         * @param version The current version.
         * @return The packet class for the given version.
         */
        public static Default getByVersion(ZNPC znpc,
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
}
