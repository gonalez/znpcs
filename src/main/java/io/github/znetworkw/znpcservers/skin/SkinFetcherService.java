package io.github.znetworkw.znpcservers.skin;

import com.google.gson.JsonParser;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.internal.DefaultGetSkinServer;
import io.github.znetworkw.znpcservers.skin.internal.DefaultPostSkinServer;

import java.io.Reader;

/**
 * Service that provides parsing of skin results for the {@link SkinFetcher}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface SkinFetcherService {
    /**
     * Returns a default, skin fetcher service for the specified http method, if no
     * default service is found for the specified http method an {@link IllegalStateException}
     * will be thrown.
     *
     * @param httpMethod the http method.
     * @return a skin fetcher service for the given http method.
     * @throws IllegalStateException if no skin service is found for the specified http method.
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
     * @return url that will be used for skin calls.
     */
    String getUrl();

    /**
     * The http method that this service uses.
     *
     * @return http method that this service uses.
     */
    HttpMethod getMethod();

    /**
     * Parses the skin result from the given reader.
     *
     * @param reader the reader to deserialize the result from.
     * @return a skin result with the deserialized skin textures.
     */
    SkinFetcherResult parse(Reader reader);
}
