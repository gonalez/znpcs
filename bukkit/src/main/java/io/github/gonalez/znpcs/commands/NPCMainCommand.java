package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import java.util.Collection;

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
    // help message (no child commands used)
    for (Command command : getChildren()) {
      ctx.log(command.getName());
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
