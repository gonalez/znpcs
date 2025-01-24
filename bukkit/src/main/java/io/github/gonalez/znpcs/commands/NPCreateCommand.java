package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandException;
import io.github.gonalez.znpcs.command.CommandHooks;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.configuration.MessagesConfiguration;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCType;

public class NPCreateCommand extends Command {

  @Override
  public String getName() {
    return "create";
  }

  @Override
  protected CommandResult execute(
      ImmutableList<String> args, CommandEnvironment commandEnvironment, CommandHooks hooks)
      throws CommandException {
    MessagesConfiguration messagesConfig = hooks.getCommandConfigurationOrThrow(MessagesConfiguration.class);
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().setErrorMessage(messagesConfig.invalidNumber);
    }
    NPCType npcType;
    try {
      npcType = NPCType.valueOf(args.get(1).toUpperCase());
    } catch (IllegalArgumentException e) {
      return newCommandResult().setErrorMessage(messagesConfig.incorrectUsage);
    }
    String name = args.get(2).trim();
    if (name.length() < 3 || name.length() > 16) {
      return newCommandResult().setErrorMessage(messagesConfig.invalidNumber);
    }
    NPC npc = ServersNPC.createNPC(id, npcType, null, name);
    if (npcType == NPCType.PLAYER) {
      //commandProvider.getCommand(NPCSkinCommand.class).execute(commandProvider, ImmutableList.of(String.valueOf(id), name));
    }
    return newCommandResult().setSuccessMessage(messagesConfig.success);
  }

  @Override
  protected int getMandatoryArguments() {
    return 3;
  }
}
