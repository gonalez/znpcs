package io.github.gonalez.znpcs.skin;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.util.UUID;

public class MineSkinFetcher extends AbstractSkinFetcherServer {
  private static final String FORM_UTF_MIME = "application/x-www-form-urlencoded; charset=utf-8";

  @Override
  protected URI getUriForRequest(String skinName) {
    return URI.create("https://api.mineskin.org/generate/url");
  }

  @Override
  public Builder prepareRequest(String skinName) {
    return super.prepareRequest(skinName)
        .header(CONTENT_TYPE, FORM_UTF_MIME)
        .POST(HttpRequest.BodyPublishers.ofString("url=" + URLEncoder.encode(skinName, UTF_8)));
  }

  @Override
  protected GameProfile readProfile(String skinName, JsonElement jsonElement) {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    JsonObject data = jsonObject.get("data").getAsJsonObject();

    UUID uuid = UUID.fromString(data.get("uuid").getAsString());
    JsonObject skin = data.get("texture").getAsJsonObject();

    return GameProfiles.newGameProfile(uuid, skinName,
        skin.get("value").getAsString(), skin.get("signature").getAsString());
  }
}
