package ak.znetwork.znpcservers.npc.enums;

import ak.znetwork.znpcservers.user.ZNPCUser;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum NPCAction {

    /**
     * Represents an action executed by a player.
     */
    CMD {
        @Override
        public void run(ZNPCUser znpcUser, String actionValue) {
            znpcUser.toPlayer().performCommand(actionValue);
        }
    },

    /**
     * Represents an action executed by the console.
     */
    CONSOLE {
        @Override
        public void run(ZNPCUser znpcUser, String actionValue) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionValue);
        }
    },

    /**
     * Represents an action executed by a player.
     */
    CHAT {
        @Override
        public void run(ZNPCUser znpcUser, String actionValue) {
            znpcUser.toPlayer().chat(actionValue);
        }
    },

    /**
     * Represents sending a message to a player.
     */
    MESSAGE {
        @Override
        public void run(ZNPCUser znpcUser, String actionValue) {
            znpcUser.toPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', actionValue));
        }
    },

    /**
     * Represents sending a player to another server (Bungee).
     */
    SERVER {
        @Override
        public void run(ZNPCUser znpcUser, String actionValue) {
            znpcUser.getServersNPC().sendPlayerToServer(znpcUser.toPlayer(), actionValue);
        }
    };

    /**
     * Executes the appropriate method for provided action type.
     *
     * @param npcUser      The user instance.
     * @param actionValue  The action value.
     */
    public abstract void run(ZNPCUser npcUser, String actionValue);
}
