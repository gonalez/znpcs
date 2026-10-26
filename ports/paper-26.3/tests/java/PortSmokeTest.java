import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.configuration.*;
import io.github.gonalez.znpcs.npc.*;
import io.github.gonalez.znpcs.npc.packet.Packet263;
import io.netty.buffer.Unpooled;
import java.nio.file.*;
import java.util.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/** Run inside the exact target server, exercising native constructors and network codecs. */
public final class PortSmokeTest extends JavaPlugin {
  private final List<String> checks = new ArrayList<>();

  private void check(boolean ok, String label) {
    if (!ok) throw new AssertionError(label);
    checks.add(label);
  }

  @Override
  public void onEnable() {
    Bukkit.getScheduler()
        .runTaskLater(
            this,
            () -> {
              try {
                runChecks();
                Files.writeString(
                    Path.of("smoke-result.txt"), "PASS\n" + String.join("\n", checks));
              } catch (Throwable e) {
                getLogger().log(java.util.logging.Level.SEVERE, "SMOKE TEST FAILED", e);
                try {
                  Files.writeString(
                      Path.of("smoke-result.txt"), "FAIL\n" + e + "\n" + String.join("\n", checks));
                } catch (Exception ignored) {
                }
              } finally {
                Bukkit.shutdown();
              }
            },
            100L);
  }

  private void encode(Packet<?> packet, org.bukkit.World world) {
    var b =
        new RegistryFriendlyByteBuf(
            Unpooled.buffer(), ((CraftWorld) world).getHandle().registryAccess());
    try {
      if (packet instanceof ClientboundAddEntityPacket p)
        ClientboundAddEntityPacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundPlayerInfoUpdatePacket p)
        ClientboundPlayerInfoUpdatePacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundTeleportEntityPacket p)
        ClientboundTeleportEntityPacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundSetEntityDataPacket p)
        ClientboundSetEntityDataPacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundSetEquipmentPacket p)
        ClientboundSetEquipmentPacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundSetPlayerTeamPacket p)
        ClientboundSetPlayerTeamPacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundPlayerInfoRemovePacket p)
        ClientboundPlayerInfoRemovePacket.STREAM_CODEC.encode(b, p);
      else if (packet instanceof ClientboundUpdateAttributesPacket p)
        ClientboundUpdateAttributesPacket.STREAM_CODEC.encode(b, p);
      else throw new AssertionError("Untested codec " + packet.getClass());
      check(b.writerIndex() > 0, "encoded " + packet.getClass().getSimpleName());
    } finally {
      b.release();
    }
  }

  private void runChecks() throws Exception {
    check(Bukkit.getPluginManager().isPluginEnabled("ServersNPC"), "plugin enabled");
    check(Bukkit.getMinecraftVersion().equals("26.3"), "exact Minecraft target");
    check(
        io.github.gonalez.znpcs.utility.Utils.BUKKIT_VERSION.compareTo(
                io.github.gonalez.znpcs.utility.Version.fromString("26.3"))
            == 0,
        "calendar version parsed");
    NPC legacy = NPC.find(42);
    check(legacy != null, "legacy JSON NPC loaded");
    check(
        legacy
            .getNpcPojo()
            .getHologramLines()
            .equals(List.of("&aWillkommen – Test", "&eZweite Zeile")),
        "legacy hologram lines preserved");
    check(
        legacy.getNpcPojo().getSkin().equals("dGVzdA==")
            && legacy.getNpcPojo().getSignature().equals("signature-fixture"),
        "legacy signed skin preserved");
    check(
        legacy.getNpcPojo().getClickActions().get(0).getAction().equals("say migration-ok"),
        "legacy click action preserved");
    check(
        Files.list(Path.of("plugins/ServersNPC/backups")).findAny().isPresent(),
        "pre-migration backup exists");
    org.bukkit.World world = Bukkit.getWorlds().getFirst();
    Location location = new Location(world, 0.5, -59, 0.5);
    int id = 1000;
    for (NPCType type : NPCType.values()) {
      NPC npc = ServersNPC.createNPC(id++, type, location, "Smoke");
      check(npc.getNmsEntity() != null, "created " + type);
      if (type != NPCType.PLAYER)
        check(type.getConstructor() != null, "legacy command allows " + type);
      var packets = npc.getPackets().getProxyInstance();
      encode(
          (Packet<?>)
              packets.getSpawnPacket(
                  npc.getEntityID(),
                  npc.getUUID(),
                  location,
                  ((org.bukkit.entity.Entity) npc.getBukkitEntity()).getType(),
                  npc.getNmsEntity()),
          world);
      encode((Packet<?>) packets.getMetadataPacket(npc.getEntityID(), npc.getNmsEntity()), world);
      encode((Packet<?>) packets.getTeleportPacket(npc.getEntityID(), npc.getNmsEntity()), world);
      if (type == NPCType.PLAYER) {
        encode(
            Packet263.playerInfo((net.minecraft.server.level.ServerPlayer) npc.getNmsEntity()),
            world);
        encode(Packet263.removePlayer(npc.getUUID()), world);
        npc.changeSkin(NPCSkin.forValues("dGVzdDI=", "new-signature"));
        check(
            npc.getGameProfile().properties().containsKey("textures"),
            "immutable profile skin update");
      }
      for (Object p : packets.updateScoreboard(npc)) encode((Packet<?>) p, world);
      for (ItemSlot slot : ItemSlot.values())
        encode(
            (Packet<?>)
                packets.convertItemStack(
                    npc.getEntityID(), slot, new ItemStack(Material.DIAMOND_SWORD)),
            world);
      ServersNPC.deleteNPC(npc.getNpcPojo().getId());
    }
    check(NPC.all().size() == 1, "temporary NPCs cleaned up");
    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
    item.editMeta(
        meta -> meta.displayName(net.kyori.adventure.text.Component.text("Ausruestung – Test")));
    String encodedItem = ServersNPC.GSON.toJson(item, ItemStack.class);
    ItemStack decodedItem = ServersNPC.GSON.fromJson(encodedItem, ItemStack.class);
    check(item.isSimilar(decodedItem), "stored equipment metadata round trip");
    NPC villager = ServersNPC.createNPC(9001, NPCType.VILLAGER, location, "Villager");
    NPCType.VILLAGER.updateCustomization(villager, "setProfession", new String[] {"LIBRARIAN"});
    check(
        ((org.bukkit.entity.Villager) villager.getBukkitEntity())
            .getProfession()
            .getKey()
            .getKey()
            .equals("librarian"),
        "registry-backed legacy profession");
    ServersNPC.deleteNPC(9001);
    NPC cat = ServersNPC.createNPC(9002, NPCType.CAT, location, "Cat");
    NPCType.CAT.updateCustomization(cat, "setCatType", new String[] {"SIAMESE"});
    check(
        ((org.bukkit.entity.Cat) cat.getBukkitEntity())
            .getCatType()
            .getKey()
            .getKey()
            .equals("siamese"),
        "registry-backed legacy cat variant");
    ServersNPC.deleteNPC(9002);
    legacy.getNpcPojo().getBukkitAttributes().put("SCALE", 2.0);
    for (Object packet : legacy.attributePackets()) encode((Packet<?>) packet, world);
    check(legacy.getNpcPojo().getBukkitAttributes().get("SCALE") == 2.0, "5.2 scale data retained");
    check(
        io.github.gonalez.znpcs.utility.Version.fromString("1.21.11-R0.1-SNAPSHOT")
                .compareTo(io.github.gonalez.znpcs.utility.Version.fromString("21.11"))
            == 0,
        "5.3 shorthand version ABI retained");

    Configuration.DATA.save();
    JsonObject saved =
        JsonParser.parseString(Files.readString(Path.of("plugins/ServersNPC/data.json")))
            .getAsJsonArray()
            .get(0)
            .getAsJsonObject();
    check(
        saved.get("id").getAsInt() == 42 && saved.get("npcType").getAsString().equals("PLAYER"),
        "legacy JSON id and enum schema retained");
    check(
        Configuration.CONFIGURATION.<Integer>getValue(ConfigurationValue.VIEW_DISTANCE) == 48,
        "legacy setting retained");
    check(
        saved.getAsJsonObject("unknownLegacyExtension").get("keep").getAsBoolean(),
        "unknown NPC fields preserved");
    check(Bukkit.getCommandMap().getCommand("znpcs") != null, "znpcs command registered");
    check(
        !((org.bukkit.entity.Entity) legacy.getBukkitEntity()).isValid(),
        "NPC remains packet-only");
    getLogger().info("PASS " + checks.size() + " checks");
  }
}
