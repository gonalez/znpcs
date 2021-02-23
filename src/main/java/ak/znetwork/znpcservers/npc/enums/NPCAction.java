package ak.znetwork.znpcservers.npc.enums;

import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
    CMD,

    /**
     * Represents an action executed by the console.
     */
    CONSOLE,

    /**
     * Represents an action executed by a player.
     */
    CHAT,

    /**
     * Represents sending a message to a player.
     */
    MESSAGE,

    /**
     * Represents sending a player to another server (Bungee).
     */
    SERVER;

    /**
     * Executes the appropriate method for provided action type.
     *
     * @param npcUser     The player instance.
     * @param player       The player
     * @param actionValue  The method value.
     */
    public void run(ZNPCUser npcUser, Player player, String actionValue) {
        if (this == CMD)
            player.performCommand(actionValue);
        else if (this == CONSOLE)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionValue);
        else if (this == CHAT)
            player.chat(actionValue);
        else if (this == MESSAGE)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', actionValue));
        else npcUser.getServersNPC().sendPlayerToServer(player, actionValue);
    }

    /**
     * Gets NPCAction by name.
     *
     * @param text The action type name.
     * @return Corresponding enum or null if not found.
     */
    public static NPCAction fromString(String text) {
        for (NPCAction b : NPCAction.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
