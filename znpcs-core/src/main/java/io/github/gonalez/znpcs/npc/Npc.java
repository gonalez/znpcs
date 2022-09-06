package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.entity.PluginEntity;
import io.github.gonalez.znpcs.entity.PluginEntityFactory;
import io.github.gonalez.znpcs.npc.internal.DefaultNpc;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface Npc {
    /**
     * Creates a new, default npc for the specified information.
     *
     * @param factory factory that will be used to create entity.
     * @param model model that will be used to obtain and store information from the npc.
     * @param npcName the strategy that will be used to generate an unique name for the npc.
     * @param clickHandler
     * @return a new, default npc.
     */
    static Npc of(PluginEntityFactory<?> factory, NpcModel model,
        NpcName npcName, NpcClickHandler clickHandler) {
        return new DefaultNpc(factory, model, npcName, clickHandler);
    }

    /**
     * Convenience method for {@link #of(PluginEntityFactory, NpcModel, NpcName, NpcClickHandler)}
     * and {@link Npc#init()} in one go.
     *
     * @param factory factory that will be used to create entity.
     * @param model model that will be used to obtain and store information from the npc.
     * @return a new, default npc.
     * @throws Exception if the npc could not be loaded.
     * @see #of(PluginEntityFactory, NpcModel, NpcName, NpcClickHandler)
     * @see Npc#init()
     */
    static Npc of(PluginEntityFactory<?> factory, NpcModel model) throws Exception {
        Npc npc = of(factory, model, NpcName.of(), NpcClickHandler.of());
        try {
            npc.init();
            return npc;
        } catch (Exception exception) {
            throw new IllegalStateException("err" , exception);
        }
    }

    /**
     * Called when a npc is created for the first time to load the basic functions.
     *
     * @throws Exception if the npc could not be loaded.
     */
    void init() throws Exception;

    /**
     * Executed when deleting the npc.
     *
     * @throws Exception if failed to call the method.
     */
    void onDisable() throws Exception;

    /**
     * The npc uuid.
     *
     * @return npc uuid.
     */
    UUID getUUID();

    /**
     * The npc name.
     *
     * @return npc name.
     */
    String getName();

    /**
     * The npc location.
     *
     * @return npc location.
     */
    Location getLocation();

    /**
     * Returns {@code true} if this npc is loaded.
     *
     * @return {@code true} if this npc is loaded.
     */
    boolean isLoaded();

    /**
     * The entity for this npc.
     *
     * @return entity for this npc.
     */
    PluginEntity getPluginEntity();

    /**
     * The model for this npc.
     *
     * @return model for this npc.
     */
    NpcModel getModel();

    NpcHologram getHologram();

    NpcClickHandler getClickHandler();
}
