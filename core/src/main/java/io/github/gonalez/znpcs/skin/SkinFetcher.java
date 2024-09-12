package io.github.gonalez.znpcs.skin;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;

public interface SkinFetcher {

  ListenableFuture<GameProfile> fetchGameProfile(
      String name, @Nullable SkinFetcherListener listener);
}
