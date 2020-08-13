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

import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JSONUtils {

    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static SkinFetch getSkin(String name) {
        String[] properties = new String[]{"ewogICJ0aW1lc3RhbXAiIDogMTU5NTI2MjY4ODg0OCwKICAicHJvZmlsZUlkIiA6ICJiYmJmODFlMGEyY2E0OTExOGQwMWYwZjMwNzZkNDg4MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJSb2Jsb3htYW5QbGF5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M5Njk1YTc2ZWMyOGNmNWU3M2RiMGNhMzEwMDEwZjM4MjFkYzAxY2Q5M2FhZmZlZTYxNjE5ZmJkZTEwNTkxZWIiCiAgICB9CiAgfQp9" , "YV6cKWe1JW2O5viDSX9KESnWupdG8UnCWznU9Wl1Gh3Ntw09uoZEHCKcAZgQw0QoS2FQHR+k/Sl5erDdruFxXPT/CXaj+dZ/Pwrc+8CGASzsrZWxX9C6XEWqer+RgVsRktob7NQD4zaag27VKXWoATCmY/CH4UTV4wo8hfKWELBVzl61YqLwZlwGUNYK8mdp612sGEgOSUsbVn7kS4VVvRi2OOx4ZSFkc0tlYfvUzj6s+jICKomD3dhmEc3y7jOOHeNI8Y03TvvwbQtymZEB0boSnF0t2TAmBY1D/2QgIVeKBCAyLIz1HSuPMzIq6QxTvew64Amql2w6FFKtwb7vPMSp6UDe7xAaIV8vxpnBhUDfWCKgWxjMxfBwsX9Ox+NA4bc1zOAZM9BYEYEQc+t/yK7W/zUpQOgrbWsN9p+UFxS4Kt2wuysFxmPa1irdsTp2OsyEoLeRfuaHBMfMRisdEQc1sOZJRcBWW4MiyQglGG8ZwvYXIHLQQEpGVHqkPi//IvGxSqU3LEIAAvlMx7frgrZ4QtwY5S4G+GglgzaAUIOIPzcJD7D9r40x+mkEvP4SSoyj+PDYIbNs0AKQBP5Jx+03Pdzho1sLAzc+oq1QBUuA9dQRudR4GWAB20hVEp8E+bo3tLDUAESMK1yB7WtBy4ZsL8va7kdsHN0sVtASpw0="};
        final JSONParser jsonParser = new JSONParser();

        CompletableFuture<SkinFetch> skinFetchCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                final JSONObject jsonObject = (JSONObject) jsonParser.parse(readUrl("https://api.mojang.com/users/profiles/minecraft/" + name));

                if (jsonObject == null) new SkinFetch(UUID.randomUUID());

                final String uuid = jsonObject.get("id").toString();
                String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);

                return new SkinFetch(UUID.fromString(realUUID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new SkinFetch(UUID.randomUUID());
        });

        CompletableFuture<Void> thenAcceptAsync = skinFetchCompletableFuture.thenAcceptAsync(skinFetch -> {
            try {
                final JSONObject jsonObject = (JSONObject) new JSONParser().parse(readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + skinFetch.uuid.toString() + "?unsigned=false"));

                if (jsonObject == null) {
                    skinFetch.value = properties[0];
                    skinFetch.signature = properties[1];
                    return;
                }

                final JSONArray jsonArray = (JSONArray) jsonObject.get("properties");

                JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);

                skinFetch.value = jsonObject1.get("value").toString();
                skinFetch.signature = jsonObject1.get("signature").toString();
            } catch (Exception exception) {
                exception.printStackTrace();
            };
        });

        try {
            return skinFetchCompletableFuture.thenCombine(thenAcceptAsync, (result, fooResult) -> result).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}

