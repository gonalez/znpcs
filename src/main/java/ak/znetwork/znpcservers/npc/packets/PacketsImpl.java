package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPCSlot;

import org.bukkit.inventory.ItemStack;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface PacketsImpl {
    /**
     * Updates the packets for the npc.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void update() throws ReflectiveOperationException;

    /**
     * Deletes the npc scoreboard.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void deleteScoreboard() throws ReflectiveOperationException;

    /**
     * Spawns the npc scoreboard.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void spawnScoreboard() throws ReflectiveOperationException;

    /**
     * Creates the npc player spawn packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void getPlayerPacket(Object nmsWorld) throws ReflectiveOperationException;

    /**
     * Creates the npc equip packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void getEquipPacket(ZNPCSlot slot, ItemStack itemStack) throws ReflectiveOperationException;

    /**
     * Creates the npc equip packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    default void getEquipPacket() throws ReflectiveOperationException {
        getEquipPacket(null, null);
    }

    /**
     * @inheritDoc
     */
    void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException;

    /**
     * Returns the npc destroy packet.
     *
     * @param entityId The npc entity id.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    Object getDestroyPacket(int entityId) throws ReflectiveOperationException;

    /**
     * Returns the click type for the given interact packet.
     *
     * @param interactPacket The interact packet.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    Object getClickType(Object interactPacket) throws ReflectiveOperationException;

}
