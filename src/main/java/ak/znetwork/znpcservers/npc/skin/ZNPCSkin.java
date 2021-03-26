package ak.znetwork.znpcservers.npc.skin;

import java.net.URL;

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
}
