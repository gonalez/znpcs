package io.github.gonalez.znpcs.metadata;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;

public interface MetadataStore {
  ListenableFuture<Optional<Metadata>> read(
      String contextId, ImmutableMap<String, MetadataKey<?>> readableKeys);

  ListenableFuture<Void> upsert(String contextId, Metadata metadata);
}
