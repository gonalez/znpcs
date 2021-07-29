package ak.znetwork.znpcservers.npc.model;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.events.type.ClickType;
import ak.znetwork.znpcservers.user.ZNPCUser;

import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import lombok.Data;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Data
public class ZNPCAction {
    /**
     * The action type.
     */
    private final ActionType actionType;

    /**
     * The action click type.
     */
    private final ClickType clickType;

    /**
     * The action value.
     */
    private final String action;

    /**
     * The action delay (in seconds).
     */
    private int delay;

    /**
     * Creates a new action.
     *
     * @param actionType The action type.
     * @param clickType The action click type.
     * @param action The action value.
     * @param delay The action delay.
     */
    public ZNPCAction(ActionType actionType,
                      ClickType clickType,
                      String action,
                      int delay) {
        this.actionType = actionType;
        this.clickType = clickType;
        this.action = action;
        this.delay = delay;
    }

    /**
     * Creates a new action for the provided action name.
     *
     * @param actionType The action type.
     * @param action The action value.
     */
    public ZNPCAction(String actionType,
                      String action) {
        this(ActionType.valueOf(actionType), ClickType.DEFAULT, action, 0);
    }

    /**
     * Executes the action type with the correct value placeholders.
     *
     * @param npcUser The player that clicked the npc.
     * @param action  The action value.
     */
    public void run(ZNPCUser npcUser, String action) {
        actionType.run(npcUser,
                Utils.PLACEHOLDER_SUPPORT ?
                        Utils.getWithPlaceholders(npcUser.toPlayer(), action) :
                        action
                );
    }

    /**
     * Determines the action type to run when an npc is clicked.
     */
    enum ActionType {
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
                ServersNPC.BUNGEE_UTILS.sendPlayerToServer(znpcUser.toPlayer(), actionValue);
            }
        };

        /**
         * Executes the appropriate method for the provided action type.
         *
         * @param npcUser      The user instance.
         * @param actionValue  The action value.
         */
        public abstract void run(ZNPCUser npcUser, String actionValue);
    }
}
