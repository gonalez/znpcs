package io.github.znetworkw.znpcservers.settings;

import com.google.gson.Gson;
import io.github.znetworkw.znpcservers.http.AsyncHttpClient;
import io.github.znetworkw.znpcservers.npc.NpcStore;
import io.github.znetworkw.znpcservers.npc.function.NpcFunctionRegistry;
import io.github.znetworkw.znpcservers.settings.internal.DefaultPluginSettingsBuilder;
import io.github.znetworkw.znpcservers.task.TaskManager;
import io.github.znetworkw.znpcservers.user.UserStore;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginSettings {
    static PluginSettingsBuilder builder() {
        return new DefaultPluginSettingsBuilder();
    }

    AsyncHttpClient getAsyncHttpClient();
    NpcStore getNpcStore();
    UserStore getUserStore();

    NpcFunctionRegistry getNpcFunctionRegistry();

    TaskManager getTaskManager();

    Gson getGson();

    interface PluginSettingsBuilder {
        PluginSettingsBuilder withHttpClient(AsyncHttpClient asyncHttpClient);
        PluginSettingsBuilder withNpcStore(NpcStore npcStore);
        PluginSettingsBuilder withUserStore(UserStore userStore);
        PluginSettingsBuilder withNpcFunctionRegistry(NpcFunctionRegistry npcFunctionRegistry);
        PluginSettingsBuilder withGson(Gson gson);
        PluginSettingsBuilder withTaskManager(TaskManager taskManager);

        PluginSettings build();
    }
}
