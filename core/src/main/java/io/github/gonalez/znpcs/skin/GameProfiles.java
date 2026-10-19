package io.github.gonalez.znpcs.skin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GameProfiles {
  static final String TEXTURES_PROPERTY_NAME = "textures";

  @Nonnull
  public static GameProfile newGameProfile(UUID uuid, String name, String value, String signature) {
    Property property = new Property(TEXTURES_PROPERTY_NAME, value, signature);
    // GameProfile and PropertyMap are immutable since authlib 7.
    PropertyMap properties =
        new PropertyMap(ImmutableMultimap.of(TEXTURES_PROPERTY_NAME, property));

    return new GameProfile(uuid, name, properties);
  }

  @Nullable
  public static Property getTextureProperty(GameProfile gameProfile) {
    return Iterables.getFirst(gameProfile.properties().get(TEXTURES_PROPERTY_NAME), null);
  }

  private GameProfiles() {}
}
