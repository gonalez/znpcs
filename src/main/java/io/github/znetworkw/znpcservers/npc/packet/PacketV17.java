package io.github.znetworkw.znpcservers.npc.packet;

import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.Bukkit;

public class PacketV17 extends PacketV16 {

    @Override
    public int version() {
        return 17;
    }

    @Override
    public Object getPlayerPacket(Object nmsWorld, GameProfile gameProfile) throws ReflectiveOperationException {
        return CacheRegistry.PLAYER_CONSTRUCTOR_NEW.newInstance(CacheRegistry.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, gameProfile);
    }

    @Override
    public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
        Utils.setValue(packet, "n", CacheRegistry.ENUM_CHAT_FORMAT_FIND.invoke(null, npc.getNpcPojo().getGlowName()));
    }

    @Override
    public Object getClickType(Object interactPacket) {
        return "INTERACT";
    }
}
