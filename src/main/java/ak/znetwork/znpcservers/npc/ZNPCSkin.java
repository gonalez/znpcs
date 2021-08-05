package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.skin.SkinFetcherBuilder;
import ak.znetwork.znpcservers.skin.SkinFetcherResult;
import ak.znetwork.znpcservers.utility.Utils;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class ZNPCSkin {
    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * A empty array of size 1.
     */
    private static final String[] EMPTY_ARRAY = new String[]{EMPTY_STRING,
            EMPTY_STRING};

    /**
     * Layer index.
     */
    private static final int LAYER_INDEX = SkinLayerValues.findLayerByVersion();

    /**
     * The skin value.
     */
    private final String value;

    /**
     * The skin signature.
     */
    private final String signature;

    /**
     * Creates a new skin cache.
     *
     * @param values The skin values.
     */
    protected ZNPCSkin(String... values) {
        if (values.length < 1) {
            throw new IllegalArgumentException("Length cannot be zero or negative.");
        }
        this.value = values[0];
        this.signature = values[1];
    }

    /**
     * Returns the skin value.
     *
     * @return The skin value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the skin signature.
     *
     * @return The skin signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Skin layer index for current version.
     */
    public int getLayerIndex() {
        return LAYER_INDEX;
    }

    /**
     * Creates a new skin cache.
     *
     * @param values The skin values.
     * @return A skin class with the given values.
     */
    public static ZNPCSkin forValues(String...values) {
        return new ZNPCSkin(values.length > 0 ? values : EMPTY_ARRAY);
    }

    /**
     * Creates a new skin cache.
     *
     * @param skin The skin name or url.
     */
    public static void forName(String skin,
                                   SkinFetcherResult skinResultCallback) {
        SkinFetcherBuilder.withData(skin.toLowerCase().startsWith("http") ?
                SkinFetcherBuilder.SkinAPI.GENERATE_API : SkinFetcherBuilder.SkinAPI.PROFILE_API, skin)
                .toSkinFetcher()
                .fetchProfile(skinResultCallback);
    }

    /**
     * Used to find skin layer index by its version.
     */
    enum SkinLayerValues {
        V8(8, 12),
        V9(10, 13),
        V14(14, 15),
        V16(16, 16),
        V17(17, 17);

        /**
         * The minimum required version.
         */
        final int minVersion;

        /**
         * The layer index.
         */
        final int layerValue;

        /**
         * Creates a new layer identification.
         *
         * @param minVersion The minimum required version.
         * @param layerValue The layer index.
         */
        SkinLayerValues(int minVersion,
                        int layerValue) {
            this.minVersion = minVersion;
            this.layerValue = layerValue;
        }

        /**
         * Find a layer index by its version.
         *
         * @return The layer index.
         */
        static int findLayerByVersion() {
            int value = V8.layerValue;
            for (SkinLayerValues skinLayerValue : values()) {
                if (Utils.BUKKIT_VERSION >= skinLayerValue.minVersion) {
                    value = skinLayerValue.layerValue;
                }
            }
            return value;
        }
    }
}
