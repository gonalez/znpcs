package io.github.znetworkw.znpcservers.npc.internal;

import io.github.znetworkw.znpcservers.configuration.Configuration;
import io.github.znetworkw.znpcservers.configuration.ConfigurationValue;
import io.github.znetworkw.znpcservers.entity.*;
import io.github.znetworkw.znpcservers.npc.NpcModel;
import io.github.znetworkw.znpcservers.npc.NpcHologram;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class PluginNpcHologram implements NpcHologram {
    private static final PluginEntityType PLUGIN_ENTITY_TYPE = PluginEntityTypes.ARMOR_STAND;
    private static final double LINE_SPACING = Configuration.CONFIGURATION.getValue(ConfigurationValue.LINE_SPACING);

    private final PluginEntityFactory<?> factory;
    private final NpcModel npcModel;

    private final List<PluginEntity> pluginEntities = new ArrayList<>();

    public PluginNpcHologram(
        PluginEntityFactory<?> factory, NpcModel npcModel) {
        this.factory = factory;
        this.npcModel = npcModel;
    }

    @Override
    public void create() throws Exception {
        final Location location = npcModel.getLocation().bukkitLocation();
        for (final String line : npcModel.getHologramLines()) {
            PluginEntity entity = factory.createPluginEntity(PLUGIN_ENTITY_TYPE,  PluginEntitySpec.of(location));
            entity.setLocation(location);
            entity.setName(line);
            entity.setInvisible(true);
            pluginEntities.add(entity);
        }
    }

    @Override
    public Iterable<PluginEntity> getPluginEntity() {
        return pluginEntities;
    }
}
