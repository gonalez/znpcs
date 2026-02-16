package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.configuration.MessagesConfiguration;
import io.github.gonalez.znpcs.npc.ItemSlot;
import org.bukkit.inventory.ItemStack;

public final class NPCEquipCommand extends Command {

  @Override
  public String getName() {
    return "equip";
  }

  @Override
  protected int getMandatoryArguments() {
    return 2;
  }

  @Override
  protected CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfiguration.class).invalidNumber);
    }
    ItemSlot itemSlot;
    try {
      itemSlot = ItemSlot.valueOf(args.get(1).toUpperCase());
    } catch (IllegalArgumentException e) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfiguration.class).incorrectUsage);
    }
    ItemStack equipItem = ((BukkitPlayerCommandContext)ctx).getPlayer().getInventory().getItemInHand();
    return newCommandResult().setSuccessMessage(env.getConfig(MessagesConfiguration.class).success);
  }
}
