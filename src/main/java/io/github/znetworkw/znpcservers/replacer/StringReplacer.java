package io.github.znetworkw.znpcservers.replacer;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.replacer.internal.RGBLine;

import java.util.function.Function;

/**
 * Interface used for replacing an {@link String}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface StringReplacer extends Function<String, String> {
    /**
     * A list of custom default {@link StringReplacer}s.
     */
    ImmutableList<StringReplacer> DEFAULT_REPLACES = ImmutableList.of(new RGBLine());

    /**
     * Replaces the given string with custom default replaces.
     *
     * @return the converted string.
     */
    static String of(String string) {
        return DEFAULT_REPLACES.stream()
            .filter(StringReplacer::isSupported)
            .map(p -> (Function<String, String>) p)
            .reduce(Function.identity(), Function::andThen)
            .apply(string);
    }

    /**
     * The function implementation to replace the specified string.
     *
     * @param string the input string to replace.
     * @return the output string of the function.
     */
    String apply(String string);

    /**
     * Indicates if this replacer is supported for the current server version.
     *
     * @return {@code true} if this replacer is supported for the current server version.
     */
    boolean isSupported();
}
