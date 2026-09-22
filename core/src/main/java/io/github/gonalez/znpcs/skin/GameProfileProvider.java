package io.github.gonalez.znpcs.skin;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;

/** Provider interface for supplying GameProfile for a skin. */
public interface GameProfileProvider {
  /** Provides an GameProfile for the given name. */
  @Nullable
  GameProfile provideGameProfile(String name);
}
