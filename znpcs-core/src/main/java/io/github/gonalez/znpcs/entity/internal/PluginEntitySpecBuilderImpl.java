package io.github.gonalez.znpcs.entity.internal;

import io.github.gonalez.znpcs.entity.PluginEntityListener;
import io.github.gonalez.znpcs.entity.PluginEntitySpec;
import org.bukkit.Location;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class PluginEntitySpecBuilderImpl implements PluginEntitySpec.PluginEntitySpecBuilder {
    private Location location;
    private PluginEntityListener listener = PluginEntityListener.noop();

    @Override
    public PluginEntitySpec.PluginEntitySpecBuilder entityLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public PluginEntitySpec.PluginEntitySpecBuilder entityListener(PluginEntityListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public PluginEntitySpec build() {
        return new PluginEntitySpec() {
            @Override
            public Location getLocation() {
                return location;
            }

            @Override
            public PluginEntityListener getListener() {
                return listener;
            }
        };
    }
}
