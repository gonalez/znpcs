package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.util.UUID;

public class AshconSkinFetcherServer extends AbstractSkinFetcherServer {

  @Override
  protected URI getUriForRequest(String skinName) {
    return URI.create(String.format("https://api.ashcon.app/mojang/v2/user/%s", skinName));
  }

  @Override
  protected GameProfile readProfile(String skinName, JsonElement jsonElement) {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
    String username = jsonObject.get("username").getAsString();

    JsonObject textures = jsonObject.get("textures").getAsJsonObject();
    JsonObject skin = textures.get("raw").getAsJsonObject();

    return GameProfiles.newGameProfile(uuid, username,
        skin.get("value").getAsString(), skin.get("signature").getAsString());
  }
}
