package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.UnexpectedCallException;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.cache.TypeCache;
import io.github.gonalez.znpcs.utility.Utils;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * List of supported entity types for a {@link NPC}.
 */
public enum NPCType {
    PLAYER(CacheRegistry.ENTITY_PLAYER_CLASS, 0),
    ARMOR_STAND(CacheRegistry.ENTITY_ARMOR_STAND_CLASS, 0, "setSmall", "setArms"),
    CREEPER(CacheRegistry.ENTITY_CREEPER_CLASS, -0.15, "setPowered"),
    BAT(CacheRegistry.ENTITY_BAT_CLASS, -0.5, "setAwake"),
    BLAZE(CacheRegistry.ENTITY_BLAZE_CLASS, 0),
    CAVE_SPIDER(CacheRegistry.ENTITY_CAVE_SPIDER_CLASS, -1),
    COW(CacheRegistry.ENTITY_COW_CLASS, -0.25, "setAge"),
    CHICKEN(CacheRegistry.ENTITY_CHICKEN_CLASS, -1, "setAge"),
    ENDER_DRAGON(CacheRegistry.ENTITY_ENDER_DRAGON_CLASS, 1.5),
    ENDERMAN(CacheRegistry.ENTITY_ENDERMAN_CLASS, 0.7),
    ENDERMITE(CacheRegistry.ENTITY_ENDERMITE_CLASS, -1.5),
    GHAST(CacheRegistry.ENTITY_GHAST_CLASS, 3),
    IRON_GOLEM(CacheRegistry.ENTITY_IRON_GOLEM_CLASS, 0.75),
    GIANT(CacheRegistry.ENTITY_GIANT_ZOMBIE_CLASS, 11),
    GUARDIAN(CacheRegistry.ENTITY_GUARDIAN_CLASS, -0.7),
    HORSE(CacheRegistry.ENTITY_HORSE_CLASS, 0, "setStyle", "setAge", "setColor", "setVariant"),
    LLAMA(CacheRegistry.ENTITY_LLAMA_CLASS, 0, "setAge"),
    MAGMA_CUBE(CacheRegistry.ENTITY_MAGMA_CUBE_CLASS, -1.25, "setSize"),
    MUSHROOM_COW(CacheRegistry.ENTITY_MUSHROOM_COW_CLASS, -0.25, "setAge"),
    OCELOT(CacheRegistry.ENTITY_OCELOT_CLASS, -1, "setCatType", "setAge"),
    PARROT(CacheRegistry.ENTITY_PARROT_CLASS, -1.5, "setVariant"),
    PIG(CacheRegistry.ENTITY_PIG_CLASS, -1, "setAge"),
    PANDA(CacheRegistry.ENTITY_PANDA_CLASS, -0.6, "setAge", "setMainGene", "setHiddenGene"),
    RABBIT(CacheRegistry.ENTITY_RABBIT_CLASS, -1, "setRabbitType"),
    POLAR_BEAR(CacheRegistry.ENTITY_POLAR_BEAR_CLASS, -0.5),
    SHEEP(CacheRegistry.ENTITY_SHEEP_CLASS, -0.5, "setAge", "setSheared", "setColor"),
    SILVERFISH(CacheRegistry.ENTITY_SILVERFISH_CLASS, -1.5),
    SNOWMAN(CacheRegistry.ENTITY_SNOWMAN_CLASS, 0, "setHasPumpkin", "setDerp"),
    SKELETON(CacheRegistry.ENTITY_SKELETON_CLASS, 0),
    SHULKER(CacheRegistry.ENTITY_SHULKER_CLASS, 0),
    SLIME(CacheRegistry.ENTITY_SLIME_CLASS, -1.25, "setSize"),
    SPIDER(CacheRegistry.ENTITY_SPIDER_CLASS, -1),
    SQUID(CacheRegistry.ENTITY_SQUID_CLASS, -1),
    VILLAGER(CacheRegistry.ENTITY_VILLAGER_CLASS, 0, "setProfession", "setVillagerType", "setAge"),
    WITCH(CacheRegistry.ENTITY_WITCH_CLASS, 0.5),
    WITHER(CacheRegistry.ENTITY_WITHER_CLASS, 1.75),
    ZOMBIE(CacheRegistry.ENTITY_ZOMBIE_CLASS, 0, "setBaby"),
    WOLF(CacheRegistry.ENTITY_WOLF_CLASS, -1, "setSitting", "setTamed", "setAngry", "setAge", "setCollarColor"),
    FOX(CacheRegistry.ENTITY_FOX_CLASS, -1, "setFoxType", "setSitting", "setSleeping", "setAge", "setCrouching"),

    // v1.17+
    BEE(CacheRegistry.ENTITY_BEE_CLASS, -1, "setAnger", "setHasNectar", "setHasStung"),

    TURTLE(CacheRegistry.ENTITY_TURTLE, -1),
    WARDEN(CacheRegistry.ENTITY_WARDEN, 1),

    AXOLOTL(CacheRegistry.ENTITY_AXOLOTL_CLASS, -1, "setVariant", "setAge"),
    GOAT(CacheRegistry.ENTITY_GOAT_CLASS, -0.5, "setScreamingGoat", "setAge");

    /** A empty string. */
    private static final String EMPTY_STRING = "";
    /** The hologram height for the entity type. */
    private final double holoHeight;
    /** The entity customization loader. */
    private final CustomizationLoader customizationLoader;
    /** The entity spawn packet. */
    private final Constructor<?> constructor;
    /** The entity bukkit type. */
    private EntityType bukkitEntityType;
    /** The entity nms type. */
    private Object nmsEntityType;

    /**
     * Creates a new {@link NPCType}.
     *
     * @param entityClass The entity class.
     * @param newName The entity name for newer versions.
     * @param holoHeight The hologram height for the entity.
     * @param methods The possible customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            String newName,
            double holoHeight,
            String... methods) {
        this.holoHeight = holoHeight;
        this.customizationLoader = entityClass == null ?
            null : new CustomizationLoader(this.bukkitEntityType =
            EntityType.valueOf(newName.length() > 0 ? newName : name()), Arrays.asList(methods));
        // onLoad #constructor
        if (entityClass == null
            || entityClass.isAssignableFrom(CacheRegistry.ENTITY_PLAYER_CLASS)) { // check if entity constructor can be load
            constructor = null;
            return;
        }

        try {
            if (Utils.versionNewer(14)) {
                nmsEntityType = ((Optional<?>) CacheRegistry.ENTITY_TYPES_A_METHOD.load().invoke(null, bukkitEntityType.getKey().getKey().toLowerCase())).get();
                constructor = entityClass.getConstructor(CacheRegistry.ENTITY_TYPES_CLASS, CacheRegistry.WORLD_CLASS);
            } else {
                constructor = entityClass.getConstructor(CacheRegistry.WORLD_CLASS);
            }
        } catch (ReflectiveOperationException operationException) {
            // can't get entity constructor
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Creates a {@link NPCType}.
     *
     * @param entityClass The entity class.
     * @param holoHeight The hologram height for the entity.
     * @param customization The possible customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            double holoHeight,
            String... customization) {
        this(entityClass, EMPTY_STRING, holoHeight, customization);
    }

    /**
     * Returns the hologram height for the entity.
     */
    public double getHoloHeight() {
        return holoHeight;
    }

    /**
     * Returns the entity spawn packet.
     */
    public Constructor<?> getConstructor() {
        return constructor;
    }

    /**
     * Returns the entity nms type.
     */
    public Object getNmsEntityType() {
        return nmsEntityType;
    }

    /**
     * Returns the entity customization loader.
     */
    public CustomizationLoader getCustomizationLoader() {
        return customizationLoader;
    }

    /**
     * Converts the provided types to the corresponding {@code method} types.
     *
     * @param strings The array of strings to convert.
     * @param method The method.
     * @return The converted array of primitive types.
     */
    public static Object[] arrayToPrimitive(String[] strings, Method method) {
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        Object[] newArray = new Object[methodParameterTypes.length];
        for (int i = 0; i < methodParameterTypes.length; i++) {
            TypeProperty typeProperty = TypeProperty.forType(methodParameterTypes[i]);
            if (typeProperty != null) {
                newArray[i] = typeProperty.getFunction().apply(strings[i]);
            } else {
                // use cache values
                newArray[i] = TypeCache.ClassCache.find(strings[i], methodParameterTypes[i]);
            }
        }
        return newArray;
    }

    /**
     * Changes the npc customization.
     *
     * @param npc The npc to update the customization for.
     * @param name The method name.
     * @param values The method values.
     * @throws IllegalStateException If can't invoke method.
     */
    public void updateCustomization(NPC npc,
                                    String name,
                                    String[] values) {
        if (!customizationLoader.contains(name)) {
            // method not found for npc type
            return;
        }
        try {
            Method method = customizationLoader.getMethods().get(name);
            method.invoke(npc.getBukkitEntity(), arrayToPrimitive(values, method));
            // update new customization for the npc
            npc.updateMetadata(npc.getViewers());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("can't invoke method: " + name, e);
        }
    }
}
