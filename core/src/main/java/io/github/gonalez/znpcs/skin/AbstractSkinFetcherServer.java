package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.http.HttpResponse;

public abstract class AbstractSkinFetcherServer extends SkinFetcherServer {

  @Override
  public GameProfile readProfile(String skinName, HttpResponse<String> httpResponse) {
    JsonElement json = JsonParser.parseString(httpResponse.body());
    return readProfile(skinName, json);
  }

  protected abstract GameProfile readProfile(String skinName, JsonElement jsonObject);
}
