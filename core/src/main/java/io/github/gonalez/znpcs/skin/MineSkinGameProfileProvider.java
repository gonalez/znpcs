package io.github.gonalez.znpcs.skin;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.ryanharter.auto.value.gson.GenerateTypeAdapter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.util.UUID;

public class MineSkinGameProfileProvider extends HttpGameProfileProvider {
  private static final Gson GSON = new GsonBuilder()
    .registerTypeAdapterFactory(GenerateTypeAdapter.FACTORY).create();

  public MineSkinGameProfileProvider(HttpClient httpClient) {
    super(httpClient);
  }

  @Override
  Builder prepareRequest(URI uri, String skin) {
    return super.prepareRequest(uri, skin)
      .header(CONTENT_TYPE, "application/json")
      .POST(
        BodyPublishers.ofString(
          GSON.toJson(
            MineSkinGenerateRequest.newBuilder()
              .setUrl(skin)
              .build())));
  }

  @Override
  public String getTargetUrl(String skinName) {
    return "https://api.mineskin.org/v2/generate";
  }

  @Override
  protected GameProfile provideGameProfile(String skin, JsonElement value) {
    JsonObject skinObject = value.getAsJsonObject()
      .getAsJsonObject("skin");

    JsonObject textureData = skinObject
      .getAsJsonObject("texture")
      .getAsJsonObject("data");

    return GameProfiles.newGameProfile(
      UUID.randomUUID(),
      skin,
      textureData.get("value").getAsString(),
      textureData.get("signature").getAsString());
  }
}
