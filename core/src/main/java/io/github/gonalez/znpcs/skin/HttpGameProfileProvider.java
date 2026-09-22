package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public abstract class HttpGameProfileProvider implements GameProfileProvider {
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

  private final HttpClient httpClient;

  public HttpGameProfileProvider(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  protected abstract String getTargetUrl(String skin);

  protected abstract GameProfile provideGameProfile(String name, JsonElement value);

  HttpRequest.Builder prepareRequest(URI uri, String skin) {
    return HttpRequest.newBuilder().uri(uri).timeout(DEFAULT_TIMEOUT);
  }

  @Override
  public GameProfile provideGameProfile(String name) {
    try {
      HttpResponse<String> response =
          httpClient.send(
              prepareRequest(URI.create(String.format(getTargetUrl(name), name)), name).build(),
              BodyHandlers.ofString());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new IOException("Error fetching profile. Code: " + response.statusCode());
      }
      return provideGameProfile(name, JsonParser.parseString(response.body()));
    } catch (Exception e) {
      return null;
    }
  }
}
