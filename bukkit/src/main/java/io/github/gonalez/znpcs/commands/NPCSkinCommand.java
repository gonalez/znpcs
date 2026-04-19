package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.context.Context;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCManager;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class NPCSkinCommand extends Command {

  @Override
  public String getName() {
    return "skin";
  }

  @Override
  protected int getMandatoryArguments() {
    return 2;
  }

  @Override
  protected CommandResult execute(CommandEnvironment env, Context ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().errorKey("command.invalid_number");
    }
    Optional<NPC> npcOptional = ctx.get(NPCManager.class).getNpc(id);
    if (!npcOptional.isPresent()) {
      return newCommandResult().errorKey("npc.not_found");
    }

    CommandSender commandSender = ctx.get(CommandSender.class);
    String skinName = args.get(1).trim();

    return newCommandResult().successKey("command.success");
  }
}
