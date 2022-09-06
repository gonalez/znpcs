package io.github.gonalez.znpcs.npc.internal;

import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationValue;
import io.github.gonalez.znpcs.entity.*;
import io.github.gonalez.znpcs.npc.NpcHologram;
import io.github.gonalez.znpcs.npc.NpcModel;
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
