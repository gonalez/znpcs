/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.UnexpectedCallException;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.configuration.ConfigurationValue;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.npc.line.LineReplacer;
import io.github.gonalez.znpcs.npc.packet.Packet263;
import io.github.gonalez.znpcs.npc.packet.PacketCache;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.Utils;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;

public class NPC {
  private static final ConcurrentMap<Integer, NPC> NPC_MAP = new ConcurrentHashMap<Integer, NPC>();
  private static final String PROFILE_TEXTURES = "textures";
  private static final String START_PREFIX = "[ZNPC] ";
  private final Set<ZUser> viewers = Sets.newConcurrentHashSet();
  private final PacketCache packets = new PacketCache();
  private final NPCModel npcPojo;
  private final String npcName;
  private final NPCSkin npcSkin;
  private long lastMove = -1L;
  private int entityID;
  private Object glowColor;
  private Object tabConstructor;
  private Object updateTabConstructor;
  private Object nmsEntity;
  private Object bukkitEntity;
  private UUID uuid;
  private GameProfile gameProfile;
  private NPCPath.PathInitializer npcPath;
  private final List<NPC> holograms = new ArrayList<NPC>();
  private boolean shouldLoad;

  public NPC(NPCModel npcModel, boolean load) {
    this.npcPojo = npcModel;
    this.npcName = NamingType.DEFAULT.resolve(this);
    this.npcSkin = NPCSkin.forValues(npcModel.getSkin(), npcModel.getSignature());
    this.shouldLoad = load;
  }

  public NPC(NPCModel npcModel) {
    this(npcModel, false);
  }

  public void onLoad(boolean registry) {
    this.shouldLoad = registry;
    if (registry && NPC_MAP.containsKey(this.getNpcPojo().getId())) {
      throw new IllegalStateException(
          "npc with id " + this.getNpcPojo().getId() + " already exists.");
    }
    this.gameProfile =
        new GameProfile(
            UUID.randomUUID(),
            START_PREFIX + this.npcName,
            profileProperties(this.npcPojo.getSkin(), this.npcPojo.getSignature()));
    this.changeType(this.npcPojo.getNpcType());
    this.updateProfile(this.gameProfile.properties());
    this.setLocation(this.getNpcPojo().getLocation().bukkitLocation(), false);
    if (this.npcPojo.getPathName() != null) {
      this.setPath(NPCPath.AbstractTypeWriter.find(this.npcPojo.getPathName()));
    }
    this.npcPojo
        .getCustomizationMap()
        .forEach(
            (key, value) ->
                this.npcPojo
                    .getNpcType()
                    .updateCustomization(this, (String) key, (String[]) value));
    if (registry) {
      NPC_MAP.put(this.getNpcPojo().getId(), this);
    }
  }

  private void destroyHologram() {
    for (NPC npc : this.holograms) {
      npc.deleteViewers();
    }
    this.holograms.clear();
  }

  private Location getFixedLocationForHologram(Location location) {
    return location
        .clone()
        .subtract(0.0, 0.2, 0.0)
        .add(0.0, this.getNpcPojo().getHologramHeight(), 0.0);
  }

  public void createHologram() {
    this.destroyHologram();
    if (!this.shouldLoad || FunctionFactory.isTrue(this, "holo")) {
      return;
    }
    List<String> lines = this.getNpcPojo().getHologramLines();
    Location location =
        this.getFixedLocationForHologram(this.getNpcPojo().getLocation().bukkitLocation());
    location.add(0.0, this.getNpcPojo().getNpcType().getHoloHeight(), 0.0);
    location.subtract(
        0.0,
        ((Double) Configuration.CONFIGURATION.getValue(ConfigurationValue.LINE_SPACING))
            .doubleValue(),
        0.0);
    for (String line : lines) {
      location.add(
          0.0,
          ((Double) Configuration.CONFIGURATION.getValue(ConfigurationValue.LINE_SPACING))
              .doubleValue(),
          0.0);
      NPC npc =
          new NPC(
              new NPCModel()
                  .withLocation(new ZLocation(location.clone()))
                  .withNpcType(NPCType.ARMOR_STAND)
                  .withHologramLines(java.util.List.of(line)));
      npc.onLoad(false);
      this.holograms.add(npc);
      this.viewers.forEach(npc::spawn);
    }
  }

  private void updateLine(ZUser user) throws InvocationTargetException, IllegalAccessException {
    String line = this.getNpcPojo().getHologramLines().get(0);
    ((Entity) this.nmsEntity)
        .setCustomName(CraftChatMessage.fromStringOrNull(LineReplacer.makeAll(user, line)));
  }

  public NPCModel getNpcPojo() {
    return this.npcPojo;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public int getEntityID() {
    return this.entityID;
  }

  public Object getBukkitEntity() {
    return this.bukkitEntity;
  }

  public Object getNmsEntity() {
    return this.nmsEntity;
  }

  public Object getGlowColor() {
    return this.glowColor;
  }

  public GameProfile getGameProfile() {
    return this.gameProfile;
  }

  public NPCPath.PathInitializer getNpcPath() {
    return this.npcPath;
  }

  public Set<ZUser> getViewers() {
    return this.viewers;
  }

  public PacketCache getPackets() {
    return this.packets;
  }

  public void setGlowColor(Object glowColor) {
    this.glowColor = glowColor;
  }

  public void setLocation(Location location, boolean updateTime) {
    try {
      if (this.npcPath == null) {
        this.lookAt(null, location, true);
        if (updateTime) {
          this.lastMove = System.nanoTime();
        }
        location =
            new Location(
                location.getWorld(),
                ConfigurationConstants.CENTER_NPC
                    ? (double) location.getBlockX() + 0.5
                    : location.getX(),
                location.getY(),
                ConfigurationConstants.CENTER_NPC
                    ? (double) location.getBlockZ() + 0.5
                    : location.getZ(),
                location.getYaw(),
                location.getPitch());
        this.npcPojo.setLocation(new ZLocation(location));
      }
      ((Entity) this.nmsEntity)
          .snapTo(
              location.getX(),
              location.getY(),
              location.getZ(),
              location.getYaw(),
              location.getPitch());
      Object npcTeleportPacket =
          this.getPackets().getProxyInstance().getTeleportPacket(this.entityID, this.nmsEntity);
      this.viewers.forEach(player -> Utils.sendPackets(player, npcTeleportPacket));
      this.createHologram();
    } catch (Exception operationException) {
      throw new UnexpectedCallException(operationException);
    }
  }

  public void changeSkin(NPCSkin skinFetch) {
    changeSkin(skinFetch, true);
  }

  public void setSecondLayerSkin() {
    if (this.nmsEntity instanceof ServerPlayer player) {
      player.getEntityData().set(Avatar.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 127);
    }
  }

  public synchronized void changeType(NPCType npcType) {
    this.deleteViewers();
    try {
      ServerLevel level = ((CraftWorld) this.getLocation().getWorld()).getHandle();
      this.nmsEntity =
          npcType == NPCType.PLAYER
              ? this.packets.getProxyInstance().getPlayerPacket(level, this.gameProfile)
              : ((net.minecraft.world.entity.EntityType<?>) npcType.getNmsEntityType())
                  .create(level, EntitySpawnReason.COMMAND);
      if (this.nmsEntity == null)
        throw new IllegalArgumentException("Cannot create NPC type " + npcType);
      Entity entity = (Entity) this.nmsEntity;
      entity.setNoGravity(true);
      if (entity instanceof net.minecraft.world.entity.Mob mob) mob.setNoAi(true);
      if (!this.shouldLoad && entity instanceof ArmorStand armorStand) {
        armorStand.setMarker(true);
        armorStand.setInvisible(true);
        armorStand.setCustomNameVisible(true);
      }
      this.bukkitEntity = entity.getBukkitEntity();
      this.uuid = entity.getUUID();
      this.entityID = entity.getId();
      if (npcType == NPCType.PLAYER) this.setSecondLayerSkin();
      this.npcPojo.setNpcType(npcType);
      this.packets.flushCache();
      this.setLocation(this.getLocation(), false);
      FunctionFactory.findFunctionsForNpc(this).forEach(function -> function.resolve(this));
      this.getPackets().getProxyInstance().update(this.packets);
    } catch (ReflectiveOperationException e) {
      throw new UnexpectedCallException(e);
    }
  }

  private static Object createPlayerInfoUpdatePacket(NPC npc) throws Exception {
    return Packet263.playerInfo((ServerPlayer) npc.getNmsEntity());
  }

  public static List<Field> getFieldsByRawType(Class<?> clazz, Class<?> rawType) {
    ArrayList<Field> matchingFields = new ArrayList<Field>();
    for (Field field : clazz.getDeclaredFields()) {
      if (!rawType.isAssignableFrom(field.getType())) continue;
      field.setAccessible(true);
      matchingFields.add(field);
    }
    return matchingFields;
  }

  public synchronized void spawn(ZUser user) {
    if (this.viewers.contains(user)) {
      return;
    }
    try {
      boolean npcIsPlayer;
      this.viewers.add(user);
      boolean bl = npcIsPlayer = this.npcPojo.getNpcType() == NPCType.PLAYER;
      if (FunctionFactory.isTrue(this, "glow") || npcIsPlayer) {
        ImmutableList<Object> scoreboardPackets =
            this.packets.getProxyInstance().updateScoreboard(this);
        scoreboardPackets.forEach(p -> Utils.sendPackets(user, p));
      }
      if (npcIsPlayer) {
        if (FunctionFactory.isTrue(this, "mirror")) {
          this.updateProfile(user.getGameProfile().properties());
        } else {
          this.updateProfile(this.gameProfile.properties());
        }
        Utils.sendPackets(user, Packet263.playerInfo((ServerPlayer) this.nmsEntity));
      }
      Utils.sendPackets(
          user,
          this.packets
              .getProxyInstance()
              .getSpawnPacket(
                  this.entityID,
                  this.uuid,
                  this.getLocation(),
                  ((org.bukkit.entity.Entity) this.bukkitEntity).getType(),
                  this.nmsEntity));
      if (!this.shouldLoad && this.nmsEntity instanceof ArmorStand) this.updateLine(user);
      this.updateMetadata(Collections.singleton(user));
      this.sendEquipPackets(user);
      this.updateAttributes(user);
      this.lookAt(user, this.getLocation(), true);
      if (npcIsPlayer) {
        Object removeTabPacket = this.packets.getProxyInstance().getTabRemovePacket(this.nmsEntity);
        ServersNPC.SCHEDULER.scheduleSyncDelayedTask(
            () -> Utils.sendPackets(user, removeTabPacket, this.updateTabConstructor), 60);
      } else if (this.getNpcPojo().getNpcType() == NPCType.ARMOR_STAND) {
        this.updateLine(user);
      }
      for (NPC npc : this.holograms) {
        npc.spawn(user);
      }
    } catch (Throwable operationException) {
      this.delete(user);
      throw new UnexpectedCallException(operationException);
    }
  }

  public void delete(ZUser user) {
    if (!this.viewers.contains(user)) {
      return;
    }
    this.viewers.remove(user);
    this.handleDelete(user);
    for (NPC npc : this.holograms) {
      npc.delete(user);
    }
  }

  private void handleDelete(ZUser user) {
    try {
      Utils.sendPackets(user, this.packets.getProxyInstance().getDestroyPacket(this.entityID));
      if (this.npcPojo.getNpcType() == NPCType.PLAYER) {
        Utils.sendPackets(user, Packet263.removePlayer(this.uuid));
      }
      if (this.npcPojo.getNpcType() == NPCType.PLAYER || FunctionFactory.isTrue(this, "glow")) {
        Utils.sendPackets(user, Packet263.removeTeam(this));
      }
    } catch (ReflectiveOperationException e) {
      throw new UnexpectedCallException(e);
    }
  }

  public void lookAt(ZUser player, Location location, boolean rotation) {
    long lastMoveNanos = System.nanoTime() - this.lastMove;
    if (this.lastMove > 1L && lastMoveNanos < 1000000000L) {
      return;
    }
    Location direction =
        rotation
            ? location
            : this.npcPojo
                .getLocation()
                .bukkitLocation()
                .clone()
                .setDirection(
                    location
                        .clone()
                        .subtract(this.npcPojo.getLocation().bukkitLocation().clone())
                        .toVector());
    try {
      Object lookPacket =
          new net.minecraft.network.protocol.game.ClientboundMoveEntityPacket.Rot(
              this.entityID,
              (byte) (direction.getYaw() * 256.0f / 360.0f),
              (byte) (direction.getPitch() * 256.0f / 360.0f),
              true);
      Object headRotationPacket =
          new net.minecraft.network.protocol.game.ClientboundRotateHeadPacket(
              (Entity) this.nmsEntity, (byte) (direction.getYaw() * 256.0f / 360.0f));
      if (player != null) {
        Utils.sendPackets(player, lookPacket, headRotationPacket);
      } else {
        this.viewers.forEach(players -> Utils.sendPackets(players, headRotationPacket));
      }
    } catch (Exception operationException) {
      throw new UnexpectedCallException(operationException);
    }
  }

  public void deleteViewers() {
    Iterator<ZUser> iterator = this.viewers.iterator();
    while (iterator.hasNext()) {
      ZUser user = iterator.next();
      this.delete(user);
      iterator.remove();
    }
  }

  protected void updateMetadata(Iterable<ZUser> users) {
    try {
      Object metaData =
          this.packets.getProxyInstance().getMetadataPacket(this.entityID, this.nmsEntity);
      for (ZUser user : users) {
        Utils.sendPackets(user, metaData);
      }
    } catch (Exception operationException) {
      throw new UnexpectedCallException(operationException);
    }
  }

  public void updateProfile(PropertyMap propertyMap) {
    if (this.nmsEntity instanceof ServerPlayer player) {
      player.gameProfile =
          new GameProfile(this.gameProfile.id(), this.gameProfile.name(), propertyMap);
    }
  }

  public void sendEquipPackets(ZUser zUser) {
    if (this.npcPojo.getNpcEquip().isEmpty()) {
      return;
    }
    try {
      ImmutableList<Object> equipPackets = this.packets.getProxyInstance().getEquipPackets(this);
      equipPackets.forEach(o -> Utils.sendPackets(zUser, o));
    } catch (Exception operationException) {
      throw new UnexpectedCallException(operationException.getCause());
    }
  }

  public void setPath(NPCPath.AbstractTypeWriter typeWriter) {
    if (typeWriter == null) {
      this.npcPath = null;
      this.npcPojo.setPathName("none");
    } else {
      this.npcPath = typeWriter.getPath(this);
      this.npcPojo.setPathName(typeWriter.getName());
    }
  }

  public void tryStartConversation(Player player) {
    ConversationModel conversation = this.npcPojo.getConversation();
    if (conversation == null) {
      throw new IllegalStateException("can't find conversation");
    }
    conversation.startConversation(this, player);
  }

  public Location getLocation() {
    return this.npcPath != null
        ? this.npcPath.getLocation().bukkitLocation()
        : this.npcPojo.getLocation().bukkitLocation();
  }

  public static NPC find(int id) {
    return (NPC) NPC_MAP.get(id);
  }

  public static void unregister(int id) {
    NPC npc = NPC.find(id);
    if (npc == null) {
      throw new IllegalStateException("can't find npc with id " + id);
    }
    NPC_MAP.remove(id);
    npc.deleteViewers();
  }

  public static Collection<NPC> all() {
    return NPC_MAP.values();
  }

  public void onUpdate(ZUser zUser) {
    for (NPC hologramLine : this.holograms) {
      try {
        hologramLine.updateLine(zUser);
        hologramLine.updateMetadata(Collections.singleton(zUser));
      } catch (Exception operationException) {
        throw new UnexpectedCallException(operationException);
      }
    }
  }

  /** Public 5.3 skin API; rekeys the packet-only player so clients discard cached skin data. */
  public void changeSkin(NPCSkin skinFetch, boolean refresh) {
    if (!org.bukkit.Bukkit.isPrimaryThread()) {
      org.bukkit.Bukkit.getScheduler()
          .runTask(ServersNPC.getPlugin(ServersNPC.class), () -> changeSkin(skinFetch, refresh));
      return;
    }
    this.npcPojo.setSkin(skinFetch.getTexture());
    this.npcPojo.setSignature(skinFetch.getSignature());
    var audience = java.util.List.copyOf(this.viewers);
    if (refresh) this.deleteViewers();
    UUID profileId = UUID.randomUUID();
    this.gameProfile =
        newGameProfileWithProperties(START_PREFIX + this.npcName, profileId, skinFetch, null);
    if (this.nmsEntity instanceof ServerPlayer player) {
      player.setUUID(profileId);
      this.uuid = profileId;
      player.gameProfile = this.gameProfile;
      this.packets.flushCache();
    }
    if (refresh) audience.forEach(this::spawn);
  }

  public GameProfile newGameProfileWithProperties(
      String name, UUID id, NPCSkin skin, Property property) {
    PropertyMap properties =
        property == null
            ? (skin == null
                ? PropertyMap.EMPTY
                : profileProperties(skin.getTexture(), skin.getSignature()))
            : new PropertyMap(ImmutableMultimap.of("textures", property));
    return new GameProfile(id, name, properties);
  }

  public void updateProfile(GameProfile profile) {
    if (this.nmsEntity instanceof ServerPlayer player) player.gameProfile = profile;
  }

  public synchronized void respawn() {
    var audience = java.util.List.copyOf(this.viewers);
    deleteViewers();
    createHologram();
    audience.forEach(this::spawn);
  }

  public java.util.List<Object> attributePackets() {
    java.util.List<net.minecraft.world.entity.ai.attributes.AttributeInstance> attributes =
        new java.util.ArrayList<>();
    this.npcPojo
        .getBukkitAttributes()
        .forEach(
            (name, value) -> {
              org.bukkit.attribute.Attribute attribute =
                  org.bukkit.attribute.Attribute.valueOf(name);
              var instance =
                  new net.minecraft.world.entity.ai.attributes.AttributeInstance(
                      org.bukkit.craftbukkit.attribute.CraftAttribute.bukkitToMinecraftHolder(
                          attribute),
                      changed -> {});
              instance.setBaseValue(value);
              attributes.add(instance);
            });
    return attributes.isEmpty()
        ? java.util.List.of()
        : java.util.List.of(
            new net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket(
                this.entityID, attributes));
  }

  void updateAttributes(ZUser user) {
    for (Object packet : attributePackets()) Utils.sendPackets(user, packet);
  }

  private static PropertyMap profileProperties(String texture, String signature) {
    if (texture == null || texture.isBlank()) return PropertyMap.EMPTY;
    Property property =
        signature == null || signature.isBlank()
            ? new Property("textures", texture)
            : new Property("textures", texture, signature);
    return new PropertyMap(ImmutableMultimap.of("textures", property));
  }
}
