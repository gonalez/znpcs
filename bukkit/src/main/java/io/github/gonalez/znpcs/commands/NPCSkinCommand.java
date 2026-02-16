package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.config.Config;
import io.github.gonalez.znpcs.config.ConfigOption;
import io.github.gonalez.znpcs.config.MessagesConfig;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.skin.ApplySkinFetcherListener;
import io.github.gonalez.znpcs.skin.SkinFetcher;

public final class NPCSkinCommand extends Command {

  public static class Configuration extends Config {

    @ConfigOption(
        name = "skin_fetcher",
        description = "Skin fetcher is used for fetching skins"
    )
    public SkinFetcher skinFetcher;
  }


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
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).invalidNumber);
    }
    NPC npc = NPC.find(id);
    if (npc == null) {
      return newCommandResult().setErrorMessage(env.getConfig(MessagesConfig.class).invalidNumber);
    }

    String skinName = args.get(1).trim();

    ctx.log(env.getConfig(MessagesConfig.class).fetchingSkin, skinName);
    env.getConfig(Configuration.class).skinFetcher
        .fetchGameProfile(skinName, new ApplySkinFetcherListener(npc) {
          @Override
          public void onComplete(GameProfile gameProfile) {
            ctx.log(env.getConfig(MessagesConfig.class).getSkin, skinName);
            super.onComplete(gameProfile);
          }

          @Override
          public void onError(Throwable error) {
            ctx.log(env.getConfig(MessagesConfig.class).cantGetSkin, skinName);
          }
        });

    return newCommandResult().setSuccessMessage(env.getConfig(MessagesConfig.class).success);
  }
}
