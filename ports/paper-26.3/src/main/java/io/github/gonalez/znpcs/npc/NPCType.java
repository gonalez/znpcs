/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.cache.TypeCache;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.entity.EntityType;

public enum NPCType {
  PLAYER(0.0, new String[0]),
  ARMOR_STAND(0.0, "setSmall", "setArms"),
  CREEPER(-0.15, "setPowered"),
  BAT(-0.5, "setAwake"),
  BLAZE(0.0, new String[0]),
  CAVE_SPIDER(-1.0, new String[0]),
  COW(-0.25, "setAge"),
  CHICKEN(-1.0, "setAge"),
  ENDER_DRAGON(1.5, new String[0]),
  ENDERMAN(0.7, new String[0]),
  ENDERMITE(-1.5, new String[0]),
  GHAST(3.0, new String[0]),
  IRON_GOLEM(0.75, new String[0]),
  GIANT(11.0, new String[0]),
  GUARDIAN(-0.7, new String[0]),
  HORSE(0.0, "setStyle", "setAge", "setColor", "setVariant"),
  LLAMA(0.0, "setAge"),
  MAGMA_CUBE(-1.25, "setSize"),
  MUSHROOM_COW(-0.25, "setAge"),
  OCELOT(-1.0, "setCatType", "setAge"),
  PARROT(-1.5, "setVariant"),
  PIG(-1.0, "setAge"),
  PANDA(-0.6, "setAge", "setMainGene", "setHiddenGene"),
  RABBIT(-1.0, "setRabbitType"),
  POLAR_BEAR(-0.5, new String[0]),
  SHEEP(-0.5, "setAge", "setSheared", "setColor"),
  SILVERFISH(-1.5, new String[0]),
  SNOWMAN(0.0, "setHasPumpkin", "setDerp"),
  SKELETON(0.0, new String[0]),
  SHULKER(0.0, new String[0]),
  SLIME(-1.25, "setSize"),
  SPIDER(-1.0, new String[0]),
  SQUID(-1.0, new String[0]),
  VILLAGER(0.0, "setProfession", "setVillagerType", "setAge"),
  WITCH(0.5, new String[0]),
  WITHER(1.75, new String[0]),
  ZOMBIE(0.0, "setBaby"),
  WOLF(-1.0, "setSitting", "setTamed", "setAngry", "setAge", "setCollarColor"),
  FOX(-1.0, "setFoxType", "setSitting", "setSleeping", "setAge", "setCrouching"),
  BEE(-1.0, "setAnger", "setHasNectar", "setHasStung"),
  TURTLE(-1.0, new String[0]),
  WARDEN(1.0, new String[0]),
  AXOLOTL(-1.0, "setVariant", "setAge"),
  GOAT(-0.5, "setScreamingGoat", "setAge"),
  ALLAY(-0.5, "startDancing", "stopDancing"),
  PIGLIN(-1.0, new String[0]),
  FROG(-0.5, "setAge"),
  CAMEL(0.5, "setAge"),
  HOGLIN(-1.0, new String[0]),
  CAT(-1.0, "setCatType", "setCollarColor");

  private static final String EMPTY_STRING = "";
  private final double holoHeight;
  private final CustomizationLoader customizationLoader;
  private final Constructor<?> constructor;
  private EntityType bukkitEntityType;
  private Object nmsEntityType;

  private NPCType(double holoHeight, String... methods) {
    this.holoHeight = holoHeight;
    String bukkitName =
        switch (this.name()) {
          case "MUSHROOM_COW" -> "MOOSHROOM";
          case "SNOWMAN" -> "SNOW_GOLEM";
          default -> this.name();
        };
    this.bukkitEntityType = EntityType.valueOf(bukkitName);
    this.customizationLoader =
        new CustomizationLoader(this.bukkitEntityType, Arrays.asList(methods));
    this.nmsEntityType =
        org.bukkit.craftbukkit.entity.CraftEntityType.bukkitToMinecraft(this.bukkitEntityType);
    this.constructor =
        findConstructor((net.minecraft.world.entity.EntityType<?>) this.nmsEntityType);
  }

  public double getHoloHeight() {
    return this.holoHeight;
  }

  public Constructor<?> getConstructor() {
    return this.constructor;
  }

  public Object getNmsEntityType() {
    return this.nmsEntityType;
  }

  public CustomizationLoader getCustomizationLoader() {
    return this.customizationLoader;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object[] arrayToPrimitive(String[] values, Method method) {
    Class<?>[] types = method.getParameterTypes();
    if (values.length != types.length)
      throw new IllegalArgumentException("Wrong customization argument count");
    Object[] result = new Object[types.length];
    for (int i = 0; i < types.length; i++) {
      TypeProperty primitive = TypeProperty.forType(types[i]);
      if (primitive != null) result[i] = primitive.getFunction().apply(values[i]);
      else if (types[i].isEnum()) {
        final String value = values[i];
        result[i] =
            Arrays.stream(types[i].getEnumConstants())
                .filter(e -> ((Enum<?>) e).name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown enum value " + value));
      } else if (org.bukkit.Keyed.class.isAssignableFrom(types[i])) {
        org.bukkit.Registry registry = org.bukkit.Bukkit.getRegistry((Class) types[i]);
        org.bukkit.NamespacedKey key =
            org.bukkit.NamespacedKey.fromString(values[i].toLowerCase(java.util.Locale.ROOT));
        result[i] = registry == null || key == null ? null : registry.get(key);
        if (result[i] == null)
          throw new IllegalArgumentException("Unknown registry value " + values[i]);
      } else {
        result[i] = TypeCache.ClassCache.find(values[i], types[i]);
        if (result[i] == null)
          throw new IllegalArgumentException("Unknown customization value " + values[i]);
      }
    }
    return result;
  }

  public void updateCustomization(NPC npc, String name, String[] values) {
    if (!this.customizationLoader.contains(name)) {
      return;
    }
    try {
      Method method = this.customizationLoader.getMethods().get(name);
      method.invoke(npc.getBukkitEntity(), NPCType.arrayToPrimitive(values, method));
      npc.updateMetadata(npc.getViewers());
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException("can't invoke method: " + name, e);
    }
  }

  private static Constructor<?> findConstructor(net.minecraft.world.entity.EntityType<?> type) {
    try {
      return type.getBaseClass()
          .getConstructor(
              net.minecraft.world.entity.EntityType.class, net.minecraft.world.level.Level.class);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
