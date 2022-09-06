package io.github.gonalez.znpcs.http.internal;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import io.github.gonalez.znpcs.http.AsyncHttpClient;
import io.github.gonalez.znpcs.http.HttpClient;
import io.github.gonalez.znpcs.http.HttpRequest;
import io.github.gonalez.znpcs.http.HttpResponse;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DelegateAsyncHttpClient implements AsyncHttpClient {
    private final ListeningExecutorService executorService;
    private final HttpClient delegate;

    public DelegateAsyncHttpClient(ListeningExecutorService executorService, HttpClient delegate) {
        this.executorService = executorService;
        this.delegate = delegate;
    }

    @Override
    public ListenableFuture<HttpResponse> executeAsync(HttpRequest request) {
        return executorService.submit(() -> delegate.execute(request));
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
