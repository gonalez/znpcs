package ak.znetwork.znpcservers.commands.impl;

import ak.znetwork.znpcservers.utility.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.base.Joiner;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

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

    /**
     * @inheritDoc
     */
    class ZNCommandSender {

        /**
         * Fix array spaces for bungee.
         */
        static final Joiner LINE_SEPARATOR_JOINER = Joiner.on("\n");

        /**
         * Start help message.
         */
        private static final ImmutableList<String> HELP_PREFIX =
                ImmutableList.of("&eExample &7(Ejemplo)"); // ... more

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
            sendMessage(message, null);
        }

        /**
         * Sends the command help message to the command sender.
         *
         * @param commandSub The command to get the message.
         */
        public void sendMessage(ZNCommandSub commandSub) {
            sendMessage(" &f&l* &6/&eznpcs " + commandSub.name() + " " + String.join(" ", commandSub.aliases()), Arrays.asList(commandSub.help()));
        }


        /**
         * Sends a hover-message to the command sender.
         *
         * @param message The message to send.
         */
        public void sendMessage(String message, Iterable<String> hover) {
            final TextComponent textMessage = new TextComponent(Utils.color(message));
            if (hover != null) {
                textMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(
                                Utils.color(LINE_SEPARATOR_JOINER.join(Iterables.concat(HELP_PREFIX, hover)))
                        ).create()
                ));
            }
            getPlayer().spigot().sendMessage(textMessage);
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
            if (type != SenderType.PLAYER) {
                throw new IllegalStateException("Sender is not a player.");
            }
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
         * The command help message.
         */
        String[] help() default {};

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
