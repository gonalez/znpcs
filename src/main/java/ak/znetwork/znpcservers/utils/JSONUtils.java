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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class JSONUtils {

    public static String[] getFromUrl(String url) {
        final String[] properties = new String[2];

        try {
            String getJSON = readUrl(url);
            JSONObject jsonObject = (JSONObject) JSONValue.parse(getJSON);

            final JSONArray jsonArray = (JSONArray) jsonObject.get("properties");

            JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);


            properties[0] = jsonObject1.get("value").toString();
            properties[1] = jsonObject1.get("signature").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
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

    public static UUID fetchUUID(String name) {
        try {
            String getJSON = readUrl("https://api.mojang.com/users/profiles/minecraft/" + name);
            JSONObject jsonObject = (JSONObject) JSONValue.parse(getJSON);

            final String uuid = jsonObject.get("id").toString();

            String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);

            return UUID.fromString(realUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
