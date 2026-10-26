/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class Command extends BukkitCommand {
  private static final String WHITESPACE = " ";
  private static final CommandMap COMMAND_MAP;
  private final Map<CommandInformation, CommandInvoker> subCommands =
      new HashMap<CommandInformation, CommandInvoker>();

  public Command(String name) {
    super(name);
    this.load();
  }

  private void load() {
    COMMAND_MAP.register(this.getName(), (org.bukkit.command.Command) this);
    for (Method method : ((Object) ((Object) this)).getClass().getMethods()) {
      if (!method.isAnnotationPresent(CommandInformation.class)) continue;
      CommandInformation cmdInfo = method.getAnnotation(CommandInformation.class);
      this.subCommands.put(cmdInfo, new CommandInvoker(this, method, cmdInfo.permission()));
    }
  }

  public Set<CommandInformation> getCommands() {
    return this.subCommands.keySet();
  }

  protected Map.Entry<CommandInformation, CommandInvoker> getSubcommandForArgs(String[] args) {
    return this.subCommands.entrySet().stream()
        .filter(
            command ->
                ((CommandInformation) command.getKey())
                    .name()
                    .contentEquals(args.length > 0 ? args[0] : ""))
        .findFirst()
        .orElse(null);
  }

  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    Map.Entry<CommandInformation, CommandInvoker> test = this.getSubcommandForArgs(args);
    if (test == null) {
      sender.sendMessage(ChatColor.RED + "Unknown subcommand for arguments.");
      return false;
    }
    try {
      ImmutableList list = ImmutableList.copyOf((Object[]) args);
      test.getValue()
          .execute(
              sender, ImmutableList.copyOf((Iterable) Iterables.skip((Iterable) list, (int) 1)));
    } catch (CommandExecuteException e) {
      sender.sendMessage(ChatColor.RED + "Failed to execute command.");
      e.printStackTrace();
    } catch (CommandPermissionException e) {
      sender.sendMessage(ChatColor.RED + "No permission.");
    }
    return true;
  }

  static {
    COMMAND_MAP = Bukkit.getCommandMap();
  }
}
