package ak.znetwork.znpcservers.npc.skin;

import java.net.URL;

import ak.znetwork.znpcservers.skin.callback.SkinResultCallback;
import ak.znetwork.znpcservers.utility.Utils;
import lombok.Getter;

import static ak.znetwork.znpcservers.skin.impl.SkinFetcherImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
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
     * @return A skin class with the fetched values.
     */
    public static void forName(String skin,
                                   SkinResultCallback skinResultCallback) {
        try {
            // Check if skin value is a url
            new URL(skin).toURI();

            SkinBuilder.withData(SkinAPI.GENERATE_API, skin).toSkinFetcher().fetchProfile(skinResultCallback);
        } catch (Exception e) {
            SkinBuilder.withData(SkinAPI.PROFILE_API, skin).toSkinFetcher().fetchProfile(skinResultCallback);
        }
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
            int value = V9.layerValue;
            for (SkinLayerValues skinLayerValue : values()) {
                if (Utils.BUKKIT_VERSION >= skinLayerValue.minVersion) {
                    value = skinLayerValue.layerValue;
                }
            }
            return value;
        }
    }
}
