package ak.znetwork.znpcservers.skin;

import ak.znetwork.znpcservers.skin.impl.SkinFetcherImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter @Setter
public final class SkinFetcher implements SkinFetcherImpl {

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * The default charset name.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * The executor service to delegate work.
     */
    private static final ExecutorService skinExecutorService;

    /**
     * Creates a new parser.
     */
    private static final JsonParser jsonParser;

    static {
        skinExecutorService = Executors.newCachedThreadPool();

        jsonParser = new JsonParser();
    }

    /**
     * The skin builder.
     */
    private final SkinBuilder builder;

    /**
     * The skin api type.
     */
    private final SkinAPI skinAPI;

    /**
     * The json url response.
     */
    private JsonObject response;

    /**
     * Creates a new skin fetcher.
     * With all the builder provided types.
     *
     * @param builder The skin builder.
     */
    public SkinFetcher(SkinBuilder builder) {
        this.builder = builder;
        this.skinAPI = builder.getApiUrl();
    }

    /**
     * Gets the api server response.
     *
     * @return The http response.
     */
    public CompletableFuture<JsonObject> getResponse() {
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        skinExecutorService.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(builder.getApiUrl().getApiURL() + getData()).openConnection();
                connection.setRequestMethod(getBuilder().getApiUrl().getMethod());

                connection.setDoInput(true);

                if (getBuilder().getApiUrl() == SkinAPI.GENERATE_API) {
                    // Send data
                    connection.setDoOutput(true);
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        outputStream.writeBytes("url=" + URLEncoder.encode(getBuilder().getData(), DEFAULT_CHARSET));
                    }
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Failed to retrieve response from api server.");
                }

                try (Reader reader = new InputStreamReader(connection.getInputStream(), Charset.forName(DEFAULT_CHARSET))) {
                    // Read json
                    completableFuture.complete(jsonParser.parse(reader).getAsJsonObject());
                } finally {
                    connection.disconnect();
                }
            } catch (Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        return completableFuture;
    }

    @Override
    public String getUUID() {
        try {
            return getResponse().get().get("id").getAsString();
        } catch (InterruptedException | ExecutionException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String[] getProfile() {
        try {
            JsonObject data = getResponse().get().get(getSkinAPI() == SkinAPI.GENERATE_API ? "data" : "raw").getAsJsonObject();
            JsonObject properties = (getSkinAPI() == SkinAPI.GENERATE_API ?
                    data.get("texture").getAsJsonObject() :
                    data.getAsJsonArray("properties").get(0).getAsJsonObject());

            return new String[]{ properties.get("value").getAsString(), properties.get("signature").getAsString() };
        } catch (InterruptedException | ExecutionException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Gets real data for skin api.
     *
     * @return Get data for skin api.
     */
    private String getData() {
        return getSkinAPI() != SkinAPI.GENERATE_API ? "/" + getBuilder().getData() : EMPTY_STRING;
    }
}
