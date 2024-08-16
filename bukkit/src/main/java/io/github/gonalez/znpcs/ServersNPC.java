package io.github.gonalez.znpcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gonalez.znpcs.ZNPConfigUtils.PluginConfigConfigurationFormat;
import io.github.gonalez.znpcs.commands.list.DefaultCommand;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.configuration.DataConfiguration;
import io.github.gonalez.znpcs.listeners.InventoryListener;
import io.github.gonalez.znpcs.listeners.PlayerListener;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.npc.NPCPath;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.npc.task.NPCManagerTask;
import io.github.gonalez.znpcs.npc.task.NpcRefreshSkinTask;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.BungeeUtils;
import io.github.gonalez.znpcs.utility.MetricsLite;
import io.github.gonalez.znpcs.utility.SchedulerUtils;
import io.github.gonalez.znpcs.utility.itemstack.ItemStackSerializer;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.logging.Level;

public class ServersNPC extends JavaPlugin {
  public static final String PATH_EXTENSION = ".path";

  public static final Gson GSON =
      (new GsonBuilder())
          .registerTypeAdapter(ZLocation.class, ZLocation.SERIALIZER)
          .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer())
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();

  public static SchedulerUtils SCHEDULER;

  public static BungeeUtils BUNGEE_UTILS;

  private ZNPConfigSaveTask configSaveTask;

  @Override
  public void onEnable() {
    Path pluginPath = getDataFolder().toPath();
    Path pathPath = pluginPath.resolve("paths");

    try {
      loadAllPaths(pathPath);
    } catch (IOException e) {
      getLogger().log(Level.WARNING, "Could not load paths", e);
    }

    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    new MetricsLite(this, 8054);

    ZNPConfigUtils.setConfigurationManager(new PluginConfigConfigurationFormat(pluginPath, GSON));
    new DefaultCommand(pathPath);

    SCHEDULER = new SchedulerUtils(this);
    BUNGEE_UTILS = new BungeeUtils(this);
    Bukkit.getOnlinePlayers().forEach(ZUser::find);
    new NPCManagerTask(this);
    (configSaveTask = new ZNPConfigSaveTask()).runTaskTimerAsynchronously(this, 300,
        ZNPConfigUtils.getConfig(ConfigConfiguration.class).saveNpcsDelaySeconds);
    new NpcRefreshSkinTask().runTaskTimerAsynchronously(this, 0L, 20L);
    new PlayerListener(this);
    new InventoryListener(this);
  }

  @Override
  public void onDisable() {
    Bukkit.getOnlinePlayers().forEach(ZUser::unregister);
    if (configSaveTask != null) {
      configSaveTask.run();
    }
  }

  /**
   * Finds all files that qualify as NPC paths. A file is considered a valid NPC path file
   * if its name ends with {@link #PATH_EXTENSION}. This method reads each qualifying file
   * and converts it to an NPC path & initializes it.
   */
  private void loadAllPaths(Path directory) throws IOException {
    if (Files.isDirectory(directory)) {
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
          if (!Files.isDirectory(file)
              && file.getFileName().toString().endsWith(PATH_EXTENSION)) {
            loadPath(file.toFile());
          }
          return FileVisitResult.CONTINUE;
        }

        void loadPath(File file) {
          NPCPath.AbstractTypeWriter abstractTypeWriter =
              NPCPath.AbstractTypeWriter.forFile(
                  file, NPCPath.AbstractTypeWriter.TypeWriter.MOVEMENT);
          abstractTypeWriter.load();
        }
      });
    }
    Files.createDirectories(directory);
  }

  public static NPC createNPC(int id, NPCType npcType, Location location, String name) {
    NPC find = NPC.find(id);
    if (find != null) return find;
    NPCModel pojo =
        (new NPCModel(id))
            .withHologramLines(Collections.singletonList(name))
            .withLocation(new ZLocation(location))
            .withNpcType(npcType);
    // TODO: Make a proper npc saving
    ZNPConfigUtils.getConfig(DataConfiguration.class).npcList.add(pojo);
    return new NPC(pojo, true);
  }

  public static void deleteNPC(int npcID) {
    NPC npc = NPC.find(npcID);
    if (npc == null)
      throw new IllegalStateException("can't find npc:  " + npcID);
    NPC.unregister(npcID);
    // TODO: Make a proper npc saving
    ZNPConfigUtils.getConfig(DataConfiguration.class).npcList.remove(npc.getNpcPojo());
  }
}
