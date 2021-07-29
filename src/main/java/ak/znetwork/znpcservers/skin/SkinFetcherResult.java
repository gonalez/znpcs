package ak.znetwork.znpcservers.skin;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 25/7/2021
 */
public interface SkinFetcherResult {
    /**
     * Called when a skin is fetched.
     *
     * @param values The skin values.
     */
    void onDone(String[] values);
}
