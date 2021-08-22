package io.github.znetworkw.znpcservers.npc.packet;

import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.utility.Utils;
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
