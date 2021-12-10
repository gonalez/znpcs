package io.github.znetworkw.znpcservers.skin;

import static io.github.znetworkw.znpcservers.ZNPCs.SETTINGS;

import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.internal.DefaultSkinFetcherBuilder;

import java.util.function.Consumer;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface SkinFetcher {
    String DEFAULT_SKIN_NAME = "Notch";

    /**
     * Creates a new builder for creating an skin fetcher.
     *
     * @return a new skin fetcher builder.
     */
    static SkinFetcherBuilder builder() {
        return new DefaultSkinFetcherBuilder();
    }

    static SkinFetcher of(String skin, Consumer<SkinFetcherResult> onSuccess, Consumer<Throwable> onError) {
        return builder()
            .withSkin(skin)
            .withClient(SETTINGS.getAsyncHttpClient())
            .withServer(SkinFetcherService.of(skin.startsWith("http") ?
                HttpMethod.POST : HttpMethod.GET))
            .onSuccess(onSuccess)
            .onError(onError)
            .build();
    }

    void request() throws Exception;

    SkinFetcherService getServer();

    interface SkinFetcherBuilder {
        SkinFetcherBuilder withClient(AsyncHttpClient httpClient);

        SkinFetcherBuilder withSkin(String skinName);

        SkinFetcherBuilder withServer(SkinFetcherService fetcherServer);

        SkinFetcherBuilder withTimeout(int timeout);

        SkinFetcherBuilder onSuccess(Consumer<SkinFetcherResult> onSuccess);

        SkinFetcherBuilder onError(Consumer<Throwable> onError);

        SkinFetcher build();
    }
}
