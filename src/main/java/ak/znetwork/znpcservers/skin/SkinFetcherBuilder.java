package ak.znetwork.znpcservers.skin;
/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class SkinFetcherBuilder {
    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * The Rest API server to use.
     */
    private final SkinAPI apiUrl;

    /**
     * The data to send.
     */
    private final String data;

    /**
     * Creates a builder.
     *
     * @param apiUrl     The skin url type.
     * @param data       The data to send.
     */
    private SkinFetcherBuilder(SkinAPI apiUrl,
                               String data) {
        this.apiUrl = apiUrl;
        this.data = data;
    }

    /**
     * Returns the skin url type.
     *
     * @return The skin url type.
     */
    public SkinAPI getApiUrl() {
        return apiUrl;
    }

    /**
     * Returns the skin data.
     *
     * @return The skin data.
     */
    public String getData() {
        return data;
    }

    /**
     * Creates a new builder for an API Server.
     *
     * @param skinAPIURL The API url.
     * @return           A builder for an API Server.
     */
    public static SkinFetcherBuilder ofType(SkinAPI skinAPIURL) {
        return new SkinFetcherBuilder(skinAPIURL, EMPTY_STRING);
    }

    /**
     * Creates a new builder with the additional data.
     *
     * @param skinAPIURL The API url.
     * @param data       The data.
     * @return           A builder with the additional data.
     */
    public static SkinFetcherBuilder withData(SkinAPI skinAPIURL, String data) {
        return new SkinFetcherBuilder(skinAPIURL, data);
    }

    /**
     * Creates a new skin fetcher for the given builder.
     *
     * @return A skin fetcher definition for the given builder.
     */
    public SkinFetcher toSkinFetcher() {
        return new SkinFetcher(this);
    }

    /**
     * This enum represents the possible API servers to use,
     * for fetching a skin.
     */
    public enum SkinAPI {

        /**
         * Used for retrieving a profile linked to a name.
         */
        PROFILE_API("GET", "https://api.ashcon.app/mojang/v2/user"),

        /**
         * Used to generate a profile from an image URL.
         */
        GENERATE_API("POST", "https://api.mineskin.org/generate/url");

        /**
         * The HTTP request method.
         */
        private final String method;

        /**
         * The Rest API URL.
         */
        private final String apiURL;

        /**
         * Creates a new api url.
         *
         * @param method The HTTP request method.
         * @param apiURL The url api server.
         */
        SkinAPI(String method,
                String apiURL) {
            this.apiURL = apiURL;
            this.method = method;
        }

        /**
         * Returns the api url.
         *
         * @return The api url.
         */
        public String getApiURL() {
            return apiURL;
        }

        /**
         * Returns the request method (GET,POST).
         *
         * @return The request method
         */
        public String getMethod() {
            return method;
        }
    }
}