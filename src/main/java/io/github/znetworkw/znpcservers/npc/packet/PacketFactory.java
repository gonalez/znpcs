package io.github.znetworkw.znpcservers.npc.packet;

import com.google.common.collect.ImmutableSet;
import io.github.znetworkw.znpcservers.utility.Utils;

import java.util.Comparator;

/** Packet definitions. */
public final class PacketFactory {
    /** List of packets. */
    public static final ImmutableSet<Packet> ALL =
        ImmutableSet.of(
            new PacketV8(),
            new PacketV9(),
            new PacketV16(),
            new PacketV17());

    /**
     * Returns the packet instance for the server bukkit version.
     */
    public static Packet PACKET_FOR_CURRENT_VERSION =
        findPacketForVersion(Utils.BUKKIT_VERSION);

    /**
     * Tries to locate a packet instance by its version.
     *
     * @param version The version.
     * @return A packet instance for the given version.
     * @throws IllegalArgumentException If cannot find packets for the given version.
     */
    public static Packet findPacketForVersion(int version) {
        return ALL.stream()
            .filter(packet -> version >= packet.version())
            .max(Comparator.comparing(Packet::version))
            .orElseThrow(() -> new IllegalArgumentException("No packet instance found for version: " + version));
    }

    private PacketFactory() {}
}
