package io.github.gonalez.znpcs;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import io.github.gonalez.znpcs.command.PluginCommand;
import io.github.gonalez.znpcs.command.PluginCommandExec;
import io.github.gonalez.znpcs.command.internal.DefaultPluginSubCommandFinder;
import io.github.gonalez.znpcs.command.internal.plugin.*;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.listener.PlayerListener;
import io.github.gonalez.znpcs.configuration.ConfigurationSaveTask;
import io.github.gonalez.znpcs.npc.internal.plugin.GlowNpcFunction;
import io.github.gonalez.znpcs.setting.PluginSettings;
import io.github.gonalez.znpcs.user.User;
import io.github.gonalez.znpcs.utility.BungeeUtils;
import io.github.gonalez.znpcs.utility.MetricsLite;
import io.github.gonalez.znpcs.utility.PluginLocation;
import io.github.gonalez.znpcs.utility.SchedulerUtils;
import io.github.gonalez.znpcs.utility.itemstack.ItemStackSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * The main class of the plugin.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ZNPCs extends JavaPlugin {
    private static final String PLUGIN_NAME = "ZNPCs";

    public static final File PLUGIN_FOLDER = new File("plugins/" + PLUGIN_NAME);
    public static final File PATH_FOLDER = new File("plugins/" + PLUGIN_NAME + "/paths");

    private static final ImmutableList<File> FILES = ImmutableList.of(PLUGIN_FOLDER, PATH_FOLDER);

    public static final PluginSettings SETTINGS = PluginSettings.builder()
        .withGson(new GsonBuilder()
            .registerTypeAdapter(PluginLocation.class, PluginLocation.SERIALIZER)
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create())
        .build()/*default*/;

    private static final int PLUGIN_ID = 8054;

    public static SchedulerUtils SCHEDULER;
    public static BungeeUtils BUNGEE_UTILS;

    @Override
    public void onEnable() { ;
        for (File file : FILES) {
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    disablePlugin(String.format("Could not create folder %s", file.getName()));
                    return;
                }
            }
        }

        SCHEDULER = new SchedulerUtils(this);
        try {
            SETTINGS.init();
            // register custom functions
            SETTINGS.getNpcFunctionRegistry().register(new GlowNpcFunction());
        } catch (Exception exception) {
            exception.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        BUNGEE_UTILS = new BungeeUtils(this);

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), this);

        final PluginCommand baseCommand = PluginCommand.builder("znpcs")
            .addSubCommand(new NpcCreateSubCommand())
            .addSubCommand(new NpcDeleteSubCommand())
            .addSubCommand(new NpcSetSkinSubCommand())
            .addSubCommand(new NpcToggleSubCommand())
            .addSubCommand(new NpcListSubCommand())
            .addSubCommand(new NpcMoveSubCommand())
            .addSubCommand(new NpcTeleportCommand())
            .addSubCommand(new NpcEquipSubCommand())
            .addSubCommand(new NpcAddLineCommand())
            .addSubCommand(new NpcRemoveLineCommand())
            .build();
        baseCommand.init(PluginCommandExec.of(baseCommand, DefaultPluginSubCommandFinder.INSTANCE));

        new MetricsLite(this, PLUGIN_ID);

        new PlayerListener();

        new ConfigurationSaveTask(ConfigurationConstants.SAVE_DELAY);

        for (final Player player : getServer().getOnlinePlayers()) {
            try {
                SETTINGS.getUserStore().addUser(User.of(player));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        Configuration.SAVE_CONFIGURATIONS.forEach(Configuration::save);
        SETTINGS.getNpcStore().getNpcs().forEach(npc -> {
            try {
                npc.onDisable();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        SETTINGS.getAsyncHttpClient().shutdown();
    }

    /** Disables the plugin. */
    private void disablePlugin(String disableMessage) {
        getServer().getPluginManager().disablePlugin(this);
        getLogger().log(Level.INFO, disableMessage);

    }
}
