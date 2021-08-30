package io.github.znetworkw.znpcservers.skin;

/**
 * Builder for a {@link SkinFetcher}.
 */
public class SkinFetcherBuilder {
    /** The rest API server to use. */
    private final SkinServer apiServer;
    /** The skin name. */
    private final String name;

    /**
     * Creates a new {@link SkinFetcherBuilder}.
     *
     * @param apiServer The skin api server.
     * @param name The data to send.
     */
    protected SkinFetcherBuilder(SkinServer apiServer,
                                 String name) {
        this.apiServer = apiServer;
        this.name = name;
    }

    /**
     * Returns the skin api server.
     */
    public SkinServer getAPIServer() {
        return apiServer;
    }

    /**
     * Returns the skin name/url.
     */
    public String getData() {
        return name;
    }

    public boolean isUrlType() {
        return apiServer == SkinServer.GENERATE_API;
    }

    public boolean isProfileType() {
        return apiServer == SkinServer.PROFILE_API;
    }

    /**
     * Creates a {@link SkinFetcherBuilder} with the given data.
     *
     * @param skinAPIURL The API server url.
     * @param name The skin name.
     * @return A builder with the additional name.
     */
    public static SkinFetcherBuilder create(SkinServer skinAPIURL,
                                            String name) {
        return new SkinFetcherBuilder(skinAPIURL, name);
    }

    /**
     * Returns a new {@link SkinFetcherBuilder} with the given skin {@code name}.
     * <p>
     * This method will solve which api server to use when calling {@link SkinFetcher#doReadSkin(SkinFetcherResult)}.
     * If the given {@code name#startsWith("http")} the builder will use {@link SkinServer#GENERATE_API}
     * to generate the skin from the given url image otherwise the method will use
     * {@link SkinServer#PROFILE_API} to fetch the skin from a player name.
     * <p>
     * <b>NOTE:</b> If using a url make sure it is a valid skin image with a size of 64x64.
     * @param name The skin name/url.
     * @see SkinServer
     */
    public static SkinFetcherBuilder withName(String name) {
        return create(name.startsWith("http") ? SkinServer.GENERATE_API : SkinServer.PROFILE_API, name);
    }

    /**
     * Creates a new {@link SkinFetcher} from the current instance.
     */
    public SkinFetcher toSkinFetcher() {
        return new SkinFetcher(this);
    }

    /**
     * The possible API servers to use when  fetching a skin.
     */
    public enum SkinServer {
        /**
         * Used for retrieving a profile linked to a name.
         */
        PROFILE_API(
            "GET",
            "https://api.ashcon.app/mojang/v2/user",
            "textures",
            "raw"
        ),
        /**
         * Used to generate a profile from an image URL.
         */
        GENERATE_API(
            "POST",
            "https://api.mineskin.org/generate/url",
            "data",
            "textures"
        );

        /** The http request method. */
        private final String method;

        /** The rest api server url. */
        private final String url;

        private final String valueKey;
        private final String signatureKey;

        /**
         * Creates a new {@link SkinServer}.
         *
         * @param method The http request method.
         * @param url The rest api url.
         */
        SkinServer(String method,
                   String url,
                   String valueKey,
                   String signatureKey) {
            this.method = method;
            this.url = url;
            this.valueKey = valueKey;
            this.signatureKey = signatureKey;
        }

        /**
         * Returns the http request method.
         */
        public String getMethod() {
            return method;
        }

        /**
         * Returns the rest api server url.
         */
        public String getURL() {
            return url;
        }

        public String getValueKey() {
            return valueKey;
        }

        public String getSignatureKey() {
            return signatureKey;
        }
    }
}