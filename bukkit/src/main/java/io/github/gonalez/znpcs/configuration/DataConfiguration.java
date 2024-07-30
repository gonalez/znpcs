package io.github.gonalez.znpcs.configuration;

import io.github.gonalez.znpcs.npc.NPCModel;
import java.util.ArrayList;
import java.util.List;

public class DataConfiguration extends Configuration {

  @ConfigurationKey(
      name = "NPC_LIST",
      description = "List of NPC models")
  public List<NPCModel> npcList = new ArrayList<>();
}
