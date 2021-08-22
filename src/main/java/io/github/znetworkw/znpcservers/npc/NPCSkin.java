package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.skin.SkinFetcherBuilder;
import io.github.znetworkw.znpcservers.skin.SkinFetcherResult;
import io.github.znetworkw.znpcservers.utility.Utils;

/**
 * This class is intended to contain the skin information,
 * used for setting the skin of a {@link NPC}.
 * <p />
 * Create a new {@link NPCSkin} using {@link #forName(String, SkinFetcherResult)} or
 * {@link #forValues(String...)} if you already know the skin values.
 */
public class NPCSkin {
    /** A empty string. */
    private static final String EMPTY_STRING = "";

    /** A empty string array of size 1. */
    private static final String[] EMPTY_ARRAY = new String[]{EMPTY_STRING, EMPTY_STRING};

    /** The skin layer index. */
    private static final int LAYER_INDEX = SkinLayerValues.findLayerByVersion();

    /** The skin texture. */
    private final String texture;

    /** The skin signature. */
    private final String signature;

    /**
     * Constructor for creating a {@link NPCSkin} object with the texture values
     * <p>The first element of the array will be used as the texture of the npc {@link com.mojang.authlib.GameProfile}
     * and the second texture as the signature.
     * @throws IllegalArgumentException If the array size is 0 or less.
     */
    protected NPCSkin(String... values) {
        if (values.length < 1) {
            throw new IllegalArgumentException("Length cannot be zero or negative.");
        }
        this.texture = values[0];
        this.signature = values[1];
    }

    /**
     * Returns the skin texture.
     */
    public String getTexture() {
        return texture;
    }

    /**
     * Returns the skin signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Returns the skin layer index for the current bukkit version.
     */
    public int getLayerIndex() {
        return LAYER_INDEX;
    }

    /**
     * @param values The skin values.
     * @return A {@link NPCSkin} instance with the given values.
     */
    public static NPCSkin forValues(String...values) {
        return new NPCSkin(values.length > 0 ? values : EMPTY_ARRAY);
    }

    /**
     * Fetches a skin profile by its name or url.
     *
     * @param skin The skin name or url.
     * @param skinResultCallback The callback that will be invoked when the skin is fetched.
     */
    public static void forName(String skin, SkinFetcherResult skinResultCallback) {
        SkinFetcherBuilder.withName(skin).toSkinFetcher().fetchProfile(skinResultCallback);
    }

    /**
     * Used to find the skin layer index for the current bukkit version.
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
         * Creates a new skin layer identification.
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
         * Returns the layer index for the current bukkit version.
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
