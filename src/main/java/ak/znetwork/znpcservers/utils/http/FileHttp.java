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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class FileHttp {

	public String value;

	public FileHttp(final String url, final String skin) throws IOException {
		final URL url1 = new URL(url);

		final HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
		httpURLConnection.setDoOutput(true);

		httpURLConnection.setRequestMethod("POST");

		try (final DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream())) {
			outputStream.writeBytes("url=" + URLEncoder.encode(skin, "UTF-8"));
		}

		if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			final StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
			}
			value = stringBuilder.toString();
		}
	}

	public String getValue() {
		return value;
	}
}