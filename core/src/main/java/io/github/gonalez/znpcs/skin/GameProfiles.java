package io.github.gonalez.znpcs.skin;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GameProfiles {
  static final String TEXTURES_PROPERTY_NAME = "textures";

  @Nonnull
  public static GameProfile newGameProfile(UUID uuid, String name, String value, String signature) {
    GameProfile gameProfile = new GameProfile(uuid, name);

    Property property = new Property(TEXTURES_PROPERTY_NAME, value, signature);
    gameProfile.getProperties().put(TEXTURES_PROPERTY_NAME, property);

    return gameProfile;
  }

  @Nullable
  public static Property getTextureProperty(GameProfile gameProfile) {
    return Iterables.getFirst(gameProfile.getProperties().get(TEXTURES_PROPERTY_NAME), null);
  }

  private GameProfiles() {}
}
