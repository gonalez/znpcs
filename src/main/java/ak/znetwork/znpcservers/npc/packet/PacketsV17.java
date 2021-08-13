package ak.znetwork.znpcservers.npc.packet;

import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.cache.CacheRegistry;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.Bukkit;

/**
 * @inheritDoc
 */
public class PacketsV17 extends PacketsV16 {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV17(NPC npc) {
        super(npc);
    }

    @Override
    public void updateSpawnPacket(Object nmsWorld) throws ReflectiveOperationException {
        playerSpawnPacket = CacheRegistry.PLAYER_CONSTRUCTOR_NEW.newInstance(CacheRegistry.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getNPC().getGameProfile());
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        Utils.setValue(packet, "n", CacheRegistry.ENUM_CHAT_FORMAT_FIND.invoke(null, getNPC().getNpcPojo().getGlowName()));
    }

    @Override
    public Object getClickType(Object interactPacket) {
        //
        return "INTERACT";
    }
}
