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
package ak.znetwork.znpcservers.utils.http;

import com.google.common.io.CharStreams;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FileHttp {

    @Getter private String result;

    public FileHttp(String url, String skin) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();

        // Use post method
        httpURLConnection.setRequestMethod("POST");

        // Allow output && input
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        // Send data
        try (DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
            outputStream.writeBytes("url=" + URLEncoder.encode(skin, "UTF-8"));
        }

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                // Read
                this.result = CharStreams.toString(bufferedReader);
            }
        }
    }
}