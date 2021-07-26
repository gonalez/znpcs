package ak.znetwork.znpcservers.npc.packets.list;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * v1.8 - 1.16
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV13 extends PacketsV9 {
    /**
     * Creates the packets for the given npc.
     *
     * @param znpc The npc.
     */
    public PacketsV13(ZNPC znpc) {
        super(znpc);
    }

    @Override
    public void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException {
        ReflectionUtils.setValue(packetTeam, "g", getNPC().getGlowColor());
        ReflectionUtils.setValue(packetTeam, "c", ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(getNPC().getGlowColor())));
    }
}
