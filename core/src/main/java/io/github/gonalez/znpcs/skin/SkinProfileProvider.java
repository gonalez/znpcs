package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public abstract class SkinProfileProvider {
  private static final Duration SKIN_REQUEST_DEFAULT_TIMEOUT = Duration.ofSeconds(5);

  public abstract String getTargetUrl(String skin);

  protected abstract GameProfile readProfile(String skin, JsonElement value);

  HttpRequest.Builder prepareRequest(URI uri, String skin) {
    return HttpRequest.newBuilder()
        .uri(uri)
        .timeout(SKIN_REQUEST_DEFAULT_TIMEOUT);
  }
}
