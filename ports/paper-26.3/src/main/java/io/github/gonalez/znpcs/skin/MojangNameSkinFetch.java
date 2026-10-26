/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.skin;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/** Fetches the current signed texture from Mojang, without a third-party skin API. */
public class MojangNameSkinFetch extends SkinFetcherServer {
  private final URI names;
  private final URI profiles;

  public MojangNameSkinFetch() {
    this(
        URI.create("https://api.mojang.com/users/profiles/minecraft/"),
        URI.create("https://sessionserver.mojang.com/session/minecraft/profile/"));
  }

  public MojangNameSkinFetch(URI names, URI profiles) {
    this.names = names;
    this.profiles = profiles;
  }

  @Override
  public String getName() {
    return "Mojang";
  }

  @Override
  protected HttpURLConnection createConnection(String name) throws IOException {
    String id = name.replace("-", "");
    if (!id.matches("[a-fA-F0-9]{32}")) {
      if (!name.matches("[A-Za-z0-9_]{1,16}"))
        throw new IOException("Invalid Minecraft name or UUID");
      HttpURLConnection lookup =
          SkinFetchHttp.openConnection(
              SkinFetchHttp.RequestMethod.GET, names.resolve(name).toString());
      try {
        id = readHttpResponse(lookup).toJson().getAsJsonObject().get("id").getAsString();
      } catch (RuntimeException e) {
        throw new IOException("Mojang profile response is missing a valid UUID", e);
      } finally {
        lookup.disconnect();
      }
    }
    if (!id.matches("[a-fA-F0-9]{32}")) throw new IOException("Mojang returned an invalid UUID");
    return SkinFetchHttp.openConnection(
        SkinFetchHttp.RequestMethod.GET, profiles.resolve(id + "?unsigned=false").toString());
  }

  @Override
  protected SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response)
      throws IOException {
    try {
      for (var item : response.toJson().getAsJsonObject().getAsJsonArray("properties")) {
        JsonObject property = item.getAsJsonObject();
        if ("textures".equals(property.get("name").getAsString()))
          return new SkinProperties(
              property.get("value").getAsString(), property.get("signature").getAsString());
      }
    } catch (RuntimeException e) {
      throw new IOException("Mojang returned invalid or unsigned texture data", e);
    }
    throw new IOException("Mojang returned no texture property");
  }
}
