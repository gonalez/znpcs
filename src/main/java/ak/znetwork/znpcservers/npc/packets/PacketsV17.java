package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;

import org.bukkit.Bukkit;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * V1.17+
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV17 extends PacketsV16 {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV17(ZNPC npc) {
        super(npc);
    }

    @Override
    public void updateSpawnPacket(Object nmsWorld) throws ReflectiveOperationException {
        playerSpawnPacket = ClassTypes.PLAYER_CONSTRUCTOR_NEW.newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getNPC().getGameProfile());
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        ReflectionUtils.setValue(packet, "n", ClassTypes.ENUM_CHAT_FORMAT_FIND.invoke(null, getNPC().getNpcPojo().getGlowName()));
    }

    @Override
    public Object getClickType(Object interactPacket) {
        //
        return "INTERACT";
    }
}
