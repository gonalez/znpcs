package io.github.gonalez.znpcs.skin;

import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class SkinFetcherServer {
  private static final Duration SKIN_REQUEST_DEFAULT_TIMEOUT = Duration.ofSeconds(5);

  protected abstract URI getUriForRequest(String skinName);

  public abstract GameProfile readProfile(
      String skinName, HttpResponse<String> httpResponse);

  public HttpRequest.Builder prepareRequest(String skinName) {
    return HttpRequest.newBuilder()
        .uri(getUriForRequest(skinName))
        .timeout(SKIN_REQUEST_DEFAULT_TIMEOUT);
  }
}
