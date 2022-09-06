package io.github.gonalez.znpcs.entity;

import io.github.gonalez.znpcs.entity.internal.PluginEntitySpecBuilderImpl;
import org.bukkit.Location;

/**
 * Contains specifications for an entity when creating an new entity
 * via the {@link PluginEntityFactory entity factory}.
 */
public interface PluginEntitySpec {
    /**
     * Builds a new, default entity specification with the specified location.
     *
     * @param location the entity location.
     * @return a new entity specification.
     */
    static PluginEntitySpec of(Location location) {
        return builder().entityLocation(location).build();
    }

    /**
     * Creates a new, default entity specification builder.
     *
     * @return a new entity specification builder.
     */
    static PluginEntitySpecBuilder builder() {
        return new PluginEntitySpecBuilderImpl();
    }

    /**
     * The location where the entity will be spawned.
     *
     * @return entity location.
     */
    Location getLocation();

    /**
     *
     * @return
     */
    PluginEntityListener getListener();

    /**
     * A builder to create an {@link PluginEntitySpec}.
     */
    interface PluginEntitySpecBuilder {
        /**
         * Sets the location for the entity.
         *
         * @param location the entity location.
         * @return this.
         */
        PluginEntitySpecBuilder entityLocation(Location location);

        /**
         * Sets the listener for the entity.
         *
         * @param listener the entity event listener.
         * @return this.
         */
        PluginEntitySpecBuilder entityListener(PluginEntityListener listener);

        /**
         * A new {@link PluginEntitySpec} based from this builder.
         *
         * @return A new entity specification based from this builder.
         */
        PluginEntitySpec build();
    }
}
