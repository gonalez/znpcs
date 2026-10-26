/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.gonalez.znpcs.commands.list.DefaultCommand;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.listeners.InventoryListener;
import io.github.gonalez.znpcs.listeners.PlayerListener;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.npc.NPCPath;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.npc.task.NPCLoadTask;
import io.github.gonalez.znpcs.npc.task.NPCManagerTask;
import io.github.gonalez.znpcs.npc.task.NPCSaveTask;
import io.github.gonalez.znpcs.npc.task.NpcRefreshSkinTask;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.BungeeUtils;
import io.github.gonalez.znpcs.utility.MetricsLite;
import io.github.gonalez.znpcs.utility.SchedulerUtils;
import io.github.gonalez.znpcs.utility.itemstack.ItemStackSerializer;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ServersNPC extends JavaPlugin {
  private boolean started;
  private java.util.concurrent.ExecutorService skinExecutor;
  private io.github.gonalez.znpcs.skin.SkinFetcher skinFetcher;

  public io.github.gonalez.znpcs.skin.SkinFetcher getSkinFetcher() {
    return skinFetcher;
  }

  public static final String PATH_EXTENSION = ".path";
  public static final File PLUGIN_FOLDER = new File("plugins/ServersNPC");
  public static final Gson GSON =
      new GsonBuilder()
          .registerTypeAdapter(ZLocation.class, (Object) ZLocation.SERIALIZER)
          .registerTypeHierarchyAdapter(ItemStack.class, (Object) new ItemStackSerializer())
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();
  public static SchedulerUtils SCHEDULER;
  public static BungeeUtils BUNGEE_UTILS;

  public void onEnable() {
    Path pluginPath = this.getDataFolder().toPath();
    if (!Bukkit.getMinecraftVersion().equals("26.3"))
      throw new IllegalStateException("This build requires Paper 26.3");
    try {
      Path backup = io.github.gonalez.znpcs.utility.MigrationBackup.backup(pluginPath);
      if (backup != null) getLogger().info("Saved migration backup to " + backup);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot back up existing NPC data; migration stopped", e);
    }
    // Read all data successfully before registering commands, tasks, or players.
    Configuration.DATA.getValue(io.github.gonalez.znpcs.configuration.ConfigurationValue.NPC_LIST);
    SCHEDULER = new SchedulerUtils(this);
    BUNGEE_UTILS = new BungeeUtils(this);
    Path pathPath = pluginPath.resolve("paths");
    try {
      this.loadAllPaths(pathPath);
    } catch (IOException e) {
      this.getLogger().log(Level.WARNING, "Could not load paths", e);
    }
    this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
    new MetricsLite((Plugin) this, 8054);
    skinExecutor =
        new java.util.concurrent.ThreadPoolExecutor(
            2,
            2,
            30,
            java.util.concurrent.TimeUnit.SECONDS,
            new java.util.concurrent.ArrayBlockingQueue<>(64),
            runnable -> {
              Thread thread = new Thread(runnable, "ZNPCs-Skins");
              thread.setDaemon(true);
              return thread;
            });
    io.github.gonalez.znpcs.skin.MineSkinFetch mineSkin;
    try {
      mineSkin = io.github.gonalez.znpcs.skin.MineSkinFetch.fromConfig(pluginPath);
    } catch (IOException e) {
      getLogger().log(Level.WARNING, "Could not read optional MineSkin settings", e);
      mineSkin = new io.github.gonalez.znpcs.skin.MineSkinFetch();
    }
    skinFetcher =
        new io.github.gonalez.znpcs.skin.SkinFetcher(
            skinExecutor,
            com.google.common.collect.ImmutableList.of(
                new io.github.gonalez.znpcs.skin.MojangNameSkinFetch(), mineSkin));
    new DefaultCommand(pathPath, skinFetcher);
    SCHEDULER = new SchedulerUtils((Plugin) this);
    BUNGEE_UTILS = new BungeeUtils((Plugin) this);
    Bukkit.getOnlinePlayers().forEach(ZUser::find);
    new NPCManagerTask(this);
    new NPCSaveTask(this, ConfigurationConstants.SAVE_DELAY);
    new NpcRefreshSkinTask(skinFetcher).runTaskTimer((Plugin) this, 0L, 20L);
    new PlayerListener(this);
    new InventoryListener(this);
    for (NPCModel npcModel : ConfigurationConstants.NPC_LIST) new NPCLoadTask(new NPC(npcModel));
    started = true;
    try {
      io.github.gonalez.znpcs.utility.MigrationBackup.markSuccess(pluginPath);
    } catch (IOException e) {
      getLogger().log(Level.WARNING, "Could not write migration marker", e);
    }
    getLogger()
        .info(
            "Paper 26.3 compatibility layer active; "
                + ConfigurationConstants.NPC_LIST.size()
                + " stored NPCs queued.");
  }

  public void onDisable() {
    if (skinExecutor != null) skinExecutor.shutdownNow();
    if (!started) return;
    Bukkit.getOnlinePlayers().forEach(ZUser::unregister);
    Configuration.SAVE_CONFIGURATIONS.forEach(Configuration::save);
  }

  private void loadAllPaths(Path directory) throws IOException {
    if (Files.isDirectory(directory, new LinkOption[0])) {
      Files.walkFileTree(
          directory,
          (FileVisitor<? super Path>)
              new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                  if (!Files.isDirectory(file, new LinkOption[0])
                      && file.getFileName().toString().endsWith(ServersNPC.PATH_EXTENSION)) {
                    this.loadPath(file.toFile());
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
    Files.createDirectories(directory, new FileAttribute[0]);
  }

  public static NPC createNPC(int id, NPCType npcType, Location location, String name) {
    NPC find = NPC.find(id);
    if (find != null) {
      return find;
    }
    NPCModel pojo =
        new NPCModel(id)
            .withHologramLines(new java.util.ArrayList<>(java.util.List.of(name)))
            .withLocation(new ZLocation(location))
            .withNpcType(npcType);
    ConfigurationConstants.NPC_LIST.add(pojo);
    NPC npc = new NPC(pojo, true);
    npc.onLoad(true);
    return npc;
  }

  public static void deleteNPC(int npcID) {
    NPC npc = NPC.find(npcID);
    if (npc == null) {
      throw new IllegalStateException("can't find npc:  " + npcID);
    }
    NPC.unregister(npcID);
    ConfigurationConstants.NPC_LIST.remove(npc.getNpcPojo());
  }
}
