package ak.znetwork.znpcservers.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ak.znetwork.znpcservers.skin.SkinFetcherBuilder.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class SkinFetcher {
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
    private static final ExecutorService SKIN_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /**
     * Creates a new parser.
     */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * The skin builder.
     */
    private final SkinFetcherBuilder builder;

    /**
     * The skin api type.
     */
    private final SkinAPI skinAPI;

    /**
     * Creates a new skin fetcher.
     * With all the builder provided types.
     *
     * @param builder The skin builder.
     */
    public SkinFetcher(SkinFetcherBuilder builder) {
        this.builder = builder;
        this.skinAPI = builder.getApiUrl();
    }

    /**
     * Returns the api server response.
     *
     * @return The http response.
     */
    private CompletableFuture<JsonObject> getResponse() {
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        SKIN_EXECUTOR_SERVICE.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(builder.getApiUrl().getApiURL() + getData()).openConnection();
                connection.setRequestMethod(builder.getApiUrl().getMethod());
                connection.setDoInput(true);
                if (builder.getApiUrl() == SkinAPI.GENERATE_API) {
                    connection.setDoOutput(true);
                    // Send skin data
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        outputStream.writeBytes("url=" + URLEncoder.encode(builder.getData(), DEFAULT_CHARSET));
                    }
                }
                try (Reader reader = new InputStreamReader(connection.getInputStream(), Charset.forName(DEFAULT_CHARSET))) {
                    // Read url result
                    completableFuture.complete(JSON_PARSER.parse(reader).getAsJsonObject());
                } finally {
                    connection.disconnect();
                }
            } catch (Throwable throwable) {
                completableFuture.completeExceptionally(throwable);
            }
        });
        return completableFuture;
    }

    /**
     * Fetches the skin profile values.
     */
    public void fetchProfile(
            SkinFetcherResult skinResultCallback
    ) {
        getResponse().thenAcceptAsync(jsonObject -> {
            jsonObject = jsonObject.getAsJsonObject(skinAPI == SkinAPI.GENERATE_API ? "data" : "textures");
            JsonObject properties = (skinAPI == SkinAPI.GENERATE_API ?
                    jsonObject.getAsJsonObject("texture") :
                    jsonObject.getAsJsonObject("raw"));
            skinResultCallback.onDone(new String[]{ properties.get("value").getAsString(), properties.get("signature").getAsString() });
        });
    }

    /**
     * Returns the real data for skin api.
     *
     * @return The data for skin api.
     */
    private String getData() {
        return skinAPI != SkinAPI.GENERATE_API ? "/" + builder.getData() : EMPTY_STRING;
    }
}
