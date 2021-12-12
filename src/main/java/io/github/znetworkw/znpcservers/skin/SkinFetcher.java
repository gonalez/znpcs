package io.github.znetworkw.znpcservers.skin;

import static io.github.znetworkw.znpcservers.ZNPCs.SETTINGS;

import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.internal.DefaultSkinFetcherBuilder;

import java.util.function.Consumer;

/**
 * Interface for fetching skin results of a skin name.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface SkinFetcher {
    /**
     * Skin name used for default skins.
     */
    String DEFAULT_SKIN_NAME = "Notch";

    /**
     * Creates a new builder skin fetcher builder.
     *
     * @return a new skin fetcher builder.
     */
    static SkinFetcherBuilder builder() {
        return new DefaultSkinFetcherBuilder();
    }

    /**
     * Creates a new, default skin fetcher for the specified skin name.
     *
     * @param skin skin name.
     * @return a new skin fetcher for the specified skin name.
     */
    static SkinFetcher of(String skin) {
        return builder()
            .withSkin(skin)
            .withClient(SETTINGS.getAsyncHttpClient())
            .withServer(SkinFetcherService.of(skin.startsWith("http") ? HttpMethod.POST : HttpMethod.GET))
            .build();
    }

    /**
     * Fetches the skin result, if the fetch is success the method will invoke {@code onSuccess}
     * consumer otherwise the method will invoke {@code onError} consumer.
     *
     * @param onSuccess called when the fetch success.
     * @param onError called when the fetch failed.
     * @throws Exception if failed to get skin result.
     */
    void fetch(Consumer<SkinFetcherResult> onSuccess, Consumer<Throwable> onError) throws Exception;

    /**
     * Builder interface to create {@link SkinFetcher}s.
     */
    interface SkinFetcherBuilder {
        /**
         * Sets the fetcher skin name.
         *
         * @param name skin name.
         * @return this.
         */
        SkinFetcherBuilder withSkin(String name);

        /**
         * Sets the fetcher http client.
         *
         * @param httpClient fetcher http client.
         * @return this.
         */
        SkinFetcherBuilder withClient(AsyncHttpClient httpClient);

        /**
         * Sets the fetcher server.
         *
         * @param server fetcher server.
         * @return this.
         */
        SkinFetcherBuilder withServer(SkinFetcherService server);

        /**
         * Sets the fetcher timeout.
         *
         * @param timeout fetch timeout.
         * @return this.
         */
        SkinFetcherBuilder withTimeout(int timeout);

        /**
         * Creates a new skin fetcher based from this builder.
         *
         * @return a new skin fetcher based from this builder.
         */
        SkinFetcher build();
    }
}
