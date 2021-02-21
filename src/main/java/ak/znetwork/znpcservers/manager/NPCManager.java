package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;

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
     * A list of NPC.
     */
    private final List<ZNPC> npcList;

    /**
     * A list of paths that are provided to NPCs.
     */
    private final List<ZNPCPathReader> npcPaths;

    /**
     * Initializes manager for NPCs.
     */
    public NPCManager() {
        this.npcList = new ArrayList<>();
        this.npcPaths = new ArrayList<>();
    }
}
