package io.github.gonalez.znpcservers.npc;

import io.github.gonalez.znpcservers.entity.*;
import io.github.gonalez.znpcservers.npc.function.NpcFunctions;
import io.github.gonalez.znpcservers.entity.*;
import io.github.gonalez.znpcservers.npc.internal.PluginNpcHologram;
import io.github.gonalez.znpcservers.task.Task;
import io.github.gonalez.znpcservers.task.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * Base implementation of a {@link Npc}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public abstract class AbstractNpc implements Npc {
    protected static final Logger LOGGER = Logger.getLogger(AbstractNpc.class.getName());

    protected Entity entity;

    protected final AtomicBoolean loaded = new AtomicBoolean();

    private final NpcModel npcModel;

    protected UUID uuid;

    protected final String name;


    private final NpcClickHandler clickHandler;

    private final PluginNpcHologram npcHologram;

    private final PluginEntityFactory<?> pluginEntityFactory;

    protected PluginEntity pluginEntity;

    public AbstractNpc(
        PluginEntityFactory<?> pluginEntityFactory, NpcModel model,
        NpcName npcName, NpcClickHandler clickHandler, TaskManager taskManager) {
        this.pluginEntityFactory = pluginEntityFactory;
        this.npcModel = model;
        this.name = npcName.generateNpcName(this);
        this.clickHandler = clickHandler;
        this.npcHologram = new PluginNpcHologram(pluginEntityFactory, model);
        final BukkitRunnable loadRunnable =
            new BukkitRunnable() {
            final int MAX_TRIES = 10;
            int tries = 0;
            @Override
            public void run() {
                if (tries++ > MAX_TRIES) {
                    cancel();
                    return;
                }
                World world = Bukkit.getWorld(model.getLocation().getWorldName());
                if (world == null) { // world is not loaded..
                    return;
                }
                cancel();
                try {
                    init(); // world found, load npc..
                    loaded.set(true);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        taskManager.submitTask(Task.of(loadRunnable));
    }

    /**
     * Tries to load the npc.
     *
     * @throws Exception if the npc could not be loaded.
     */
    protected abstract void load() throws Exception;

    @Override
    public void init() throws Exception {
        if (!isLoaded()) {
            throw new IllegalStateException("world for npc location is not loaded.");
        }

        final Location location = npcModel.getLocation().bukkitLocation();
        final PluginEntityTypes entityType = PluginEntityTypes.valueOf("ZOMBIE");
        pluginEntity = pluginEntityFactory.createPluginEntity(
            entityType, PluginEntitySpec.builder()
                .entityLocation(location)
                .entityListener(new PluginEntityListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        for (PluginEntity pluginEntity : npcHologram.getPluginEntity()) {
                            pluginEntity.setLocation(location);
                        }
                    }
                }).build());
        npcHologram.create();
        pluginEntity.setLocation(location);
        load();
        NpcFunctions.findFunctionsForNpc(this).forEach(function -> function.resolve(this));
    }

    @Override
    public boolean isLoaded() {
        return loaded.get();
    }

    @Override
    public void onDisable() throws Exception {

    }

    @Override
    public Location getLocation() {
        return pluginEntity == null ? npcModel.getLocation().bukkitLocation() : pluginEntity.getLocation();
    }

    @Override
    public PluginEntity getPluginEntity() {
        return pluginEntity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public NpcModel getModel() {
        return npcModel;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public NpcHologram getHologram() {
        return npcHologram;
    }

    @Override
    public NpcClickHandler getClickHandler() {
        return clickHandler;
    }
}
