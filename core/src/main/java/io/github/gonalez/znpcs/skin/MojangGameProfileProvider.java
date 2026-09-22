package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.http.HttpClient;
import java.util.UUID;

public final class MojangGameProfileProvider extends HttpGameProfileProvider {
  private final ByUuid byUuid;

  public MojangGameProfileProvider(HttpClient httpClient) {
    super(httpClient);
    this.byUuid = new ByUuid(httpClient);
  }

  @Override
  protected String getTargetUrl(String skin) {
    return "https://api.mojang.com/users/profiles/minecraft/%s";
  }

  @Override
  protected GameProfile provideGameProfile(String name, JsonElement value) {
    String uuid = value.getAsJsonObject()
      .get("id")
      .getAsString();
    return byUuid.provideGameProfile(uuid);
  }

  private static final class ByUuid extends HttpGameProfileProvider {

    private ByUuid(HttpClient httpClient) {
      super(httpClient);
    }

    @Override
    protected String getTargetUrl(String uuid) {
      return "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    }

    private static String formatUuid(String uuid) {
      return uuid.replaceFirst(
        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
        "$1-$2-$3-$4-$5"
      );
    }

    @Override
    protected GameProfile provideGameProfile(String name, JsonElement value) {
      JsonArray properties = value.getAsJsonObject().getAsJsonArray("properties");
      JsonObject textures = properties.get(0).getAsJsonObject();

      return GameProfiles.newGameProfile(
        UUID.fromString(formatUuid(value.getAsJsonObject().get("id").getAsString())),
        value.getAsJsonObject().get("name").getAsString(),
        textures.get("value").getAsString(),
        textures.get("signature").getAsString());
    }
  }
}
