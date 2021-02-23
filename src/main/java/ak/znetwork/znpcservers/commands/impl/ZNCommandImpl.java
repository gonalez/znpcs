package ak.znetwork.znpcservers.commands.impl;

import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ZNCommandImpl {

    /**
     * Loads the command (subcommands).
     */
    void load();

    /**
     * Executes a subcommand for sender.
     *
     * @param commandSender               The commandSender to run the command-
     * @param args                        The command arguments.
     * @throws CommandPermissionException If commandSender does not have permission to execute the subCommand.
     * @throws CommandExecuteException    If subCommand cannot be executed.
     * @throws CommandNotFoundException   If no subCommand was found.
     */
    <S extends ZNCommandSender> void execute(S commandSender, String[] args) throws CommandNotFoundException, CommandPermissionException, CommandExecuteException;

    @Getter
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
            getCommandSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }

        /**
         * Gets the command sender as player.
         *
         * If the sender command is not represented by Player.class,
         * The method will throw an IllegalStateException.
         *
         * @return The player command sender.
         */
        public Player getPlayer() {
            if (getType() != SenderType.PLAYER)
                throw new IllegalStateException("Sender is not a player");

            return (Player) getCommandSender();
        }

        /**
         * This enum represents the sender type,
         * that has executed a command.
         */
        enum SenderType {

            /**
             * Represents when the command has been executed by a player.
             */
            PLAYER,

            /**
             * Represents when the command has been executed by console.
             */
            CONSOLE
        }
    }

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
