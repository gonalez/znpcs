package io.github.gonalez.znpcs.skin.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.util.concurrent.FutureCallback;
import io.github.gonalez.znpcs.skin.SkinFetcher;
import io.github.gonalez.znpcs.skin.SkinFetcherResult;
import io.github.gonalez.znpcs.skin.SkinFetcherService;
import io.github.gonalez.znpcs.http.AsyncHttpClient;
import io.github.gonalez.znpcs.http.HttpMethod;
import io.github.gonalez.znpcs.http.HttpRequest;
import io.github.gonalez.znpcs.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.function.Consumer;

/**
 * A default implementation of the {@link SkinFetcher}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultAsyncSkinFetcher implements SkinFetcher {
    private final AsyncHttpClient httpClient;
    private final SkinFetcherService fetcherServer;
    private final HttpMethod httpMethod;

    private final String skinName;
    private final int timeout;

    private final URI uri;

    public DefaultAsyncSkinFetcher(
        AsyncHttpClient httpClient, SkinFetcherService fetcherServer,
        int timeout, String skinName) {
        this.httpClient = httpClient;
        this.fetcherServer = fetcherServer;
        this.httpMethod = fetcherServer.getMethod();
        this.timeout = timeout;
        this.skinName = skinName;
        this.uri = URI.create(fetcherServer.getMethod() == HttpMethod.GET ?
            fetcherServer.getUrl() + "/" + skinName :
            fetcherServer.getUrl());
    }

    @Override
    public void fetch(Consumer<SkinFetcherResult> onSuccess, Consumer<Throwable> onError) throws Exception {
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
                    onSuccess.accept(fetcherServer.parse(reader));
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
}
