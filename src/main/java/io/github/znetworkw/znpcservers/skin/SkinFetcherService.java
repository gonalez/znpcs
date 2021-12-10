package io.github.znetworkw.znpcservers.skin;

import com.google.gson.JsonParser;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.internal.DefaultGetSkinServer;
import io.github.znetworkw.znpcservers.skin.internal.DefaultPostSkinServer;

import java.io.Reader;

/**
 * Service that contains the necessary information to get
 * and read skin results for an {@link SkinFetcher}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface SkinFetcherService {
    /**
     * Returns a default, skin fetcher service for the specified http method,
     * if no default service is found for the specified http method, an
     * {@link IllegalStateException} will be thrown as described.
     *
     * @param httpMethod the http method.
     * @return a default, skin fetcher service.
     * @throws IllegalStateException
     */
    static SkinFetcherService of(HttpMethod httpMethod) {
        if (httpMethod == HttpMethod.GET) {
            return DefaultGetSkinServer.INSTANCE;
        } else if (httpMethod == HttpMethod.POST) {
            return DefaultPostSkinServer.INSTANCE;
        } else {
            throw new IllegalStateException(
                String.format("no default skin service found for http method: %s", httpMethod.name()));
        }
    }

    /**
     * A json parser to parse skin results.
     */
    JsonParser JSON_PARSER = new JsonParser();

    /**
     * The service url that will be used to make the requests.
     *
     * @return url that will be used for the skin calls.
     */
    String getUrl();

    /**
     * The http method that this service uses.
     *
     * @return http method that this service uses.
     */
    HttpMethod getMethod();

    /**
     * Reads the skin result from the specified reader.
     *
     * @param reader the request reader to deserialize the result from.
     * @return a new skin result with the deserialized skin textures.
     */
    SkinFetcherResult read(Reader reader);
}
