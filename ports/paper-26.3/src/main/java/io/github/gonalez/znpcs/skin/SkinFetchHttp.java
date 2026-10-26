/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public final class SkinFetchHttp {
  private SkinFetchHttp() {}

  public static HttpURLConnection openConnection(RequestMethod method, String url)
      throws IOException {
    HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
    connection.setRequestMethod(method.name());
    connection.setInstanceFollowRedirects(false);
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(10000);
    connection.setRequestProperty("Accept", "application/json");
    connection.setRequestProperty(
        "User-Agent", "ZNPCs/5.4-SNAPSHOT (+https://github.com/gonalez/znpcs)");
    return connection;
  }

  public enum RequestMethod {
    GET,
    POST
  }

  public static final class HttpResponse {
    private final int statusCode;
    private final byte[] bodyBytes;

    public HttpResponse(int statusCode, byte[] bodyBytes) {
      this.statusCode = statusCode;
      this.bodyBytes = bodyBytes.clone();
    }

    public int getStatusCode() {
      return statusCode;
    }

    public byte[] getBodyBytes() {
      return bodyBytes.clone();
    }

    public String getBodyAsString() {
      return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    public JsonElement toJson() {
      return JsonParser.parseString(getBodyAsString());
    }
  }
}
