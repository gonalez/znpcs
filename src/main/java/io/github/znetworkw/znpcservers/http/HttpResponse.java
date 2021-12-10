package io.github.znetworkw.znpcservers.http;

import io.github.znetworkw.znpcservers.http.internal.DefaultHttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface HttpResponse {
    static HttpResponse of(InputStream inputStream, int responseCode, String responseMessage) {
        return new DefaultHttpResponse(inputStream, responseCode, responseMessage);
    }

    static HttpResponse of(HttpRequest request) throws IOException {
        return HttpClient.of().execute(request);
    }

    InputStream inputStream();
    int responseCode();
    String responseMessage();
}
