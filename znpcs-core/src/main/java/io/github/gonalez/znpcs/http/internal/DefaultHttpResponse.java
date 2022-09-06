package io.github.gonalez.znpcs.http.internal;

import io.github.gonalez.znpcs.http.HttpResponse;

import java.io.InputStream;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultHttpResponse implements HttpResponse {
    private final InputStream inputStream;

    private final int responseCode;
    private final String responseMessage;

    public DefaultHttpResponse(
        InputStream inputStream, int responseCode,
        String responseMessage) {
        this.inputStream = inputStream;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    @Override
    public InputStream inputStream() {
        return inputStream;
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    @Override
    public String responseMessage() {
        return responseMessage;
    }
}
