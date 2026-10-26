/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** MineSkin V2 queue client for image URLs, with optional API-key configuration. */
public class MineSkinFetch extends SkinFetcherServer {
  private final URI api;
  private final String key;
  private final long maxWaitMillis;

  public MineSkinFetch() {
    this(
        URI.create("https://api.mineskin.org/v2/"), System.getenv("ZNPCS_MINESKIN_API_KEY"), 60000);
  }

  public MineSkinFetch(URI api, String key, long maxWaitMillis) {
    this.api = api;
    this.key = key == null ? "" : key.trim();
    this.maxWaitMillis = maxWaitMillis;
  }

  public static MineSkinFetch fromConfig(Path folder) throws IOException {
    Path file = folder.resolve("skins.properties");
    if (!Files.exists(file))
      Files.writeString(
          file,
          "# Optional MineSkin API key for PNG URL skins. Player names use Mojang and need no"
              + " key.\n"
              + "mineskin-api-key=\n",
          StandardCharsets.UTF_8);
    Properties properties = new Properties();
    try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
      properties.load(reader);
    }
    String environmentKey = System.getenv("ZNPCS_MINESKIN_API_KEY");
    String key =
        environmentKey == null || environmentKey.isBlank()
            ? properties.getProperty("mineskin-api-key", "")
            : environmentKey;
    return new MineSkinFetch(URI.create("https://api.mineskin.org/v2/"), key, 60000);
  }

  @Override
  public String getName() {
    return "MineSkin";
  }

  private HttpURLConnection connection(SkinFetchHttp.RequestMethod method, String path)
      throws IOException {
    var c = SkinFetchHttp.openConnection(method, api.resolve(path).toString());
    if (!key.isEmpty()) c.setRequestProperty("Authorization", "Bearer " + key);
    return c;
  }

  @Override
  protected HttpURLConnection createConnection(String url) throws IOException {
    if (!SkinFetcher.isUrl(url) || URI.create(url).getHost() == null)
      throw new IOException("A PNG image URL is required");
    var c = connection(SkinFetchHttp.RequestMethod.POST, "queue");
    c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
    c.setDoOutput(true);
    JsonObject body = new JsonObject();
    body.addProperty("url", url);
    body.addProperty("visibility", "unlisted");
    try (var output = c.getOutputStream()) {
      output.write(body.toString().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      c.disconnect();
      throw e;
    }
    return c;
  }

  private JsonObject get(String path) throws IOException {
    var c = connection(SkinFetchHttp.RequestMethod.GET, path);
    try {
      return readHttpResponse(c).toJson().getAsJsonObject();
    } catch (RuntimeException e) {
      throw new IOException("MineSkin returned invalid JSON", e);
    } finally {
      c.disconnect();
    }
  }

  @Override
  protected SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response)
      throws IOException {
    long deadline =
        System.nanoTime() + java.util.concurrent.TimeUnit.MILLISECONDS.toNanos(maxWaitMillis);
    try {
      JsonObject object = response.toJson().getAsJsonObject();
      while (true) {
        if (object.has("skin") && object.get("skin").isJsonObject()) {
          JsonObject data =
              object.getAsJsonObject("skin").getAsJsonObject("texture").getAsJsonObject("data");
          return new SkinProperties(
              data.get("value").getAsString(), data.get("signature").getAsString());
        }
        if (System.nanoTime() >= deadline)
          throw new IOException("MineSkin generation timed out; try again later");
        JsonObject job = object.getAsJsonObject("job");
        if (job == null)
          throw new IOException("MineSkin returned neither signed skin data nor a job");
        String status = job.get("status").getAsString();
        if (status.equals("failed"))
          throw new IOException(
              "MineSkin could not generate this image; check its URL and API-key permissions");
        if (status.equals("completed") && job.has("result")) {
          object =
              get(
                  "skins/"
                      + URLEncoder.encode(job.get("result").getAsString(), StandardCharsets.UTF_8));
          if (!object.has("skin") && object.has("texture")) {
            JsonObject wrapper = new JsonObject();
            wrapper.add("skin", object);
            object = wrapper;
          }
          continue;
        }
        if (System.nanoTime() >= deadline)
          throw new IOException("MineSkin generation timed out; try again later");
        try {
          Thread.sleep(Math.min(1000, Math.max(1, maxWaitMillis)));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new IOException("Skin generation interrupted", e);
        }
        object =
            get("queue/" + URLEncoder.encode(job.get("id").getAsString(), StandardCharsets.UTF_8));
      }
    } catch (RuntimeException e) {
      throw new IOException("MineSkin returned invalid or unsigned texture data", e);
    }
  }
}
