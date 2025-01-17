package io.github.gonalez.znpcs.skin;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCSkin;

public abstract class ApplySkinFetcherListener implements SkinFetcherListener {
  private final NPC npc;

  public ApplySkinFetcherListener(NPC npc) {
    this.npc = Preconditions.checkNotNull(npc);
  }

  @Override
  public void onComplete(GameProfile gameProfile) {
    Property textureProperty = GameProfiles.getTextureProperty(gameProfile);
    if (textureProperty != null) {
      npc.changeSkin(NPCSkin.forValues(
          textureProperty.getValue(), textureProperty.getSignature()));
    }
  }
}
