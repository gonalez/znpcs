package io.github.gonalez.znpcs.user;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

/** A handler that responds to NPC interact messages. */
final class NpcInteractServerHandler extends ChannelInboundHandlerAdapter {
  private static final long DEFAULT_NPC_INTERACT_INTERVAL_MS = 5_000;

  final Object lock = new Object();

  @GuardedBy("lock")
  final HashMap<NPCValuePair, Stopwatch> npcClickTimers = new HashMap<>();

  /** A npc/value pair used in for tracking npc click cooldowns. */
  private static final class NPCValuePair implements Serializable {
    final NPC npc;
    final Object value;

    private NPCValuePair(NPC npc, Object value) {
      this.npc = npc;
      this.value = value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      NPCValuePair npcValuePair = (NPCValuePair) o;
      return npc == npcValuePair.npc && Objects.equals(value, npcValuePair.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(npc, value);
    }
  }

  final ZUser user;

  public NpcInteractServerHandler(ZUser user) {
    this.user = Preconditions.checkNotNull(user);
  }

  /** Attempts to start or retrieve a timer for the given NPC and associated value. */
  Stopwatch tryStartTiming(NPC npc, Object value) {
    NPCValuePair npcValuePair = new NPCValuePair(npc, value);
    synchronized (lock) {
      Stopwatch stopwatch = npcClickTimers.get(npcValuePair);
      if (stopwatch == null) {
        npcClickTimers.put(npcValuePair, stopwatch = Stopwatch.createStarted());
      }
      return stopwatch;
    }
  }

  /**
   * Resets the timer for the given NPC and value if the specified duration has elapsed.
   * If the elapsed time is greater than the provided duration, the timer is reset and
   * started again.
   *
   * <p>If the timer has not yet expired, the method returns {@code true} to indicate that the
   * action should not be reset. Otherwise, it returns {@code false}, signaling that the timer has
   * expired and was reset.
   */
  boolean resetTimerIfExpired(NPC npc, Object value, long millis) {
    Stopwatch stopwatch = tryStartTiming(npc, value);
    if (stopwatch.elapsed(TimeUnit.MILLISECONDS) > millis) {
      stopwatch.reset().start();
      return false;
    }
    return true;
  }

  /**
   * Handles incoming packets related to Entity interaction. When a packet of type {@link
   * CacheRegistry#PACKET_PLAY_IN_USE_ENTITY_CLASS} is received, it processes the entity ID
   * and checks if the NPC associated with that entity has any registered actions to perform.
   *
   * <p><b>Note:</b> If the packet is not of the expected type, the method simply passes the
   * message to the next handler in the pipeline for further processing.
   *
   * @param ctx the context for this handler.
   * @param msg the incoming message.
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (CacheRegistry.PACKET_PLAY_IN_USE_ENTITY_CLASS.isInstance(msg)) {
      int entityId = CacheRegistry.PACKET_IN_USE_ENTITY_ID_FIELD.load().getInt(msg);
      for (NPC npc : NPC.all()) {
        NPCModel npcModel = npc.getNpcPojo();
        if (npc.getEntityID() != entityId) {
          continue;
        }
        if (resetTimerIfExpired(npc, npc.getEntityID(), DEFAULT_NPC_INTERACT_INTERVAL_MS)) {
          return;
        }
        for (NPCAction npcAction : npcModel.getClickActions()) {
          if (npcAction.getDelay() > 0) {
            if (resetTimerIfExpired(npc, npcAction, npcAction.getDelay() * 1000L)) {
              return;
            }
          }
          npcAction.run(user, npcAction.getAction());
        }
      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }
}
