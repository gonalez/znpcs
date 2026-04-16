package io.github.gonalez.znpcs.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.util.Translation;
import java.util.Collection;
import org.bukkit.command.CommandSender;

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
  protected CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().errorKey("command.invalid_number");
    }
    NPC npc = NPC.find(id);
    if (npc == null) {
      return newCommandResult().errorKey("npc.not_found");
    }
    return newCommandResult()
        .setContextPropagator(b -> b.put(NPC.class, npc))
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
        CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      ctx.get(NPC.class).getNpcPojo().getHologramLines().clear();
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
        CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      ctx.get(NPC.class).getNpcPojo().getHologramLines().add(Joiner.on(' ').join(args.subList(0, args.size())));
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
        CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      ctx.get(NPC.class).getNpcPojo().getHologramLines().clear();
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
        CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      CommandSender commandSender = ctx.get(CommandSender.class);
      NPC npc = ctx.get(NPC.class);

      commandSender.sendMessage(Translation.get("command.list.header"));
      for (int i = 0; i < npc.getNpcPojo().getHologramLines().size(); i++) {
        String line = npc.getNpcPojo().getHologramLines().get(i);
        commandSender.sendMessage(
            Translation.get("command.list.line", i, line)
        );
      }

      return newCommandResult();
    }
  }
}
