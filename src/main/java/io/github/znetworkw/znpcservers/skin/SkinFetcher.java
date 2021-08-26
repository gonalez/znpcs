package io.github.znetworkw.znpcservers.skin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Retrieves the skin textures for a {@link SkinFetcherBuilder}.
 * @see SkinFetcherBuilder
 */
public class SkinFetcher {
    /** A empty string. */
    private static final String EMPTY_STRING = "";

    /** The charset that will be used when making the skin request. */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /** A executor service to delegate the work. */
    private static final ExecutorService SKIN_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    /** Creates a new parser. */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /** The skin builder. */
    private final SkinFetcherBuilder builder;

    /**
     * Creates a new {@link SkinFetcher} for the given builder.
     *
     * @param builder The builder.
     */
    public SkinFetcher(SkinFetcherBuilder builder) {
        this.builder = builder;
    }

    /**
     * Fetches the json object of the skin from the specified
     * builder {@link SkinFetcherBuilder#getAPIServer()}.
     */
    protected CompletableFuture<JsonObject> doReadSkin() {
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();
        SKIN_EXECUTOR_SERVICE.submit(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(builder.getAPIServer().getURL() + getData()).openConnection();
                connection.setRequestMethod(builder.getAPIServer().getMethod());
                connection.setDoInput(true);
                if (builder.isUrlType()) {
                    connection.setDoOutput(true);
                    // send skin data
                    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                        outputStream.writeBytes("url=" + URLEncoder.encode(builder.getData(), DEFAULT_CHARSET));
                    }
                }
                try (Reader reader = new InputStreamReader(connection.getInputStream(), Charset.forName(DEFAULT_CHARSET))) {
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
     * Gets the fetched skin values.
     *
     * @param skinResultCallback The callback to run.
     */
    public void fetchProfile(SkinFetcherResult skinResultCallback) {
        doReadSkin().thenAcceptAsync(jsonObject -> {
            jsonObject = jsonObject.getAsJsonObject(builder.isUrlType() ? "data" : "textures");
            JsonObject properties = (builder.isUrlType() ?
                jsonObject.getAsJsonObject("texture") :
                jsonObject.getAsJsonObject("raw"));
            skinResultCallback.onDone(new String[]{ properties.get("value").getAsString(), properties.get("signature").getAsString() });
        });
    }

    /**
     * Returns the url data for the builder api server.
     */
    private String getData() {
        return builder.isProfileType() ? "/" + builder.getData() : EMPTY_STRING;
    }
}
