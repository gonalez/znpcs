package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.bukkit.command.CommandSender;

public class NPCMainCommand extends Command {

  @Override
  public String getName() {
    return "znpcs";
  }

  @Override
  protected int getMandatoryArguments() {
    return 0;
  }

  @Override
  protected CommandResult execute(
      CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
    for (Command command : getChildren()) {
      ctx.get(CommandSender.class).sendMessage(command.getName());
    }
    return newCommandResult();
  }

  @Override
  protected boolean executeOnChildrenFound() {
    return false;
  }

  @Override
  protected Collection<Command> getChildren() {
    return ImmutableList.of(
        new NPCreateCommand(),
        new NPCSkinCommand(),
        new NPCEquipCommand(),
        new NPCLinesCommand());
  }
}
