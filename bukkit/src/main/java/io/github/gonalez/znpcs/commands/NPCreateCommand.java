package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.context.Context;

public class NPCreateCommand extends Command {

  @Override
  public String getName() {
    return "create";
  }

  @Override
  protected int getMandatoryArguments() {
    return 1;
  }

  @Override
  protected CommandResult execute(CommandEnvironment env, Context ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().errorKey("command.invalid_number");
    }
    String npcType = args.get(1).toUpperCase();
    String name = args.get(2).trim();
    if (name.length() < 3 || name.length() > 16) {
      return newCommandResult().errorKey("npc.invalid_name");
    }
    return newCommandResult().successKey("command.success");
  }
}
