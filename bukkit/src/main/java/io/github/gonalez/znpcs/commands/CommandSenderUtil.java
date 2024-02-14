package io.github.gonalez.znpcs.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.utility.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CommandSenderUtil {
  private static final Joiner LINE_SEPARATOR_JOINER = Joiner.on("\n");
  private static final ImmutableList<String> HELP_PREFIX = ImmutableList.of("&6&lEXAMPLES&r:");

  public static void sendMessage(CommandSender sender, CommandInformation subCommand) {
    sendMessage(sender, " &7» &6/&eznpcs " + subCommand.name() + " " +
            Arrays.stream(subCommand.arguments())
                .map(s -> "<" + s + ">")
                .collect(Collectors.joining(" ")),
        Arrays.asList(subCommand.help()));
  }

  public static void sendMessage(
      CommandSender sender, String message, Iterable<String> hover) {
    message = Utils.toColor(message);
    if (sender instanceof Player) {
      TextComponent textComponent = new TextComponent(TextComponent.fromLegacyText(Utils.toColor(message)));
      if (hover != null)
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder(
            Utils.toColor(LINE_SEPARATOR_JOINER
                .join(Iterables.concat(HELP_PREFIX, hover)))))
            .create()));
      ((Player)sender).spigot().sendMessage(textComponent);
    } else {
      sender.sendMessage(message);
    }
  }

  private CommandSenderUtil() {}
}
