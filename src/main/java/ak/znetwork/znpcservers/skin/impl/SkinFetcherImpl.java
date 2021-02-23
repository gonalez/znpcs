package ak.znetwork.znpcservers.skin.impl;

import ak.znetwork.znpcservers.skin.SkinFetcher;
import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface SkinFetcherImpl {

    /**
     * Used to fetch data from the server.
     */
    String GET_METHOD = "GET";

    /**
     * Used when sending data to the server.
     */
    String POST_METHOD = "POST";

    /**
     * Represents the skin uuid.
     *
     * @return The skin uuid.
     */
    String getUUID();

    /**
     * Represents the skin profile values.
     *
     * @return The skin profile with the value & signature.
     */
    String[] getProfile();

    @Getter
    class SkinBuilder {

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
        private SkinBuilder(SkinAPI apiUrl,
                              String data) {
            this.apiUrl = apiUrl;
            this.data = data;
        }

        /**
         * Creates a new builder for an API Server.
         *
         * @param skinAPIURL The API url.
         * @return A builder for an API Server.
         */
        public static SkinBuilder ofType(SkinAPI skinAPIURL) {
            return new SkinBuilder(skinAPIURL, EMPTY_STRING);
        }

        /**
         * Creates a new builder with the additional data.
         *
         * @param skinAPIURL The API url.
         * @param data       The data.
         * @return           A builder with the additional data.
         */
        public static SkinBuilder withData(SkinAPI skinAPIURL, String data) {
            return new SkinBuilder(skinAPIURL, data);
        }

        /**
         * Creates a new skin fetcher for the given builder.
         *
         * @return A skin fetcher definition for the given builder.
         */
        public SkinFetcher toSkinFetcher() {
            return new SkinFetcher(this);
        }
    }

    /**
     * This enum represents the possible API servers to use
     * for fetching a skin.
     */
    @Getter
    enum SkinAPI {

        /**
         * Used for retrieving a UUID linked to a username.
         */
        UUID_API(GET_METHOD, "https://api.minetools.eu/uuid"),

        /**
         * Used for retrieving a Profile linked to a uuid.
         */
        PROFILE_API(GET_METHOD, "https://api.minetools.eu/profile"),

        /**
         * Used to generate a texture from an image URL.
         */
        GENERATE_API(POST_METHOD, "https://api.mineskin.org/generate/url");

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
    }
}
