package io.github.znetworkw.znpcservers.skin.internal;

import io.github.znetworkw.znpcservers.skin.SkinFetcher;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.skin.SkinFetcherService;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.SkinFetcher.SkinFetcherBuilder;

import java.util.function.Consumer;

/**
 * A basic, default implementation of the {@link SkinFetcherBuilder}
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultSkinFetcherBuilder implements SkinFetcher.SkinFetcherBuilder {
    private final AsyncHttpClient httpClient;
    private final SkinFetcherService fetcherServer;
    private final String skinName;
    private final int timeout;

    private DefaultSkinFetcherBuilder(
        AsyncHttpClient httpClient, SkinFetcherService fetcherServer,
        int timeout, String skinName) {
        this.httpClient = httpClient;
        this.fetcherServer = fetcherServer;
        this.timeout = timeout;
        this.skinName = skinName;
    }

    public DefaultSkinFetcherBuilder() {
        this(ZNPCs.SETTINGS.getAsyncHttpClient(),
            SkinFetcherService.of(HttpMethod.GET),
            5000,
            SkinFetcher.DEFAULT_SKIN_NAME);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withClient(AsyncHttpClient httpClient) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withSkin(String skinName) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withServer(SkinFetcherService fetcherServer) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withTimeout(int timeout) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, timeout, skinName);
    }

    @Override
    public SkinFetcher build() {
        return new DefaultAsyncSkinFetcher(httpClient, fetcherServer, timeout, skinName);
    }
}
