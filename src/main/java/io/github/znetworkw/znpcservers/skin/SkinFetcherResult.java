package io.github.znetworkw.znpcservers.skin;

import io.github.znetworkw.znpcservers.skin.internal.DefaultSkinFetcherResultBuilder;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface SkinFetcherResult {
    /**
     * Creates a new builder for creating a skin result.
     *
     * @return a new skin fetcher result builder.
     */
    static SkinFetcherResultBuilder builder() {
        return new DefaultSkinFetcherResultBuilder();
    }

    static SkinFetcherResult of(String texture, String signature) {
        return builder().withTexture(texture).withSignature(signature).build();
    }

    String getTexture();
    String getSignature();

    interface SkinFetcherResultBuilder {
        SkinFetcherResultBuilder withTexture(String texture);
        SkinFetcherResultBuilder withSignature(String signature);

        SkinFetcherResult build();
    }
}
