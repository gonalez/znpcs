package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a sender that is dispatching a {@link Command}.
 */
public class CommandSender {
    /**
     * Fix array of spaces for bungee.
     */
    static final Joiner LINE_SEPARATOR_JOINER = Joiner.on("\n");

    /**
     * The default help message for the command.
     */
    private static final ImmutableList<String> HELP_PREFIX = ImmutableList.of("&6&lEXAMPLES&r:");

    /**
     * The bukkit command sender.
     */
    private final org.bukkit.command.CommandSender commandSender;

    /**
     * Represents if the sender is a player or console.
     */
    private final SenderType type;

    /**
     * Creates a new {@link CommandSender} for a bukkit sender.
     *
     * @param commandSender The command sender.
     */
    public CommandSender(org.bukkit.command.CommandSender commandSender) {
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
     * Sends the sub command help message to the command sender.
     *
     * @param subCommand The sub-command.
     */
    public void sendMessage(CommandInformation subCommand) {
        sendMessage(" &7» &6/&eznpcs " + subCommand.name() + " " +
                        Arrays.stream(subCommand.arguments())
                                .map(s -> "<" + s + ">")
                                .collect(Collectors.joining(" ")),
                Arrays.asList(subCommand.help()));
    }

    /**
     * Sends a hover message to the command sender.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message, Iterable<String> hover) {
        final TextComponent textComponent = new TextComponent();
        for (BaseComponent baseComponent : TextComponent.fromLegacyText(Utils.toColor(message))) {
            textComponent.addExtra(baseComponent);
        }
        // set hover event for help messages
        if (hover != null) {
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(Utils.toColor(LINE_SEPARATOR_JOINER
                            .join(Iterables.concat(HELP_PREFIX, hover))))
                            .create())
            );
        }
        getPlayer().spigot().sendMessage(textComponent);
    }

    /**
     * Returns the command sender as a {@link Player}.
     * If the sender is not a player the method will throw an {@link IllegalStateException}.
     *
     * @return The player command sender.
     * @throws IllegalStateException If the sender is not a player.
     */
    public Player getPlayer() {
        if (type != SenderType.PLAYER) {
            throw new IllegalStateException("Sender is not a player.");
        }
        return (Player) getCommandSender();
    }

    /**
     * Returns the bukkit command sender.
     *
     * @return The bukkit command sender
     */
    public org.bukkit.command.CommandSender getCommandSender() {
        return commandSender;
    }

    /**
     * This enum represents the sender type, that has executed the command.
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