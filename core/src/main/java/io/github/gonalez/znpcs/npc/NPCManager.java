package io.github.gonalez.znpcs.npc;

import java.util.Optional;

public interface NPCManager {

  /** Returns the npc associated with {@code id}, or empty if no npc is found. */
  Optional<NPC> getNpc(int id);
}
