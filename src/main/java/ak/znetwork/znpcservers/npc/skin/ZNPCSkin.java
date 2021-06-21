package ak.znetwork.znpcservers.npc.skin;

import java.net.URL;

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
        this.value = values[0];
        this.signature = values[1];
    }

    /**
     * @inheritDoc
     */
    public int getLayerIndex() {
        return SkinLayerValues.findLayerByVersion(Utils.BUKKIT_VERSION);
    }

    /**
     * Creates a new skin cache.
     *
     * @param values The skin values.
     * @return A skin class with the given values.
     */
    public static ZNPCSkin forValues(String...values) {
        return new ZNPCSkin(values);
    }

    /**
     * Creates a new skin cache.
     *
     * @param skin The skin value.
     * @param signature The skin signature.
     * @return A skin class with the given values.
     */
    public static ZNPCSkin forSkin(String skin, String signature) {
        return new ZNPCSkin(skin, signature);
    }

    /**
     * Creates a new skin cache.
     *
     * @param skin The skin name or url.
     * @return A skin class with the fetched values.
     */
    public static ZNPCSkin forName(String skin) {
        try {
            // Check if skin value is a url
            new URL(skin).toURI();

            return new ZNPCSkin(SkinBuilder.withData(SkinAPI.GENERATE_API,
                    skin).toSkinFetcher().getProfile());
        } catch (Exception e) {
            return new ZNPCSkin(SkinBuilder.withData(SkinAPI.PROFILE_API, skin).toSkinFetcher().getProfile());
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
         * @param minVersion The minimum required version.
         * @return The layer index.
         */
        public static int findLayerByVersion(int minVersion) {
            int value = V9.layerValue;
            for (SkinLayerValues skinLayerValue : values()) {
                if (minVersion >= skinLayerValue.minVersion) {
                    value = skinLayerValue.layerValue;
                }
            }
            return value;
        }
    }
}
