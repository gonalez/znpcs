package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.http.HttpClient;
import java.util.UUID;
import java.util.regex.Pattern;

public final class MojangGameProfileProvider extends HttpGameProfileProvider {
  private final ImplUuid implUuid;

  public MojangGameProfileProvider(HttpClient httpClient) {
    super(httpClient);
    this.implUuid = new ImplUuid(httpClient);
  }

  @Override
  protected String getTargetUrl(String skin) {
    return "https://api.mojang.com/users/profiles/minecraft/%s";
  }

  @Override
  protected GameProfile provideGameProfile(String name, JsonElement value) {
    String uuid = value.getAsJsonObject().get("id").getAsString();
    return implUuid.provideGameProfile(uuid);
  }

  // Implementation when the contained skin in the url is UUID.
  private static final class ImplUuid extends HttpGameProfileProvider {
    private static final Pattern UUID_PATTERN =
        Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private ImplUuid(HttpClient httpClient) {
      super(httpClient);
    }

    @Override
    protected String getTargetUrl(String uuid) {
      return "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    }

    @Override
    protected GameProfile provideGameProfile(String name, JsonElement value) {
      JsonArray properties = value.getAsJsonObject().getAsJsonArray("properties");
      JsonObject textures = properties.get(0).getAsJsonObject();

      UUID uuid =
          UUID.fromString(
              UUID_PATTERN
                  .matcher(value.getAsJsonObject().get("id").getAsString())
                  .replaceFirst("$1-$2-$3-$4-$5"));

      return GameProfiles.newGameProfile(
          uuid,
          value.getAsJsonObject().get("name").getAsString(),
          textures.get("value").getAsString(),
          textures.get("signature").getAsString());
    }
  }
}
