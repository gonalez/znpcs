package io.github.gonalez.znpcservers.skin;

import io.github.gonalez.znpcservers.skin.internal.DefaultSkinFetcherResultBuilder;

import java.io.Reader;

/**
 * Represents the result of an {@link SkinFetcherService}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 * @see SkinFetcherService#parse(Reader)
 */
public interface SkinFetcherResult {
    /**
     * Creates a new, skin fetcher result builder.
     *
     * @return a new skin fetcher result builder.
     */
    static SkinFetcherResultBuilder builder() {
        return new DefaultSkinFetcherResultBuilder();
    }

    /**
     * Creates a new skin result with the given texture and signature.
     *
     * @param texture the result skin texture.
     * @param signature the result skin signature.
     * @return a new skin result with the given texture and signature.
     */
    static SkinFetcherResult of(String texture, String signature) {
        return builder()
            .withTexture(texture)
            .withSignature(signature)
            .build();
    }

    /**
     * The skin texture.
     *
     * @return skin texture.
     */
    String getTexture();

    /**
     * The skin signature.
     *
     * @return skin signature.
     */
    String getSignature();

    /**
     * Builder to create {@link SkinFetcherResult}s.
     */
    interface SkinFetcherResultBuilder {
        /**
         * Sets the result texture.
         *
         * @param texture skin texture.
         * @return this.
         */
        SkinFetcherResultBuilder withTexture(String texture);

        /**
         * Sets the result signature.
         *
         * @param signature skin signature.
         * @return this.
         */
        SkinFetcherResultBuilder withSignature(String signature);

        /**
         * A new skin fetch result based from this builder.
         *
         * @return a new skin fetch result based from this builder.
         */
        SkinFetcherResult build();
    }
}
