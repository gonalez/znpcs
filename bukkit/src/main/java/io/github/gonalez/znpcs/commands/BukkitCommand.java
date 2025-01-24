package io.github.gonalez.znpcs.commands;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public final class BukkitCommand extends Command {
  private final io.github.gonalez.znpcs.command.Command forward;
  private final CommandEnvironment commandEnvironment;

  public BukkitCommand(
      String name,
      io.github.gonalez.znpcs.command.Command forward,
      CommandEnvironment commandEnvironment) {
    super(name);
    this.forward = checkNotNull(forward);
    this.commandEnvironment = checkNotNull(commandEnvironment);
  }

  @Override
  public boolean execute(CommandSender sender, String name, String[] args) {
    CommandResult commandResult = commandEnvironment.executeCommand(forward, ImmutableList.copyOf(args));
    return !commandResult.hasError();
  }
}
