package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.http.HttpClient;
import java.util.UUID;

public class AshconGameProfileProvider extends HttpGameProfileProvider {

  public AshconGameProfileProvider(HttpClient httpClient) {
    super(httpClient);
  }

  @Override
  public String getTargetUrl(String skin) {
    return "https://api.ashcon.app/mojang/v2/user/%s";
  }

  @Override
  protected GameProfile provideGameProfile(String name, JsonElement value) {
    JsonObject jsonObject = value.getAsJsonObject();

    UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
    String username = jsonObject.get("username").getAsString();

    JsonObject textures = jsonObject.get("textures").getAsJsonObject();
    JsonObject raw = textures.get("raw").getAsJsonObject();

    return GameProfiles.newGameProfile(
      uuid,
      username,
      raw.get("value").getAsString(),
      raw.get("signature").getAsString());
  }
}
