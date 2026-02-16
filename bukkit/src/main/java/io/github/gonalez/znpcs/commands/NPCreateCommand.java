package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.config.MessagesConfig;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCType;

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
  protected CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).invalidNumber);
    }
    NPCType npcType;
    try {
      npcType = NPCType.valueOf(args.get(1).toUpperCase());
    } catch (IllegalArgumentException e) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).incorrectUsage);
    }
    String name = args.get(2).trim();
    if (name.length() < 3 || name.length() > 16) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).invalidNumber);
    }
    NPC npc = ServersNPC.createNPC(id, npcType, null, name);
    if (npcType == NPCType.PLAYER) {
      CommandResult skinCommandExecResult = env.provideCommand(NPCSkinCommand.class)
          .executeCommand(env, ctx, ImmutableList.of(String.valueOf(id), name));
      if (!skinCommandExecResult.hasError()) {
        // log
      }
    }
    return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).success);
  }
}
