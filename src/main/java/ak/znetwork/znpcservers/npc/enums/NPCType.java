package ak.znetwork.znpcservers.npc.enums;

import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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
    
    PLAYER(ClassTypes.ENTITY_PLAYER_CLASS, -1, 0),
    ARMOR_STAND(ClassTypes.ENTITY_ARMOR_STAND_CLASS, -1, 0),
    CREEPER(ClassTypes.ENTITY_CREEPER_CLASS, -1, -0.15, "setPowered"),
    BAT(ClassTypes.ENTITY_BAT_CLASS, -1, -0.5, "setAsleep"),
    BLAZE(ClassTypes.ENTITY_BLAZE_CLASS, -1, 0, Utils.versionNewer(8) ? "p" : Utils.versionNewer(15) ? "l" : "eL"),
    CAVE_SPIDER(ClassTypes.ENTITY_CAVE_SPIDER_CLASS, -1, -1),
    COW(ClassTypes.ENTITY_COW_CLASS, -1, -0.25, "setAge"),
    CHICKEN(ClassTypes.ENTITY_CHICKEN_CLASS, -1, -1, "setAge"),
    ENDER_DRAGON(ClassTypes.ENTITY_ENDER_DRAGON_CLASS, -1, 1.5),
    ENDERMAN(ClassTypes.ENTITY_ENDERMAN_CLASS, -1, 0.7),
    ENDERMITE(ClassTypes.ENTITY_ENDERMITE_CLASS, -1, -1.5),
    GHAST(ClassTypes.ENTITY_GHAST_CLASS, -1, 3),
    IRON_GOLEM(ClassTypes.ENTITY_IRON_GOLEM_CLASS, -1, 0.75),
    GIANT(ClassTypes.ENTITY_GIANT_ZOMBIE_CLASS, -1, 11),
    GUARDIAN(ClassTypes.ENTITY_GUARDIAN_CLASS, -1, -0.7),
    HORSE(ClassTypes.ENTITY_HORSE_CLASS, -1, 0, "setVariant", "setAge"),
    LLAMA(ClassTypes.ENTITY_LLAMA_CLASS, -1, 0, "setAge"),
    MAGMA_CUBE(ClassTypes.ENTITY_MAGMA_CUBE_CLASS, -1, -1.25, "setSize"),
    MOOSHROOM(ClassTypes.ENTITY_MUSHROOM_COW_CLASS, -1, -0.25, "setAge"),
    OCELOT(ClassTypes.ENTITY_OCELOT_CLASS, -1, -1, "setCatType", "setAge"),
    PARROT(ClassTypes.ENTITY_PARROT_CLASS, -1, -1.5, "setVariant"),
    PIG(ClassTypes.ENTITY_PIG_CLASS, -1, -1, "setAge"),
    PANDA(ClassTypes.ENTITY_PANDA_CLASS, -1, -0.6, "setAge", "s"),
    RABBIT(ClassTypes.ENTITY_RABBIT_CLASS, -1, -1, "setRabbitType"),
    ZOMBIFIED_PIGLIN(ClassTypes.ENTITY_PIG_ZOMBIE_CLASS, "ZOMBIE_PIGMAN", -1, 0),
    POLAR_BEAR(ClassTypes.ENTITY_POLAR_BEAR_CLASS, -1, -0.5),
    SHEEP(ClassTypes.ENTITY_SHEEP_CLASS, -1, -0.5, "setAge", "setSheared", "setColor"),
    SILVERFISH(ClassTypes.ENTITY_SILVERFISH_CLASS, -1, -1.5),
    SNOWMAN(ClassTypes.ENTITY_SNOWMAN_CLASS, "SNOW_GOLEM", -1, 0, "setHasPumpkin"),
    SKELETON(ClassTypes.ENTITY_SKELETON_CLASS, -1, 0),
    SHULKER(ClassTypes.ENTITY_SHULKER_CLASS, -1, 0),
    SLIME(ClassTypes.ENTITY_SLIME_CLASS, -1, -1.25, "setSize"),
    SPIDER(ClassTypes.ENTITY_SPIDER_CLASS, -1, -1),
    SQUID(ClassTypes.ENTITY_SQUID_CLASS, -1, -1),
    VILLAGER(ClassTypes.ENTITY_VILLAGER_CLASS, -1, 0, "setProfession", "setAge"),
    WITCH(ClassTypes.ENTITY_WITCH_CLASS, -1, 0.5),
    WITHER(ClassTypes.ENTITY_WITHER_CLASS, -1, 1.75, "g"),
    ZOMBIE(ClassTypes.ENTITY_ZOMBIE_CLASS, -1, 0, "setBaby"),
    WOLF(ClassTypes.ENTITY_WOLF_CLASS, -1, -1, "setSitting", "setTamed", "setAngry", "setAge", "setCollarColor"),
    END_CRYSTAL(ClassTypes.ENTITY_ENDER_CRYSTAL_CLASS, 51, 0);

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

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
     * The customization method names;
     */
    private final List<String> customization;

    /**
     * The customization methods.
     */
    private final HashMap<String, Method> customizationMethods;

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
     * @param customization The customization methods for the entity.
     */
    NPCType(Class<?> entityClass,
            String newName,
            int id,
            double holoHeight,
            String... customization) {
        this.entityClass = entityClass;
        this.newName = newName;
        this.id = id;
        this.holoHeight = holoHeight;
        this.customization = Arrays.asList(customization);
        this.customizationMethods = new HashMap<>();
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
     * Loads the npc type.
     */
    public void load() {
        if (getEntityClass() == null)
            return;

        // Load npc customization methods
        for (Method method : getEntityClass().getMethods()) {
            if (!getCustomizationMethods().containsKey(method.getName()) && getCustomization().contains(method.getName())) {
                getCustomizationMethods().put(method.getName(), method);
            }
        }

        try {
            boolean isNpcPlayer = this == NPCType.PLAYER;

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
                    if (entityType != null && !isNpcPlayer)
                        constructor = getEntityClass().getConstructor(getEntityType().getClass(), ClassTypes.WORLD_CLASS);
                }
            } else {
                if (!isNpcPlayer)
                    constructor = getEntityClass().getConstructor(ClassTypes.WORLD_CLASS);
            }
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new AssertionError(noSuchMethodException);
        }
    }

    /**
     * For the customization command a is necessary convert each value to.
     * its primitive data type of the method.
     * <p>
     * Example if the method parameter types are.
     * boolean.class & double.class, and the input values are.
     * new String[]{"true", "10"} it will convert to its correct primitive data type.
     * > (boolean.class) true, (double.class) 10.00.
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
                newArray[i] = typeProperty.function.apply(strings[i]);
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
     * @param name   The method name.
     * @param entity The npc.
     * @param values The method values.
     * @throws InvocationTargetException If method could not be invoked.
     */
    public void invokeMethod(String name, Object entity, Object[] values) throws InvocationTargetException, IllegalAccessException {
        if (!getCustomizationMethods().containsKey(name))
            return;

        Method method = getCustomizationMethods().get(name);
        method.invoke(entity, values);
    }

    /**
     * @inheritDoc
     */
    enum TypeProperty {

        STRING(String.class, String::toString),
        BOOLEAN(boolean.class, Boolean::parseBoolean),
        INT(int.class, Integer::parseInt),
        DOUBLE(double.class, Double::parseDouble),
        FLOAT(float.class, Float::parseFloat),
        SHORT(short.class, Short::parseShort),
        LONG(long.class, Long::parseLong);

        /**
         * The primitive type.
         */
        private final Class<?> primitiveType;

        /**
         * A function that parses an String to its corresponding primitive type.
         */
        private final Function<String, ?> function;

        /**
         * Creates a new type class for a primitive type.
         *
         * @param primitiveType The primitive type.
         * @param function The primitive type parse function.
         */
        TypeProperty(Class<?> primitiveType,
                     Function<String, ?> function) {
            this.primitiveType = primitiveType;
            this.function = function;
        }

        /**
         * Locates a type property by its primitive type.
         *
         * @param primitiveType The primitiveType;
         * @return The corresponding enum or {@code null} if not found.
         */
        public static TypeProperty forType(Class<?> primitiveType) {
            if (primitiveType == String.class)
                return STRING;
            else if (primitiveType == boolean.class)
                return BOOLEAN;
            else if (primitiveType == int.class)
                return INT;
            else if (primitiveType == double.class)
                return DOUBLE;
            else if (primitiveType == float.class)
                return FLOAT;
            else if (primitiveType == short.class)
                return SHORT;
            else if (primitiveType == long.class)
                return LONG;
            else
                return null;
        }
    }
}
