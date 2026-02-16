package io.github.gonalez.znpcs.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.configuration.MessagesConfiguration;
import io.github.gonalez.znpcs.npc.NPC;
import java.util.Collection;
import org.bukkit.ChatColor;

public final class NPCLinesCommand extends Command {

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
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfiguration.class).invalidNumber);
    }
    NPC npc = NPC.find(id);
    if (npc == null) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfiguration.class).npcNotFound);
    }
    return newCommandResult()
        .addDependency(NPC.class, npc)
        .setSuccessMessage(env.getConfig(MessagesConfiguration.class).success);
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
      NPC npc = env.getMergedCommandResult().getDependency(NPC.class);
      npc.getNpcPojo().getHologramLines().clear();
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
      NPC npc = env.getMergedCommandResult().getDependency(NPC.class);
      npc.getNpcPojo().getHologramLines().add(Joiner.on(' ').join(args.subList(0, args.size())));
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
      NPC npc = env.getMergedCommandResult().getDependency(NPC.class);
      npc.getNpcPojo().getHologramLines().clear();
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
      NPC npc = env.getMergedCommandResult().getDependency(NPC.class);
      ctx.log(ChatColor.YELLOW + "NPC Lines:");
      for (int i = 0; i < npc.getNpcPojo().getHologramLines().size(); i++) {
        ctx.log(ChatColor.translateAlternateColorCodes('&',
            String.format("&a%d &7- " + npc.getNpcPojo().getHologramLines().get(i), i)));
      }
      return newCommandResult();
    }
  }
}
