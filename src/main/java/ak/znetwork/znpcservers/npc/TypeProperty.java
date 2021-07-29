package ak.znetwork.znpcservers.npc;

import java.util.function.Function;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum TypeProperty {
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
     * @param function      The primitive type parse function.
     */
    TypeProperty(Class<?> primitiveType,
                 Function<String, ?> function) {
        this.primitiveType = primitiveType;
        this.function = function;
    }

    /**
     * Returns the type parse function.
     *
     * @return The type parse function.
     */
    public Function<String, ?> getFunction() {
        return function;
    }

    /**
     * Locates a type property by its primitive type.
     *
     * @param primitiveType The primitive type
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