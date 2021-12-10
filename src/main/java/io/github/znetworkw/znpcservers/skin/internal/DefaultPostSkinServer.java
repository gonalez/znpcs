package io.github.znetworkw.znpcservers.skin.internal;

import com.google.gson.JsonObject;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.SkinFetcherService;

import java.io.Reader;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPostSkinServer implements SkinFetcherService {
    public static SkinFetcherService INSTANCE = new DefaultPostSkinServer();

    @Override
    public String getUrl() {
        return "https://api.mineskin.org/generate/url";
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public SkinFetcherResult read(Reader reader) {
        JsonObject texture = JSON_PARSER.parse(reader)
            .getAsJsonObject()
            .getAsJsonObject("data");
        JsonObject properties = texture.getAsJsonObject("textures");
        return SkinFetcherResult.of(
            properties.get("value").getAsString(),
            properties.get("signature").getAsString());
    }
}
