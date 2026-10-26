/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.skin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public abstract class SkinFetcherServer {
  public abstract String getName();

  protected abstract HttpURLConnection createConnection(String name) throws IOException;

  protected abstract SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response)
      throws IOException;

  protected SkinFetchHttp.HttpResponse readHttpResponse(HttpURLConnection connection)
      throws IOException {
    int status = connection.getResponseCode();
    if (status != 200 && status != 202) {
      String message = getName() + " returned HTTP " + status;
      if (status == 404 || status == 204) message += " (Minecraft profile or skin not found)";
      if (status == 429) message += " (rate limited; retry later)";
      if (this instanceof MineSkinFetch && (status == 401 || status == 403))
        message += " (configure mineskin-api-key in plugins/ServersNPC/skins.properties)";
      try (InputStream body = connection.getErrorStream()) {
        if (body != null) body.readNBytes(1024);
      }
      throw new IOException(message);
    }
    try (InputStream body = connection.getInputStream()) {
      byte[] bytes = body.readNBytes(262145);
      if (bytes.length > 262144) throw new IOException("Skin service response exceeds 256 KiB");
      return new SkinFetchHttp.HttpResponse(status, bytes);
    }
  }
}
