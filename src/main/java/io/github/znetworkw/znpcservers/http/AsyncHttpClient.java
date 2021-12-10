package io.github.znetworkw.znpcservers.http;

import com.google.common.util.concurrent.*;
import io.github.znetworkw.znpcservers.http.internal.DelegateAsyncHttpClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface AsyncHttpClient {
    /**
     * Creates a new, default async http client for the specified executor and http client.
     *
     * @param executorService the executor service to delegate work.
     * @param client the delegate http client.
     * @return a new, async http client.
     */
    static AsyncHttpClient of(ListeningExecutorService executorService, HttpClient client) {
        return new DelegateAsyncHttpClient(executorService, client);
    }

    /**
     * Convenience method for the {@link #of(ListeningExecutorService, HttpClient)}
     * method in one go using default information.
     *
     * @return a new, async http client.
     * @see #of(ListeningExecutorService, HttpClient) 
     */
    static AsyncHttpClient of() {
        return of(MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()), HttpClient.of());
    }

    /**
     * Executes the specified request, an {@code ListenableFuture} will be
     * returned representing the response for the request.
     *
     * @param request the request, containing the required information.
     * @return a future response for the specified request.
     */
    ListenableFuture<HttpResponse> executeAsync(HttpRequest request);

    /**
     * Convenience method to execute a callback when completing the submitted future for the
     * specified request, by default this method will execute the specified request using the
     * {@link #executeAsync(HttpRequest)} method, however is recommended to implement this method
     * and add your own logic.
     *
     * @param callback the callback to execute when the future is completed.
     * @return a future response for the specified request.
     * @see #executeAsync(HttpRequest)
     */
    default ListenableFuture<HttpResponse> executeAsyncWithCallback(HttpRequest request, FutureCallback<HttpResponse> callback) {
        final ListenableFuture<HttpResponse> executeAsync = executeAsync(request);
        Futures.addCallback(executeAsync, callback);
        return executeAsync;
    }

    /**
     * Shutdowns this async http client, terminating all pending work.
     */
    void shutdown();
}
