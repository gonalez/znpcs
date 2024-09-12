package io.github.gonalez.znpcs.skin;

import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static java.net.http.HttpClient.Redirect.ALWAYS;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.authlib.GameProfile;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class SkinFetcherImpl implements SkinFetcher {

  /** Receiver for output of {@link #fetchGameProfile}. */
  public interface SkinGameProfileCollector {
    void acceptSkinGameProfile(String name, GameProfile profile);
    void acceptSkinError(String name, Throwable t);
  }

  /** Builder for {@link SkinFetcherImpl}. */
  public static final class Builder {
    private Executor skinExecutor;
    private HttpClient httpClient;
    private final ImmutableList.Builder<SkinFetcherServer> serverBuilder = ImmutableList.builder();
    private Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector = Optional.empty();

    public Builder addSkinFetcherServer(SkinFetcherServer... skinFetcherServers) {
      serverBuilder.addAll(ImmutableList.copyOf(skinFetcherServers));
      return this;
    }

    public Builder setOptionalSkinGameProfileCollector(
        Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector) {
      this.optionalSkinGameProfileCollector = optionalSkinGameProfileCollector;
      return this;
    }

    public Builder setHttpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
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
      if (httpClient == null) {
        httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(ALWAYS)
            .proxy(ProxySelector.getDefault())
            .build();
      }
      return new SkinFetcherImpl(this);
    }
  }

  /** Returns a Builder for {@link SkinFetcherImpl}. */
  public static Builder builder() {
    return new Builder();
  }

  private final HttpClient httpClient;
  private final Executor executor;
  private final ImmutableList<SkinFetcherServer> skinFetcherServers;
  private Optional<SkinGameProfileCollector> optionalSkinGameProfileCollector;

  private SkinFetcherImpl(Builder builder) {
    this.httpClient = builder.httpClient;
    this.executor = builder.skinExecutor;
    this.skinFetcherServers = builder.serverBuilder.build();
    this.optionalSkinGameProfileCollector = builder.optionalSkinGameProfileCollector;
  }

  @Override
  public ListenableFuture<GameProfile> fetchGameProfile(
      String name, @Nullable SkinFetcherListener listener) {
    List<ListenableFuture<GameProfile>> allProfiles = getAllProfiles(name);
    ListenableFuture<GameProfile> fetchGameProfileFuture =
        Futures.transformAsync(
            Futures.whenAllComplete(allProfiles)
                .callAsync(() -> {
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
                }, executor),
            gameProfiles -> {
              if (!gameProfiles.isEmpty()) {
                GameProfile gameProfile = gameProfiles.get(0);
                if (listener != null) {
                  listener.onComplete(gameProfile);
                }
                return Futures.immediateFuture(gameProfile);
              }
              return Futures.immediateFailedFuture(new SkinException("No skin found for: " + name));
            }, executor);
    return Futures.catchingAsync(
        fetchGameProfileFuture,
        Exception.class,
        exception -> {
          if (listener != null) {
            listener.onError(exception);
          }
          return immediateFailedFuture(exception);
        }, executor);
  }

  private List<ListenableFuture<GameProfile>> getAllProfiles(String name) {
    List<ListenableFuture<GameProfile>> fetchedGameProfilesFuture = new ArrayList<>();
    for (SkinFetcherServer skinServer : skinFetcherServers) {
      try {
        HttpResponse<String> httpResponse = httpClient.send(
            skinServer.prepareRequest(name).build(), BodyHandlers.ofString());
        fetchedGameProfilesFuture.add(
            immediateFuture(skinServer.readProfile(name, httpResponse)));
      } catch (Exception e) {
        ListenableFuture<GameProfile> errorFuture =
            immediateFailedFuture(new SkinException(name, e));
        fetchedGameProfilesFuture.add(errorFuture);
      }
    }
    return fetchedGameProfilesFuture;
  }
}
