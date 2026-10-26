/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import io.github.gonalez.znpcs.npc.*;
import java.util.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/** Paper 26.3 packets; NPCs are client-side entities and are never added to the world. */
public final class Packet263 implements Packet {
  @Override
  public String version() {
    return "26.3";
  }

  @Override
  public Object getPlayerPacket(Object world, GameProfile profile) {
    ServerPlayer player =
        new ServerPlayer(
            ((CraftServer) Bukkit.getServer()).getServer(),
            (ServerLevel) world,
            profile,
            ClientInformation.createDefault());
    player.isRealPlayer = false;
    return player;
  }

  public static ClientboundPlayerInfoUpdatePacket playerInfo(ServerPlayer player) {
    // A packet-only player has no connection. Do not use the constructor that reads latency.
    var entry =
        new ClientboundPlayerInfoUpdatePacket.Entry(
            player.getUUID(),
            player.getGameProfile(),
            false,
            0,
            GameType.SURVIVAL,
            null,
            true,
            0,
            null);
    return new ClientboundPlayerInfoUpdatePacket(
        EnumSet.of(
            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_HAT),
        entry);
  }

  public static ClientboundPlayerInfoRemovePacket removePlayer(UUID id) {
    return new ClientboundPlayerInfoRemovePacket(List.of(id));
  }

  @Override
  public Object getSpawnPacket(int id, UUID uuid, Location l, EntityType type, Object handle) {
    Entity e = (Entity) handle;
    return new ClientboundAddEntityPacket(
        id,
        uuid,
        l.getX(),
        l.getY(),
        l.getZ(),
        l.getPitch(),
        l.getYaw(),
        e.getType(),
        0,
        Vec3.ZERO,
        l.getYaw());
  }

  @Override
  public Object getHologramSpawnPacket(Object handle) {
    Entity e = (Entity) handle;
    return getSpawnPacket(
        e.getId(),
        e.getUUID(),
        e.getBukkitEntity().getLocation(),
        e.getBukkitEntity().getType(),
        e);
  }

  @Override
  public Object getDestroyPacket(int id) {
    return new ClientboundRemoveEntitiesPacket(id);
  }

  @Override
  public Object getTeleportPacket(int id, Object handle) {
    return new ClientboundTeleportEntityPacket(
        id, PositionMoveRotation.of((Entity) handle), Set.of(), false);
  }

  @Override
  public Object getDatawatcherObject(Object handle) {
    return ((Entity) handle).getEntityData();
  }

  @Override
  public Object getMetadataPacket(int id, Object handle) {
    var values = ((Entity) handle).getEntityData().getNonDefaultValues();
    return new ClientboundSetEntityDataPacket(id, values == null ? List.of() : values);
  }

  @Override
  public Object getTabRemovePacket(Object handle) {
    // Retain the skin profile, but exclude the NPC from the player list.
    return ClientboundPlayerInfoUpdatePacket.updateListed(((Entity) handle).getUUID(), false);
  }

  @Override
  public Object getClickType(Object packet) {
    return packet instanceof ServerboundAttackPacket ? "ATTACK" : "INTERACT";
  }

  private static EquipmentSlot slot(ItemSlot s) {
    return switch (s) {
      case HAND -> EquipmentSlot.MAINHAND;
      case OFFHAND -> EquipmentSlot.OFFHAND;
      case BOOTS -> EquipmentSlot.FEET;
      case LEGGINGS -> EquipmentSlot.LEGS;
      case CHESTPLATE -> EquipmentSlot.CHEST;
      case HELMET -> EquipmentSlot.HEAD;
    };
  }

  @Override
  public Object getItemSlot(int index) {
    return switch (index) {
      case 0 -> EquipmentSlot.MAINHAND;
      case 1 -> EquipmentSlot.OFFHAND;
      case 2 -> EquipmentSlot.FEET;
      case 3 -> EquipmentSlot.LEGS;
      case 4 -> EquipmentSlot.CHEST;
      case 5 -> EquipmentSlot.HEAD;
      default -> throw new IllegalArgumentException("Unknown equipment slot " + index);
    };
  }

  @Override
  public Object convertItemStack(int id, ItemSlot s, ItemStack stack) {
    return new ClientboundSetEquipmentPacket(
        id, List.of(Pair.of(slot(s), CraftItemStack.asNMSCopy(stack))));
  }

  @Override
  public ImmutableList<Object> getEquipPackets(NPC npc) {
    ImmutableList.Builder<Object> b = ImmutableList.builder();
    npc.getNpcPojo()
        .getNpcEquip()
        .forEach((s, item) -> b.add(convertItemStack(npc.getEntityID(), s, item)));
    return b.build();
  }

  @Override
  public ImmutableList<Object> updateScoreboard(NPC npc) {
    PlayerTeam team = new PlayerTeam(new Scoreboard(), "znpc_" + npc.getEntityID());
    team.setNameTagVisibility(Team.Visibility.NEVER);
    team.setColor(Optional.of(TeamColor.DARK_GRAY));
    team.getPlayers()
        .add(
            npc.getNpcPojo().getNpcType() == NPCType.PLAYER
                ? npc.getGameProfile().name()
                : npc.getUUID().toString());
    if (FunctionFactory.isTrue(npc, "glow")) updateGlowPacket(npc, team);
    return ImmutableList.of(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true));
  }

  public static ClientboundSetPlayerTeamPacket removeTeam(NPC npc) {
    return ClientboundSetPlayerTeamPacket.createRemovePacket(
        new PlayerTeam(new Scoreboard(), "znpc_" + npc.getEntityID()));
  }

  @Override
  public void updateGlowPacket(NPC npc, Object team) {
    if (npc.getGlowColor() instanceof TeamColor c) ((PlayerTeam) team).setColor(Optional.of(c));
  }

  @Override
  public boolean allowGlowColor() {
    return true;
  }
}
