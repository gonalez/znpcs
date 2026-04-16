package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.skin.ApplySkinFetcherListener;
import io.github.gonalez.znpcs.skin.SkinFetcher;
import io.github.gonalez.znpcs.util.Translation;
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
  protected CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      return newCommandResult().errorKey("command.invalid_number");
    }
    NPC npc = NPC.find(id);
    if (npc == null) {
      return newCommandResult().errorKey("npc.not_found");
    }

    CommandSender commandSender = ctx.get(CommandSender.class);
    String skinName = args.get(1).trim();

    ctx.get(SkinFetcher.class)
        .fetchGameProfile(skinName, new ApplySkinFetcherListener(npc) {
          @Override
          public void onComplete(GameProfile gameProfile) {
            super.onComplete(gameProfile);
            commandSender.sendMessage(Translation.get("npc.skin_fetched", skinName));
          }

          @Override
          public void onError(Throwable error) {
            commandSender.sendMessage(Translation.get("npc.skin_not_found", skinName));;
          }
        });
    return newCommandResult().successKey("command.success");
  }
}
