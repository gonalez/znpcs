package ak.znetwork.znpcservers.commands.impl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ZNCommandImpl {

    /**
     * Loads the command subcommands..
     */
    void load();

    class ZNCommandSender {

        /**
         * The command sender.
         */
        private final CommandSender commandSender;

        /**
         * Represents if the sender is a player or console.
         */
        private final SenderType type;

        /**
         * Creates a new command sender type.
         *
         * @param commandSender The command sender.
         */
        public ZNCommandSender(CommandSender commandSender) {
            this.commandSender = commandSender;
            this.type = commandSender instanceof Player ? SenderType.PLAYER : SenderType.CONSOLE;
        }

        /**
         * Sends a message to the command sender.
         *
         * @param message The message to send.
         */
        public void sendMessage(String message) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        /**
         * Returns the command sender as player.
         *
         * If the sender command is not represented by Player.class,
         * The method will throw an IllegalStateException.
         *
         * @return The player command sender.
         */
        public Player getPlayer() {
            if (type != SenderType.PLAYER)
                throw new IllegalStateException("Sender is not a player");

            return (Player) getCommandSender();
        }

        /**
         * Returns the command sender.
         *
         * @return The command sender
         */
        public CommandSender getCommandSender() {
            return commandSender;
        }

        /**
         * This enum represents the sender type,
         * that has executed a command.
         */
        enum SenderType {

            /**
             * Represents when a command has been executed by a player.
             */
            PLAYER,

            /**
             * Represents when a command has been executed by console.
             */
            CONSOLE
        }
    }

    /**
     * An annotation for sub commands as described in {@link ak.znetwork.znpcservers.commands.ZNCommand}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ZNCommandSub {

        /**
         * The possible command arguments.
         */
        String[] aliases();

        /**
         * The subcommand name.
         */
        String name();

        /**
         * The command permission.
         */
        String permission();
    }
}
