package io.github.gonalez.znpcs.skin;

import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class SkinFetcherImpl implements SkinFetcher {

  /** Builder for {@link SkinFetcherImpl}. */
  public static final class Builder {
    private Executor skinExecutor;
    private final ImmutableList.Builder<GameProfileProvider> gameProfileProviderBuilder =
        ImmutableList.builder();
    private Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector = Optional.empty();

    public Builder addSkinFetcherServer(GameProfileProvider... providers) {
      gameProfileProviderBuilder.addAll(ImmutableList.copyOf(providers));
      return this;
    }

    public Builder setOptionalSkinGameProfileCollector(
        Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector) {
      this.optionalSkinGameProfileCollector = optionalSkinGameProfileCollector;
      return this;
    }

    public Builder withSyncExecutor() {
      this.skinExecutor = MoreExecutors.directExecutor();
      return this;
    }

    public Builder setSkinExecutor(Executor skinExecutor) {
      this.skinExecutor = skinExecutor;
      return this;
    }

    public SkinFetcherImpl build() {
      return new SkinFetcherImpl(this);
    }
  }

  /** Returns a Builder for {@link SkinFetcherImpl}. */
  public static Builder builder() {
    return new Builder();
  }

  /** Receiver for output of {@link #fetchGameProfile}. */
  public interface SkinGameProfileCollector {
    void acceptSkinGameProfile(String name, GameProfile profile);

    void acceptSkinError(String name, Throwable t);
  }

  private final Executor executor;
  private final ImmutableList<GameProfileProvider> gameProfileProviders;
  private Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector;

  private SkinFetcherImpl(Builder builder) {
    this.executor = builder.skinExecutor;
    this.gameProfileProviders = builder.gameProfileProviderBuilder.build();
    this.optionalSkinGameProfileCollector = builder.optionalSkinGameProfileCollector;
  }

  @Override
  public ListenableFuture<GameProfile> fetchGameProfile(
      String name, @Nullable SkinFetcherListener listener) {
    List<ListenableFuture<GameProfile>> allProfiles = getAllProfiles(name);
    ListenableFuture<GameProfile> fetchGameProfileFuture =
        Futures.transformAsync(
            Futures.whenAllComplete(allProfiles)
                .callAsync(
                    () -> {
                      List<GameProfile> retrievedGameProfiles = new ArrayList<>();
                      for (ListenableFuture<GameProfile> allProfile : allProfiles) {
                        try {
                          GameProfile gameProfile = Futures.getDone(allProfile);
                          if (gameProfile == null) {
                            continue;
                          }
                          retrievedGameProfiles.add(gameProfile);
                          optionalSkinGameProfileCollector.ifPresent(
                              collector -> collector.acceptSkinGameProfile(name, gameProfile));
                        } catch (ExecutionException e) {
                          if (e.getCause() instanceof SkinException) {
                            if (allProfiles.size() == 1) {
                              return immediateFailedFuture(e);
                            }
                          }
                          optionalSkinGameProfileCollector.ifPresent(
                              collector -> collector.acceptSkinError(name, e));
                        }
                      }
                      return immediateFuture(retrievedGameProfiles);
                    },
                    executor),
            gameProfiles -> {
              if (!gameProfiles.isEmpty()) {
                GameProfile gameProfile = gameProfiles.get(0);
                if (listener != null) {
                  listener.onComplete(gameProfile);
                }
                return Futures.immediateFuture(gameProfile);
              }
              return Futures.immediateFailedFuture(new SkinException("No skin found for: " + name));
            },
            executor);
    return Futures.catchingAsync(
        fetchGameProfileFuture,
        Exception.class,
        exception -> {
          if (listener != null) {
            listener.onError(exception);
          }
          return immediateFailedFuture(exception);
        },
        executor);
  }

  private List<ListenableFuture<GameProfile>> getAllProfiles(String name) {
    List<ListenableFuture<GameProfile>> fetchedGameProfilesFuture = new ArrayList<>();
    for (GameProfileProvider profileProvider : gameProfileProviders) {
      try {
        GameProfile gameProfile = profileProvider.provideGameProfile(name);
        if (gameProfile == null) {
          throw new SkinException(profileProvider + " returned null");
        }
        fetchedGameProfilesFuture.add(immediateFuture(gameProfile));
      } catch (Exception e) {
        ListenableFuture<GameProfile> errorFuture =
            immediateFailedFuture(new SkinException(name, e));
        fetchedGameProfilesFuture.add(errorFuture);
      }
    }
    return fetchedGameProfilesFuture;
  }
}
