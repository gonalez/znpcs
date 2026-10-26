/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableSet;

public final class PacketFactory {
  public static final Packet PACKET_FOR_CURRENT_VERSION = new Packet263();
  public static final ImmutableSet<Packet> ALL = ImmutableSet.of(PACKET_FOR_CURRENT_VERSION);

  public static Packet findPacketForVersion() {
    return PACKET_FOR_CURRENT_VERSION;
  }

  private PacketFactory() {}
}
