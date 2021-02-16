/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.utils;

import ak.znetwork.znpcservers.utils.http.FileHttp;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkinFetcher {

    /**
     * Default skin
     * this will be used when a skin is not found
     */
    private static final String[] DEFAULT_SKIN = new String[]{"ewogICJ0aW1lc3RhbXAiIDogMTU5NTI2MjY4ODg0OCwKICAicHJvZmlsZUlkIiA6ICJiYmJmODFlMGEyY2E0OTExOGQwMWYwZjMwNzZkNDg4MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSb2Jsb3htYW5QbGF5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5Njk1YTc2ZWMyOGNmNWU3M2RiMGNhMzEwMDEwZjM4MjFkYzAxY2Q5M2FhZmZlZTYxNjE5ZmJkZTEwNTkxZWIiCiAgICB9CiAgfQp9", "YV6cKWe1JW2O5viDSX9KESnWupdG8UnCWznU9Wl1Gh3Ntw09uoZEHCKcAZgQw0QoS2FQHR+k/Sl5erDdruFxXPT/CXaj+dZ/Pwrc+8CGASzsrZWxX9C6XEWqer+RgVsRktob7NQD4zaag27VKXWoATCmY/CH4UTV4wo8hfKWELBVzl61YqLwZlwGUNYK8mdp612sGEgOSUsbVn7kS4VVvRi2OOx4ZSFkc0tlYfvUzj6s+jICKomD3dhmEc3y7jOOHeNI8Y03TvvwbQtymZEB0boSnF0t2TAmBY1D/2QgIVeKBCAyLIz1HSuPMzIq6QxTvew64Amql2w6FFKtwb7vPMSp6UDe7xAaIV8vxpnBhUDfWCKgWxjMxfBwsX9Ox+NA4bc1zOAZM9BYEYEQc+t/yK7W/zUpQOgrbWsN9p+UFxS4Kt2wuysFxmPa1irdsTp2OsyEoLeRfuaHBMfMRisdEQc1sOZJRcBWW4MiyQglGG8ZwvYXIHLQQEpGVHqkPi//IvGxSqU3LEIAAvlMx7frgrZ4QtwY5S4G+GglgzaAUIOIPzcJD7D9r40x+mkEvP4SSoyj+PDYIbNs0AKQBP5Jx+03Pdzho1sLAzc+oq1QBUuA9dQRudR4GWAB20hVEp8E+bo3tLDUAESMK1yB7WtBy4ZsL8va7kdsHN0sVtASpw0="};

    private static final String UUID_SKIN_API = "https://api.minetools.eu/uuid/";
    private static final String PROFILE_SKIN_API = "https://api.minetools.eu/profile/";

    private static final ExecutorService skinExecutorService;
    private static final JsonParser jsonParser;

    static {
        skinExecutorService = Executors.newCachedThreadPool();

        jsonParser = new JsonParser();
    }

    public static String readUrl(String urlString) {
        try {
            return Resources.toString(new URL(urlString), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read url", e);
        }
    }

    public static CompletableFuture<UUID> fetchUUID(String name) {
        CompletableFuture<UUID> completableFuture = new CompletableFuture<>();

        skinExecutorService.execute(() -> {
            JsonObject uuidURL = jsonParser.parse(readUrl(UUID_SKIN_API + name)).getAsJsonObject();

            UUID value;
            if (uuidURL == null)
                value = UUID.randomUUID();
            else {
                String uuid = uuidURL.get("id").getAsString();
                uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);

                value = UUID.fromString(uuid);
            }
            completableFuture.complete(value);
        });
        return completableFuture;
    }

    public static CompletableFuture<String[]> fetchSkin(UUID uuid) {
        CompletableFuture<String[]> completableFuture = new CompletableFuture<>();

        skinExecutorService.execute(() -> {
            JsonObject skinURL = jsonParser.parse(readUrl(PROFILE_SKIN_API + uuid.toString().replace("-", ""))).getAsJsonObject();

            String[] value = DEFAULT_SKIN;
            if (skinURL != null) {
                JsonObject skinValues = skinURL.get("raw").getAsJsonObject().getAsJsonArray("properties").get(0).getAsJsonObject();
                if (skinValues != null) {
                    value = new String[]{ skinValues.get("value").getAsString(), skinValues.get("signature").getAsString() };
                }
            }
            completableFuture.complete(value);
        });
        return completableFuture;
    }

    public static CompletableFuture<SkinFetch> getByURL(String url) {
        CompletableFuture<SkinFetch> completableFuture = new CompletableFuture<>();

        skinExecutorService.execute(() -> {
            SkinFetch value;
            try {
                FileHttp httpRequest = new FileHttp("https://api.mineskin.org/generate/url", url);
                JsonObject skinURL = jsonParser.parse(httpRequest.getResult()).getAsJsonObject();

                if (skinURL == null )
                    value = new SkinFetch(UUID.randomUUID(), DEFAULT_SKIN[0], DEFAULT_SKIN[1]);
                else {
                    JsonObject data = skinURL.get("data").getAsJsonObject();
                    JsonObject texture = data.get("texture").getAsJsonObject();

                    value = new SkinFetch(UUID.randomUUID(), texture.get("value").getAsString(), texture.get("signature").getAsString());
                }
            } catch (Exception e) {
                value = new SkinFetch(UUID.randomUUID(), DEFAULT_SKIN[0], DEFAULT_SKIN[1]);
            }
            completableFuture.complete(value);
        });
        return completableFuture;
    }

    public static SkinFetch getDefaultSkin(String name) throws Exception {
        UUID uuid = fetchUUID(name).get();
        String[] skin = fetchSkin(uuid).get();

        return new SkinFetch(uuid, skin[0], skin[1]);
    }
}

