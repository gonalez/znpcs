package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.NPCPlugin;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.util.Translation;
import org.bukkit.command.CommandSender;

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
      return newCommandResult().errorKey("command.invalid_number");
    }
    NPCType npcType;
    try {
      npcType = NPCType.valueOf(args.get(1).toUpperCase());
    } catch (IllegalArgumentException e) {
      return newCommandResult().errorKey("npc.unknown_type");
    }
    String name = args.get(2).trim();
    if (name.length() < 3 || name.length() > 16) {
      return newCommandResult().errorKey("npc.invalid_name");
    }
    NPC npc = NPCPlugin.createNPC(id, npcType, null, name);
    if (npcType == NPCType.PLAYER) {
      CommandResult skinCommandExecResult = env.provideCommand(NPCSkinCommand.class)
          .executeCommand(env, ctx, ImmutableList.of(String.valueOf(id), name));
      if (!skinCommandExecResult.hasError()) {
        ctx.get(CommandSender.class).sendMessage(Translation.get("npc.skin_not_found"));
      }
    }
    return newCommandResult().successKey("command.success");
  }
}
