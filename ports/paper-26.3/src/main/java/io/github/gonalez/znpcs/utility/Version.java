/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.utility;

import java.io.Serializable;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

/**
 * Retains the 5.3 ABI and compares legacy, abbreviated and calendar version strings numerically.
 */
public final class Version implements Comparable<Version>, Serializable {
  private static final Pattern PATTERN =
      Pattern.compile("^v?(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:[-+.]\\S*)?$");
  public static final Version SERVER_VERSION = fromBukkit();
  static final Version MAX_VERSION = new Version(Integer.MAX_VALUE, 0, 0);
  final int minor;
  final int patch;
  private final int revision;

  private Version(int minor, int patch, int revision) {
    this.minor = minor;
    this.patch = patch;
    this.revision = revision;
  }

  public static Version fromBukkit() {
    return fromString(Bukkit.getBukkitVersion());
  }

  public static Version fromString(String value) {
    if (value == null) throw new IllegalArgumentException("Missing Minecraft version");
    var m = PATTERN.matcher(value.trim());
    if (!m.matches()) throw new IllegalArgumentException("Invalid Minecraft version: " + value);
    int first = Integer.parseInt(m.group(1));
    int second = m.group(2) == null ? 0 : Integer.parseInt(m.group(2));
    int third = m.group(3) == null ? 0 : Integer.parseInt(m.group(3));
    return first == 1 ? new Version(second, third, 0) : new Version(first, second, third);
  }

  @Override
  public int compareTo(Version other) {
    int comparison = Integer.compare(minor, other.minor);
    if (comparison == 0) comparison = Integer.compare(patch, other.patch);
    return comparison == 0 ? Integer.compare(revision, other.revision) : comparison;
  }

  @Override
  public String toString() {
    return minor + "." + patch + (revision == 0 ? "" : "." + revision);
  }

  public enum Type {
    SPIGOT,
    PAPER_SPIGOT
  }
}
