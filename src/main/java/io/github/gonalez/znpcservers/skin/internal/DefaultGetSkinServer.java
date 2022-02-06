package io.github.gonalez.znpcservers.skin.internal;

import com.google.gson.JsonObject;
import io.github.gonalez.znpcservers.http.HttpMethod;
import io.github.gonalez.znpcservers.skin.SkinFetcherResult;
import io.github.gonalez.znpcservers.skin.SkinFetcherService;

import java.io.Reader;

/**
 * A basic implementation of the {@link SkinFetcherService} for {@link HttpMethod#GET} requests.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultGetSkinServer implements SkinFetcherService {
    /**
     * Creates a new instance of this class.
     */
    public static SkinFetcherService INSTANCE = new DefaultGetSkinServer();

    @Override
    public String getUrl() {
        return "https://api.ashcon.app/mojang/v2/user";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public SkinFetcherResult parse(Reader reader) {
        JsonObject properties = JSON_PARSER.parse(reader)
            .getAsJsonObject()
            .getAsJsonObject("textures").getAsJsonObject("raw");
        return SkinFetcherResult.of(
            properties.get("value").getAsString(),
            properties.get("signature").getAsString());
    }
}
