package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.user.ZNPCUser;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class NPCManager {

    /**
     * A list of npc users.
     */
    private static final List<ZNPCUser> NPC_USERS = new ArrayList<>();

    /**
     * A list of npc users.
     *
     * @return A list of npc users.
     */
    public static List<ZNPCUser> getNpcUsers() {
        return NPC_USERS;
    }
}
