package io.github.gonalez.znpcs.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import com.google.common.base.CharMatcher;

@Immutable
public final class Translation {
  private static final CharMatcher ALLOWED_CHARS =
      CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('0', '9').or(CharMatcher.anyOf("-.")));
  private static final String INVALID_ENTRY_MESSAGE = "invalid message key";
  // Tracks all translation strings.
  private static final HashMap<String, String> registry = new HashMap<>();

  @CanIgnoreReturnValue
  public static void register(String key, String message) {
    checkArgument(ALLOWED_CHARS.matchesAllOf(key), "'%s' must only contain letters and/or digits", key);
    registry.putIfAbsent(key, message);
  }

  /** Returns the translation of a given {@code errorCode}. */
  @Nonnull
  public static String get(String key, Object... args) {
    return String.format(registry.getOrDefault(key, INVALID_ENTRY_MESSAGE), args);
  }

  public static boolean has(String key) {
    return registry.containsKey(key);
  }

  private Translation() {}
}
