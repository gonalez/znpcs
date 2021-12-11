package io.github.znetworkw.znpcservers.settings;

import com.google.gson.Gson;
import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.npc.NpcStore;
import io.github.znetworkw.znpcservers.npc.function.NpcFunctionRegistry;
import io.github.znetworkw.znpcservers.settings.internal.DefaultPluginSettingsBuilder;
import io.github.znetworkw.znpcservers.task.TaskManager;
import io.github.znetworkw.znpcservers.user.UserStore;

/**
 * Interface for all plugin settings.
 *
 * @see PluginSettingsBuilder
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginSettings {
    /**
     * Creates a new plugin settings builder.
     *
     * @return a new plugin settings builder.
     */
    static PluginSettingsBuilder builder() {
        return new DefaultPluginSettingsBuilder();
    }

    /**
     * Initialize settings.
     *
     * @throws Exception if failed to initialize.
     */
    default void init() throws Exception {};

    /**
     * The http client of the plugin.
     *
     * @return http client.
     */
    AsyncHttpClient getAsyncHttpClient();

    /**
     * The npc store of the plugin.
     *
     * @return npc store.
     */
    NpcStore getNpcStore();

    /**
     * The user store of the plugin.
     *
     * @return user store.
     */
    UserStore getUserStore();

    /**
     * The function registry of the plugin.
     *
     * @return registry.
     */
    NpcFunctionRegistry getNpcFunctionRegistry();

    /**
     * The task manager of the plugin.
     *
     * @return task manager.
     */
    TaskManager getTaskManager();

    /**
     * The gson instance of the plugin.
     *
     * @return gson instance.
     */
    Gson getGson();

    /**
     * Builder interface to create {@link PluginSettings}.
     */
    interface PluginSettingsBuilder {
        /**
         * Sets the plugin http client.
         *
         * @param httpClient the http client.
         * @return this.
         */
        PluginSettingsBuilder withHttpClient(AsyncHttpClient httpClient);

        /**
         * Sets the plugin npc store.
         *
         * @param npcStore the npc store.
         * @return this.
         */
        PluginSettingsBuilder withNpcStore(NpcStore npcStore);

        /**
         * Sets the plugin user store.
         *
         * @param userStore the user store.
         * @return this.
         */
        PluginSettingsBuilder withUserStore(UserStore userStore);

        /**
         * Sets the plugin npc function registry.
         *
         * @param functionRegistry the function registry.
         * @return this.
         */
        PluginSettingsBuilder withNpcFunctionRegistry(NpcFunctionRegistry functionRegistry);

        /**
         * Sets the plugin gson.
         *
         * @param gson the gson instance.
         * @return this.
         */
        PluginSettingsBuilder withGson(Gson gson);

        /**
         * Sets the plugin task manager.
         *
         * @param taskManager the task manager.
         * @return this.
         */
        PluginSettingsBuilder withTaskManager(TaskManager taskManager);

        /**
         * Creates a new {@link PluginSettings} based from this builder.
         *
         * @return a new {@link PluginSettings} based from this builder.
         */
        PluginSettings build();
    }
}
