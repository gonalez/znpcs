package io.github.znetworkw.znpcservers.entity.internal;

import com.google.common.collect.Iterables;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.entity.*;
import io.github.znetworkw.znpcservers.packet.PluginPacketCache;
import io.github.znetworkw.znpcservers.task.Task;
import io.github.znetworkw.znpcservers.task.TaskManager;
import io.github.znetworkw.znpcservers.user.User;
import io.github.znetworkw.znpcservers.user.UserStore;
import org.bukkit.Location;

import java.util.HashSet;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginEntityFactory implements PluginEntityFactory<PerUserPluginEntity> {
    private final UserStore userStore;
    private final TaskManager taskManager;

    public DefaultPluginEntityFactory(UserStore userStore, TaskManager taskManager) {
        this.userStore = userStore;
        this.taskManager = taskManager;
    }

    @Override
    public PerUserPluginEntity createPluginEntity(
        PluginEntityType pluginEntityType, PluginEntitySpec spec) throws Exception {
        final PluginEntityTypeData pluginEntityTypeData = pluginEntityType.getData();
        if (pluginEntityTypeData == null) {
            throw new IllegalStateException("");
        }
        final Object transformedEntity = PluginEntityTypeTransformer.of().transform(
            pluginEntityTypeData,
            CacheRegistry.GET_HANDLE_WORLD_METHOD.invoke(spec.getLocation().getWorld()));
        final ReflectivePacketPluginEntity reflectivePacketPluginEntity = new ReflectivePacketPluginEntity(
            pluginEntityType, transformedEntity,
            PluginEntityPackets.of(PluginPacketCache.of()), new HashSet<>(), spec.getListener());
        taskManager.submitTask(Task.of(() -> {
            for (User user : userStore.getUsers()) {
                final boolean isViewer = Iterables.contains(reflectivePacketPluginEntity.getViewers(), user);
                if (user.getLocation().distance(reflectivePacketPluginEntity.getLocation())
                    < ConfigurationConstants.VIEW_DISTANCE) {
                    if (!isViewer) {
                        try {
                            reflectivePacketPluginEntity.onSpawn(user);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } else {
                    if (isViewer) {
                        try {
                            reflectivePacketPluginEntity.onDelete(user);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        }, reflectivePacketPluginEntity::isAlive));
        return reflectivePacketPluginEntity;
    }
}
