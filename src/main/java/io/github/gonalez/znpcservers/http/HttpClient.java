package io.github.gonalez.znpcservers.http;

import io.github.gonalez.znpcservers.http.internal.DefaultHttpClient;

import java.io.IOException;

public interface HttpClient {
    /**
     * Returns a default, http client.
     *
     * @return a default, http client.
     */
    static HttpClient of() {
        return DefaultHttpClient.INSTANCE;
    }

    /**
     * Executes the specified http request and return the request response.
     *
     * @param request the request, containing the required information.
     * @return a response based from the specified request.
     * @throws IOException if cannot execute the request.
     */
    HttpResponse execute(HttpRequest request) throws IOException;
}
