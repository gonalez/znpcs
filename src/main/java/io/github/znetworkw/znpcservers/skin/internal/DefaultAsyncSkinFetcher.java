package io.github.znetworkw.znpcservers.skin.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.util.concurrent.FutureCallback;
import io.github.znetworkw.znpcservers.skin.SkinFetcher;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.skin.SkinFetcherService;
import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.http.HttpRequest;
import io.github.znetworkw.znpcservers.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.function.Consumer;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultAsyncSkinFetcher implements SkinFetcher {
    private final AsyncHttpClient httpClient;
    private final SkinFetcherService fetcherServer;

    private final HttpMethod httpMethod;

    private final Consumer<SkinFetcherResult> onSuccess;
    private final Consumer<Throwable> onError;

    private final String skinName;
    private final int timeout;

    private final URI uri;

    public DefaultAsyncSkinFetcher(
        AsyncHttpClient httpClient, SkinFetcherService fetcherServer,
        Consumer<SkinFetcherResult> onSuccess, Consumer<Throwable> onError,
        int timeout, String skinName) {
        this.httpClient = httpClient;
        this.fetcherServer = fetcherServer;
        this.httpMethod = fetcherServer.getMethod();
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.timeout = timeout;
        this.skinName = skinName;
        this.uri = URI.create(fetcherServer.getMethod() == HttpMethod.GET ?
            fetcherServer.getUrl() + "/" + skinName :
            fetcherServer.getUrl());
    }

    @Override
    public void request() throws Exception {
        final HttpRequest httpRequest;
        if (httpMethod == HttpMethod.POST) {
            String write = "url=" + URLEncoder.encode(skinName, "UTF-8");
            httpRequest = HttpRequest.of(uri, httpMethod, write, timeout);
        } else {
            httpRequest = HttpRequest.of(uri, httpMethod, "", timeout);
        }
        httpClient.executeAsyncWithCallback(httpRequest, new FutureCallback<HttpResponse>() {
            @Override
            public void onSuccess(HttpResponse response) {
                try (InputStreamReader reader = new InputStreamReader(response.inputStream(), UTF_8)) {
                    onSuccess.accept(fetcherServer.read(reader));
                } catch (IOException e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                onError.accept(throwable);
            }
        });
    }

    @Override
    public SkinFetcherService getServer() {
        return fetcherServer;
    }
}
