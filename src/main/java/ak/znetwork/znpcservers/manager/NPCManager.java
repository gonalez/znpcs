package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.user.ZNPCUser;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public final class NPCManager {

    /**
     * A list of npc users.
     */
    private final List<ZNPCUser> npcUsers;

    /**
     * A list of NPC.
     */
    private final List<ZNPC> npcList;

    /**
     * Initializes manager for NPCs.
     */
    public NPCManager() {
        this.npcUsers = new ArrayList<>();

        this.npcList = ConfigManager.getByType(ZNConfigType.DATA).getValue(ZNConfigValue.NPC_LIST);
    }
}
