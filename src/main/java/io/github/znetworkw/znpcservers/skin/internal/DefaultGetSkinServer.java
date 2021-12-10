package io.github.znetworkw.znpcservers.skin.internal;

import com.google.gson.JsonObject;
import io.github.znetworkw.znpcservers.http.HttpMethod;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.skin.SkinFetcherService;

import java.io.Reader;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultGetSkinServer implements SkinFetcherService {
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
    public SkinFetcherResult read(Reader reader) {
        JsonObject texture = JSON_PARSER.parse(reader)
            .getAsJsonObject()
            .getAsJsonObject("textures");
        JsonObject properties = texture.getAsJsonObject("raw");
        return SkinFetcherResult.of(
            properties.get("value").getAsString(),
            properties.get("signature").getAsString());
    }
}
