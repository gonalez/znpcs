package io.github.znetworkw.znpcservers.skin;

/**
 * Interface used for the {@link SkinFetcher#fetchProfile} method.
 * use this interface for getting the texture values after a skin fetching is finish.
 */
public interface SkinFetcherResult {
    /**
     * Called when a skin is fetched.
     *
     * @param values The skin values.
     */
    void onDone(String[] values);
}
