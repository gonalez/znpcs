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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class JSONUtils {

    private static final String[] DEFAULT_SKIN = new String[]{"ewogICJ0aW1lc3RhbXAiIDogMTU5NTI2MjY4ODg0OCwKICAicHJvZmlsZUlkIiA6ICJiYmJmODFlMGEyY2E0OTExOGQwMWYwZjMwNzZkNDg4MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSb2Jsb3htYW5QbGF5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5Njk1YTc2ZWMyOGNmNWU3M2RiMGNhMzEwMDEwZjM4MjFkYzAxY2Q5M2FhZmZlZTYxNjE5ZmJkZTEwNTkxZWIiCiAgICB9CiAgfQp9", "YV6cKWe1JW2O5viDSX9KESnWupdG8UnCWznU9Wl1Gh3Ntw09uoZEHCKcAZgQw0QoS2FQHR+k/Sl5erDdruFxXPT/CXaj+dZ/Pwrc+8CGASzsrZWxX9C6XEWqer+RgVsRktob7NQD4zaag27VKXWoATCmY/CH4UTV4wo8hfKWELBVzl61YqLwZlwGUNYK8mdp612sGEgOSUsbVn7kS4VVvRi2OOx4ZSFkc0tlYfvUzj6s+jICKomD3dhmEc3y7jOOHeNI8Y03TvvwbQtymZEB0boSnF0t2TAmBY1D/2QgIVeKBCAyLIz1HSuPMzIq6QxTvew64Amql2w6FFKtwb7vPMSp6UDe7xAaIV8vxpnBhUDfWCKgWxjMxfBwsX9Ox+NA4bc1zOAZM9BYEYEQc+t/yK7W/zUpQOgrbWsN9p+UFxS4Kt2wuysFxmPa1irdsTp2OsyEoLeRfuaHBMfMRisdEQc1sOZJRcBWW4MiyQglGG8ZwvYXIHLQQEpGVHqkPi//IvGxSqU3LEIAAvlMx7frgrZ4QtwY5S4G+GglgzaAUIOIPzcJD7D9r40x+mkEvP4SSoyj+PDYIbNs0AKQBP5Jx+03Pdzho1sLAzc+oq1QBUuA9dQRudR4GWAB20hVEp8E+bo3tLDUAESMK1yB7WtBy4ZsL8va7kdsHN0sVtASpw0="};

    //
    public static final String UUID_SKIN_API = "https://api.minetools.eu/uuid/";
    public static final String PROFILE_SKIN_API = "https://api.minetools.eu/profile/";

    public static String readUrl(String urlString) {
        try {
            return Resources.toString(new URL(urlString), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read url", e);
        }
    }

    public static CompletableFuture<UUID> fetchUUID(final String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final JSONObject jsonObject = (JSONObject) new JSONParser().parse(readUrl(UUID_SKIN_API + name));
                if (jsonObject == null) return UUID.randomUUID();

                String uuid = jsonObject.get("id").toString();
                uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);

                return UUID.fromString(uuid);
            } catch (Exception e) {
                return UUID.randomUUID();
            }
        });
    }

    public static CompletableFuture<String[]> fetchSkin(final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(readUrl(PROFILE_SKIN_API + uuid.toString().replace("-", "")));

                final JSONObject jsonObject1 = (JSONObject) jsonObject.get("raw");
                final JSONArray jsonArray = (JSONArray) jsonObject1.get("properties");

                return new String[]{((JSONObject) jsonArray.get(0)).get("value").toString(), ((JSONObject) jsonArray.get(0)).get("signature").toString()};
            } catch (ParseException exception) {
                return DEFAULT_SKIN;
            }
        });
    }

    public static SkinFetch getDefaultSkin(String name) throws Exception {
        final UUID uuid = fetchUUID(name).get();
        final String[] skin = fetchSkin(uuid).get();

        return new SkinFetch(uuid, skin[0], skin[1]);
    }

    public static CompletableFuture<SkinFetch> getByURL(final String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileHttp httpRequest = new FileHttp("https://api.mineskin.org/generate/url", url);

                final String string = httpRequest.getValue();

                final JSONObject jsonObject = (JSONObject) new JSONParser().parse(string);

                final JSONObject data = (JSONObject) jsonObject.get("data");
                final JSONObject texture = (JSONObject) data.get("texture");

                return new SkinFetch(UUID.randomUUID(), texture.get("value").toString(), texture.get("signature").toString());
            } catch (Exception e) {
                return new SkinFetch(UUID.randomUUID(), DEFAULT_SKIN[0], DEFAULT_SKIN[1]);
            }
        });
    }
}

