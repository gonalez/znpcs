package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.context.Context;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NPCEquipCommand extends Command {

  @Override
  public String getName() {
    return "equip";
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
    ItemStack equipItem = ctx.get(Player.class).getInventory().getItemInHand();
    return newCommandResult().successKey("command.success");
  }
}
