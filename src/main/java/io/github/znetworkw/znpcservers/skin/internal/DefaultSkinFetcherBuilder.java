package io.github.znetworkw.znpcservers.skin.internal;

import io.github.znetworkw.znpcservers.skin.SkinFetcher;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.skin.SkinFetcherService;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.http.HttpMethod;

import java.util.function.Consumer;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultSkinFetcherBuilder implements SkinFetcher.SkinFetcherBuilder {
    private final AsyncHttpClient httpClient;
    private final SkinFetcherService fetcherServer;

    private final Consumer<SkinFetcherResult> resultConsumer;
    private final Consumer<Throwable> errorConsumer;

    private final String skinName;
    private final int timeout;

    private DefaultSkinFetcherBuilder(
        AsyncHttpClient httpClient, SkinFetcherService fetcherServer,
        Consumer<SkinFetcherResult> resultConsumer, Consumer<Throwable> errorConsumer,
        int timeout, String skinName) {
        this.httpClient = httpClient;
        this.fetcherServer = fetcherServer;
        this.resultConsumer = resultConsumer;
        this.errorConsumer = errorConsumer;
        this.timeout = timeout;
        this.skinName = skinName;
    }

    public DefaultSkinFetcherBuilder() {
        this(ZNPCs.SETTINGS.getAsyncHttpClient(),
            SkinFetcherService.of(HttpMethod.GET),
            (c) -> {},
            (c) -> {},
            5000,
            SkinFetcher.DEFAULT_SKIN_NAME);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withClient(AsyncHttpClient httpClient) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, resultConsumer, errorConsumer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withSkin(String skinName) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, resultConsumer, errorConsumer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withServer(SkinFetcherService fetcherServer) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, resultConsumer, errorConsumer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder withTimeout(int timeout) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, resultConsumer, errorConsumer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder onSuccess(Consumer<SkinFetcherResult> onSuccess) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, onSuccess, errorConsumer, timeout, skinName);
    }

    @Override
    public SkinFetcher.SkinFetcherBuilder onError(Consumer<Throwable> onError) {
        return new DefaultSkinFetcherBuilder(httpClient, fetcherServer, resultConsumer, onError, timeout, skinName);
    }


    @Override
    public SkinFetcher build() {
        return new DefaultAsyncSkinFetcher(httpClient, fetcherServer, resultConsumer, errorConsumer, timeout, skinName);
    }
}
