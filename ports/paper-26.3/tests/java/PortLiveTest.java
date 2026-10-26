import com.google.gson.Gson;
import com.google.gson.JsonParser;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.*;
import io.github.gonalez.znpcs.npc.event.*;
import java.nio.file.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/** Real Mojang lookup through the existing skin command, then protocol-client click actions. */
public final class PortLiveTest extends JavaPlugin implements Listener {
  private boolean right, left, skinReady, finished;
  private int events;
  private List<NPCAction> previousActions;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
    Bukkit.getScheduler()
        .runTaskLater(
            this,
            () -> {
              if (!finished) finish("FAIL: skin or left/right click timeout");
            },
            850);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    player.setOp(true);
    player.setGameMode(GameMode.CREATIVE);
    player.setAllowFlight(true);
    Bukkit.getScheduler()
        .runTaskLater(
            this,
            () -> {
              try {
                player.teleport(new Location(player.getWorld(), 0.5, -59, 3.0, 180, 0));
                player.setFlying(true);
                NPC npc = NPC.find(42);
                UUID oldUuid = npc.getUUID();
                previousActions = new ArrayList<>(npc.getNpcPojo().getClickActions());
                npc.getNpcPojo().getClickActions().clear();
                npc.getNpcPojo()
                    .getClickActions()
                    .add(
                        ServersNPC.GSON.fromJson(
                            "{\"actionType\":\"CONSOLE\",\"clickType\":\"RIGHT\",\"action\":\"porttest"
                                + " right\",\"delay\":0}",
                            NPCAction.class));
                npc.getNpcPojo()
                    .getClickActions()
                    .add(
                        ServersNPC.GSON.fromJson(
                            "{\"actionType\":\"CONSOLE\",\"clickType\":\"LEFT\",\"action\":\"porttest"
                                + " left\",\"delay\":0}",
                            NPCAction.class));
                npc.getNpcPojo()
                    .getNpcEquip()
                    .put(ItemSlot.HELMET, new ItemStack(Material.DIAMOND_HELMET));
                npc.deleteViewers();
                player.performCommand("znpcs create 43 ZOMBIE TestZombie");
                player.performCommand("znpcs type 43 COW");
                if (NPC.find(43) == null || NPC.find(43).getNpcPojo().getNpcType() != NPCType.COW)
                  throw new AssertionError("create/type command");
                player.performCommand("znpcs scale 43 2");
                if (!Double.valueOf(2)
                    .equals(NPC.find(43).getNpcPojo().getBukkitAttributes().get("SCALE")))
                  throw new AssertionError("scale command");
                String coloured =
                    io.github.gonalez.znpcs.utility.PlaceholderUtils.formatPlaceholder(
                        player, "&aconversation-colour-ok");
                if (!coloured.contains("\u00a7a"))
                  throw new AssertionError("5.1 conversation colour formatting");
                player.sendMessage(coloured);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "znpcs skin 42 Notch");
                new BukkitRunnable() {
                  int attempts;

                  @Override
                  public void run() {
                    try {
                      if (++attempts > 120) {
                        cancel();
                        finish("FAIL: real Mojang skin lookup did not complete");
                        return;
                      }
                      if (npc.getUUID().equals(oldUuid)) return;
                      String value = npc.getNpcPojo().getSkin(),
                          signature = npc.getNpcPojo().getSignature();
                      var data =
                          JsonParser.parseString(
                                  new String(
                                      Base64.getDecoder().decode(value),
                                      java.nio.charset.StandardCharsets.UTF_8))
                              .getAsJsonObject();
                      if (!data.get("profileId")
                          .getAsString()
                          .replace("-", "")
                          .equals("069a79f444e94726a5befca90e38aaf5"))
                        throw new AssertionError("real Notch skin identity");
                      if (signature.length() < 100)
                        throw new AssertionError("signed skin property");
                      var handle = (net.minecraft.server.level.ServerPlayer) npc.getNmsEntity();
                      if (!handle.getUUID().equals(npc.getGameProfile().id())
                          || !handle
                              .getGameProfile()
                              .properties()
                              .get("textures")
                              .iterator()
                              .next()
                              .value()
                              .equals(value))
                        throw new AssertionError("updated native profile and UUID");
                      skinReady = true;
                      cancel();
                      Files.writeString(
                          Path.of("live-target.json"),
                          new Gson()
                              .toJson(
                                  Map.of(
                                      "player",
                                      npc.getEntityID(),
                                      "mob",
                                      NPC.find(43).getEntityID(),
                                      "expectedTexture",
                                      value,
                                      "skinUuid",
                                      npc.getUUID().toString())));
                      getLogger()
                          .info(
                              "Real Mojang skin applied; scale and conversation-colour checks"
                                  + " passed");
                    } catch (Throwable error) {
                      cancel();
                      getLogger()
                          .log(java.util.logging.Level.SEVERE, "Skin integration failed", error);
                      finish("FAIL: " + error);
                    }
                  }
                }.runTaskTimer(this, 1, 5);
              } catch (Throwable error) {
                getLogger().log(java.util.logging.Level.SEVERE, "Live setup failed", error);
                finish("FAIL: " + error);
              }
            },
            40);
  }

  @EventHandler
  public void onNpcClick(NPCInteractEvent event) {
    events++;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 1) {
      if (args[0].equals("right")) right = true;
      if (args[0].equals("left")) left = true;
      getLogger().info("ACTION " + args[0]);
      if (right && left)
        Bukkit.getScheduler()
            .runTaskLater(
                this,
                () ->
                    finish(
                        events >= 2 && skinReady
                            ? "PASS: real Mojang skin command, updated signed profile, scale,"
                                  + " colours, join, spawn and left/right actions"
                            : "FAIL: event or skin state"),
                40);
    }
    return true;
  }

  private void finish(String result) {
    if (finished) return;
    finished = true;
    try {
      Files.writeString(Path.of("live-server-result.txt"), result + "\nevents=" + events);
    } catch (Exception ignored) {
    }
    Bukkit.shutdown();
  }

  @Override
  public void onDisable() {
    NPC npc = NPC.find(42);
    if (npc != null && previousActions != null) {
      npc.getNpcPojo().getClickActions().clear();
      npc.getNpcPojo().getClickActions().addAll(previousActions);
    }
    if (NPC.find(43) != null) ServersNPC.deleteNPC(43);
  }
}
