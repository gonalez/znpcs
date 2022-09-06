package io.github.gonalez.znpcs.setting.internal;

import com.google.gson.Gson;
import io.github.gonalez.znpcs.npc.NpcStore;
import io.github.gonalez.znpcs.npc.function.NpcFunctionRegistry;
import io.github.gonalez.znpcs.http.AsyncHttpClient;
import io.github.gonalez.znpcs.setting.PluginSettings;
import io.github.gonalez.znpcs.task.TaskManager;
import io.github.gonalez.znpcs.setting.PluginSettings.PluginSettingsBuilder;
import io.github.gonalez.znpcs.user.UserStore;

/**
 * A basic, default implementation of the {@link PluginSettingsBuilder}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginSettingsBuilder implements PluginSettings.PluginSettingsBuilder {
    private final AsyncHttpClient asyncHttpClient;
    private final NpcStore npcStore;
    private final UserStore userStore;
    private final NpcFunctionRegistry npcFunctionRegistry;
    private final Gson gson;
    private final TaskManager taskManager;

    private DefaultPluginSettingsBuilder(
        AsyncHttpClient asyncHttpClient, NpcStore npcStore,
        UserStore userStore, NpcFunctionRegistry npcFunctionRegistry,
        Gson gson, TaskManager taskManager) {
        this.asyncHttpClient = asyncHttpClient;
        this.npcStore = npcStore;
        this.userStore = userStore;
        this.npcFunctionRegistry = npcFunctionRegistry;
        this.gson = gson;
        this.taskManager = taskManager;
    }

    public DefaultPluginSettingsBuilder() {
        this(AsyncHttpClient.of(),
            NpcStore.of(),
            UserStore.of(),
            NpcFunctionRegistry.of(),
            new Gson(),
            TaskManager.of(1));
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withHttpClient(AsyncHttpClient asyncHttpClient) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withNpcStore(NpcStore npcStore) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withUserStore(UserStore userStore) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withNpcFunctionRegistry(NpcFunctionRegistry npcFunctionRegistry) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withGson(Gson gson) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings.PluginSettingsBuilder withTaskManager(TaskManager taskManager) {
        return new DefaultPluginSettingsBuilder(
            asyncHttpClient, npcStore, userStore, npcFunctionRegistry, gson, taskManager);
    }

    @Override
    public PluginSettings build() {
        return new PluginSettings() {
            @Override
            public void init() throws Exception {
                getTaskManager().start();
                getNpcStore().init();
            }

            @Override
            public AsyncHttpClient getAsyncHttpClient() {
                return asyncHttpClient;
            }

            @Override
            public NpcStore getNpcStore() {
                return npcStore;
            }

            @Override
            public UserStore getUserStore() {
                return userStore;
            }

            @Override
            public NpcFunctionRegistry getNpcFunctionRegistry() {
                return npcFunctionRegistry;
            }

            @Override
            public TaskManager getTaskManager() {
                return taskManager;
            }

            @Override
            public Gson getGson() {
                return gson;
            }
        };
    }
}
