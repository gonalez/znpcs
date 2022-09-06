package io.github.gonalez.znpcs.http.internal;

import io.github.gonalez.znpcs.http.HttpMethod;
import io.github.gonalez.znpcs.http.HttpRequest;

import java.net.URI;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultHttpRequest implements HttpRequest {
    private final URI uri;
    private final HttpMethod method;
    private final String body;
    private final int timeout;

    public DefaultHttpRequest(
        URI uri, HttpMethod method,
        String body, int timeout) {
        this.uri = uri;
        this.method = method;
        this.body = body;
        this.timeout = timeout;
    }

    @Override
    public URI uri() {
        return uri;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public int timeout() {
        return timeout;
    }
}
