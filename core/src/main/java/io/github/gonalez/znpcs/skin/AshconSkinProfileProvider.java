package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class AshconSkinProfileProvider extends SkinProfileProvider {

  @Override
  public String getTargetUrl(String skin) {
    return String.format("https://api.ashcon.app/mojang/v2/user/%s", skin);
  }

  @Override
  protected GameProfile readProfile(String skin, JsonElement value) {
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
