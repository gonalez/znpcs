package ak.znetwork.znpcservers.npc.enums;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.assignation.CustomizationProcessor;
import ak.znetwork.znpcservers.npc.assignation.TypeProperty;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.entity.EntityType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import lombok.Getter;

import static ak.znetwork.znpcservers.cache.impl.ClassCacheImpl.ClassCache;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public enum NPCType {

    PLAYER(ClassTypes.ENTITY_PLAYER_CLASS, 0),
    ARMOR_STAND(ClassTypes.ENTITY_ARMOR_STAND_CLASS, 0, "setSmall", "setArms"),
    CREEPER(ClassTypes.ENTITY_CREEPER_CLASS, -0.15, "setPowered"),
    BAT(ClassTypes.ENTITY_BAT_CLASS, -0.5, "setAwake"),
    BLAZE(ClassTypes.ENTITY_BLAZE_CLASS, 0),
    CAVE_SPIDER(ClassTypes.ENTITY_CAVE_SPIDER_CLASS, -1),
    COW(ClassTypes.ENTITY_COW_CLASS, -0.25, "setAge"),
    CHICKEN(ClassTypes.ENTITY_CHICKEN_CLASS, -1, "setAge"),
    ENDER_DRAGON(ClassTypes.ENTITY_ENDER_DRAGON_CLASS, 1.5),
    ENDERMAN(ClassTypes.ENTITY_ENDERMAN_CLASS, 0.7),
    ENDERMITE(ClassTypes.ENTITY_ENDERMITE_CLASS, -1.5),
    GHAST(ClassTypes.ENTITY_GHAST_CLASS, 3),
    IRON_GOLEM(ClassTypes.ENTITY_IRON_GOLEM_CLASS, 0.75),
    GIANT(ClassTypes.ENTITY_GIANT_ZOMBIE_CLASS, 11),
    GUARDIAN(ClassTypes.ENTITY_GUARDIAN_CLASS, -0.7),
    HORSE(ClassTypes.ENTITY_HORSE_CLASS, 0, "setStyle", "setAge", "setColor", "setVariant"),
    LLAMA(ClassTypes.ENTITY_LLAMA_CLASS, 0, "setAge"),
    MAGMA_CUBE(ClassTypes.ENTITY_MAGMA_CUBE_CLASS, -1.25, "setSize"),
    MUSHROOM_COW(ClassTypes.ENTITY_MUSHROOM_COW_CLASS, "MOOSHROOM", -1, -0.25, "setAge"),
    OCELOT(ClassTypes.ENTITY_OCELOT_CLASS, -1, "setCatType", "setAge"),
    PARROT(ClassTypes.ENTITY_PARROT_CLASS, -1.5, "setVariant"),
    PIG(ClassTypes.ENTITY_PIG_CLASS, -1, "setAge"),
    PANDA(ClassTypes.ENTITY_PANDA_CLASS, -0.6, "setAge", "setMainGene", "setHiddenGene"),
    RABBIT(ClassTypes.ENTITY_RABBIT_CLASS, -1, "setRabbitType"),
    PIG_ZOMBIE(ClassTypes.ENTITY_PIG_ZOMBIE_CLASS, "ZOMBIFIED_PIGLIN", -1, 0),
    POLAR_BEAR(ClassTypes.ENTITY_POLAR_BEAR_CLASS, -0.5),
    SHEEP(ClassTypes.ENTITY_SHEEP_CLASS, -0.5, "setAge", "setSheared", "setColor"),
    SILVERFISH(ClassTypes.ENTITY_SILVERFISH_CLASS, -1.5),
    SNOWMAN(ClassTypes.ENTITY_SNOWMAN_CLASS, "SNOW_GOLEM", -1, 0, "setHasPumpkin", "setDerp"),
    SKELETON(ClassTypes.ENTITY_SKELETON_CLASS, 0),
    SHULKER(ClassTypes.ENTITY_SHULKER_CLASS, 0),
    SLIME(ClassTypes.ENTITY_SLIME_CLASS, -1.25, "setSize"),
    SPIDER(ClassTypes.ENTITY_SPIDER_CLASS, -1),
    SQUID(ClassTypes.ENTITY_SQUID_CLASS, -1),
    VILLAGER(ClassTypes.ENTITY_VILLAGER_CLASS, 0, "setProfession", "setVillagerType", "setAge"),
    WITCH(ClassTypes.ENTITY_WITCH_CLASS, 0.5),
    WITHER(ClassTypes.ENTITY_WITHER_CLASS, 1.75),
    ZOMBIE(ClassTypes.ENTITY_ZOMBIE_CLASS, 0, "setBaby"),
    WOLF(ClassTypes.ENTITY_WOLF_CLASS, -1, "setSitting", "setTamed", "setAngry", "setAge", "setCollarColor"),
    ENDER_CRYSTAL(ClassTypes.ENTITY_ENDER_CRYSTAL_CLASS, 51, 0);

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * The default entity id.
     */
    private static final int DEFAULT_ID = -1;

    /**
     * The entity type class.
     */
    private final Class<?> entityClass;

    /**
     * Represents the new name of the entity type in newer versions.
     */
    private final String newName;

    /**
     * The bukkit entity id.
     */
    private final int id;

    /**
     * The entity hologram height.
     */
    private final double holoHeight;

    /**
     * The entity customization processor.
     */
    private final CustomizationProcessor customizationProcessor;

    /**
     * The bukkit entity type.
     */
    private Object entityType;

    /**
     * The entity spawn packet.
     */
    private Constructor<?> constructor = null;

    /**
     * Creates a new Entity type.
     *
     * @param entityClass   The entity class.
     * @param newName       The entity name for newer versions.
     * @param id            The bukkit entity ID;
     * @param holoHeight    The hologram height for the entity.
     * @param methods       The customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            String newName,
            int id,
            double holoHeight,
            String... methods) {
        this.entityClass = entityClass;
        this.newName = newName;
        this.id = id;
        this.holoHeight = holoHeight;
        this.customizationProcessor = entityClass == null ? null : new CustomizationProcessor(EntityType.valueOf(name()).getEntityClass(), Arrays.asList(methods));
        load();
    }

    /**
     * Creates a new Entity type.
     *
     * @param entityClass   The entity class.
     * @param id            The bukkit entity ID;
     * @param holoHeight    The hologram height for the entity.
     * @param customization The customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            int id,
            double holoHeight,
            String... customization) {
        this(entityClass, EMPTY_STRING, id, holoHeight, customization);
    }

    /**
     * Creates a new Entity type.
     *
     * @param entityClass   The entity class.
     * @param holoHeight    The hologram height for the entity.
     * @param customization The customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            double holoHeight,
            String... customization) {
        this(entityClass, EMPTY_STRING, DEFAULT_ID, holoHeight, customization);
    }

    /**
     * Loads the npc type.
     */
    public void load() {
        if (entityClass == null ||
                entityClass.isAssignableFrom(ClassTypes.ENTITY_PLAYER_CLASS)) {
            // The entity type is not available for the current version so don't load it
            return;
        }
        try {
            if (Utils.versionNewer(14)) {
                try {
                    entityType = ClassTypes.ENTITY_TYPES_CLASS.getField(name()).get(null);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    try {
                        entityType = ClassTypes.ENTITY_TYPES_CLASS.getField(newName).get(null);
                    } catch (IllegalAccessException | NoSuchFieldException operationException) {
                        throw new AssertionError(operationException);
                    }
                } finally {
                    if (entityType != null) {
                        constructor = entityClass.getConstructor(entityType.getClass(), ClassTypes.WORLD_CLASS);
                    }
                }
            } else {
                constructor = entityClass.getConstructor(ClassTypes.WORLD_CLASS);
            }
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new AssertionError(noSuchMethodException);
        }
    }

    /**
     * Converts the provided types to the corresponding method types.
     *
     * @param strings The array of strings to convert.
     * @param method  The customization method.
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
                // Use cache values
                newArray[i] = ClassCache.find(strings[i], methodParameterTypes[i]);
            }
        }
        return newArray;
    }

    /**
     * Change/updates the npc customization.
     *
     * @param znpc   The npc.
     * @param name   The method name.
     * @param values The method values.
     */
    public void updateCustomization(ZNPC znpc, String name, String[] values) throws InvocationTargetException, IllegalAccessException {
        if (!getCustomizationProcessor().contains(name)) {
            // Method not found
            return;
        }

        Method method = getCustomizationProcessor().getMethods().get(name);
        method.invoke(znpc.getBukkitEntity(),
                arrayToPrimitive(values, method));
        // Update the new customization for the npc
        znpc.updateMetaData();
    }
}
