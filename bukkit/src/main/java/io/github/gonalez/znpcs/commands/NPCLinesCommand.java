package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.context.Context;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCManager;
import io.github.gonalez.znpcs.util.Translation;
import java.util.Collection;
import java.util.Optional;

public class NPCLinesCommand extends Command {

  @Override
  public String getName() {
    return "lines";
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
    Optional<NPC> npcOptional = ctx.get(NPCManager.class).getNpc(id);
    if (!npcOptional.isPresent()) {
      return newCommandResult().errorKey("npc.not_found");
    }
    NPC npc = npcOptional.get();
    return newCommandResult()
        .setContext(Context.builder().put(NPC.class, npc).build())
        .successKey(Translation.get("command.success"));
  }

  @Override
  protected Collection<Command> getChildren() {
    return ImmutableList.of(
        new ClearLinesCommand(),
        new AddLinesCommand(),
        new RemoveLinesCommand(),
        new ListLinesCommand());
  }

  private static class ClearLinesCommand extends Command {

    @Override
    public String getName() {
      return "clear";
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }

    @Override
    protected CommandResult execute(
        CommandEnvironment env, Context ctx, ImmutableList<String> args) {
      return newCommandResult();
    }
  }

  private static class AddLinesCommand extends Command {

    @Override
    public String getName() {
      return "add";
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }

    @Override
    protected CommandResult execute(
        CommandEnvironment env, Context ctx, ImmutableList<String> args) {
      return newCommandResult();
    }
  }

  private static class RemoveLinesCommand extends Command {

    @Override
    public String getName() {
      return "remove";
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }

    @Override
    protected CommandResult execute(
        CommandEnvironment env, Context ctx, ImmutableList<String> args) {
      return newCommandResult();
    }
  }

  private static class ListLinesCommand extends Command {

    @Override
    public String getName() {
      return "list";
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }

    @Override
    protected CommandResult execute(
        CommandEnvironment env, Context ctx, ImmutableList<String> args) {
      NPC npc = ctx.get(NPC.class);
      return newCommandResult();
    }
  }
}
