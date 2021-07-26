package ak.znetwork.znpcservers.npc.packets.list;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;

import org.bukkit.Bukkit;

import java.util.*;

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
     * @param znpc The npc.
     */
    public PacketsV17(ZNPC znpc) {
        super(znpc);
    }

    @Override
    public void deleteScoreboard() throws ReflectiveOperationException {
        scoreboardDelete = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1.invoke(null,
                ClassTypes.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, getNPC().getGameProfile().getName()));
    }

    @Override
    public void spawnScoreboard() throws ReflectiveOperationException {
        scoreboardSpawn = ClassTypes.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, getNPC().getGameProfile().getName());
        ReflectionUtils.setValue(scoreboardSpawn, "e", getNPC().getGameProfile().getName());
        ReflectionUtils.setValue(scoreboardSpawn, "l", ClassTypes.ENUM_TAG_VISIBILITY_NEVER.get(null));
        ((Collection<String>) ClassTypes.SCOREBOARD_PLAYER_LIST.invoke(scoreboardSpawn)).add(getNPC().getGameProfile().getName());
        if (getNPC().getNpcPojo().isHasGlow()) {
            updateGlowPacket(scoreboardSpawn);
        }
        scoreboardSpawn = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE.invoke(null, scoreboardSpawn, true);
    }

    @Override
    public void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException {
        ReflectionUtils.setValue(scoreboardSpawn, "n", ClassTypes.ENUM_CHAT_FORMAT_FIND.invoke(null, getNPC().getNpcPojo().getGlowName()));
    }

    @Override
    public void getPlayerPacket(Object nmsWorld) throws ReflectiveOperationException {
        playerSpawnPacket = ClassTypes.PLAYER_CONSTRUCTOR_NEW.newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getNPC().getGameProfile());
    }

    @Override
    public Object getClickType(Object interactPacket) {
        return "INTERACT";
    }
}
