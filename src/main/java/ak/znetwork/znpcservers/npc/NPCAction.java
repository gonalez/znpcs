package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.event.type.ClickType;
import ak.znetwork.znpcservers.user.ZUser;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Represents a action for a {@link NPC} being ran by an {@link ZUser}.
 */
public class NPCAction {
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
     * The action delay to be able to be executed again for a user.
     */
    private int delay;

    /**
     * Creates a new {@link NPCAction}.
     *
     * @param actionType The action type.
     * @param clickType The action click type.
     * @param action The action value.
     * @param delay The action delay.
     */
    public NPCAction(ActionType actionType,
                     ClickType clickType,
                     String action,
                     int delay) {
        this.actionType = actionType;
        this.clickType = clickType;
        this.action = action;
        this.delay = delay;
    }

    /**
     * Creates a new {@link NPCAction}.
     *
     * @param actionType The action type.
     * @param action The action value.
     */
    public NPCAction(String actionType,
                     String action) {
        this(ActionType.valueOf(actionType), ClickType.DEFAULT, action, 0);
    }

    /**
     * Returns the action type.
     *
     * @return The action type.
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * Returns the action click type.
     *
     * @return The action click type.
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * Returns the action value.
     *
     * @return The action value.
     */
    public String getAction() {
        return action;
    }

    /**
     * Returns the action delay.
     *
     * @return The action delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the {@link #getDelay()} ()} of this action.
     *
     * @param delay The new delay.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * @inheritDoc
     */
    public long getFixedDelay() {
        return Utils.SECOND_INTERVAL_NANOS * delay;
    }

    /**
     * Executes the action for the given user.
     *
     * @param user The user player that interacted with the npc.
     * @param action The action value.
     */
    public void run(ZUser user, String action) {
        actionType.run(user, Utils.PLACEHOLDER_SUPPORT ? Utils.getWithPlaceholders(action, user.toPlayer()) : action);
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
            public void run(ZUser user, String actionValue) {
                user.toPlayer().performCommand(actionValue);
            }
        },
        /**
         * Represents an action executed by the console.
         */
        CONSOLE {
            @Override
            public void run(ZUser user, String actionValue) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionValue);
            }
        },
        /**
         * Represents an action executed by a player.
         */
        CHAT {
            @Override
            public void run(ZUser user, String actionValue) {
                user.toPlayer().chat(actionValue);
            }
        },
        /**
         * Represents sending a message to a player.
         */
        MESSAGE {
            @Override
            public void run(ZUser user, String actionValue) {
                user.toPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', actionValue));
            }
        },
        /**
         * Represents sending a player to another server (BungeeCord).
         */
        SERVER {
            @Override
            public void run(ZUser user, String actionValue) {
                ServersNPC.BUNGEE_UTILS.sendPlayerToServer(user.toPlayer(), actionValue);
            }
        };

        /**
         * Performs the action.
         * <p>
         * This method is called when the {@code user} interacts with an {@link NPC}
         * that has a action of this type.
         * <p>
         */
        public abstract void run(ZUser user, String actionValue);
    }
}
