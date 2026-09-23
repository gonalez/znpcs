package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.metadata.MetadataKey;

/** Keys for accessing through {@link NPC#getMetadata() npc metadata}. */
public final class NPCMetadataKeys {
  public static final MetadataKey<String> NPC_NAME_METADATA_KEY =
      MetadataKey.of("name", String.class);

  public static final MetadataKey<String> NPC_STORED_SKIN_NAME_METADATA_KEY =
      MetadataKey.of("stored-skin-name", String.class);

  private NPCMetadataKeys() {}
}
