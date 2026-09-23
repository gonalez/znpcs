package io.github.gonalez.znpcs.metadata;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class GsonMetadataStore extends FileMetadataStore {
  private final Gson gson;
  private final Executor executor;

  public GsonMetadataStore(Gson gson, Executor executor) {
    this.gson = gson;
    this.executor = executor;
  }

  @Override
  public ListenableFuture<Optional<Metadata>> read(
      String contextId, ImmutableMap<String, MetadataKey<?>> readableKeys) {
    return Futures.submit(
        () -> {
          Metadata.Builder builder = Metadata.builder();

          try (Reader reader = openReader(contextId)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
              MetadataKey<?> key = readableKeys.get(entry.getKey());
              if (key == null || key.getIdentifier() == null) {
                continue;
              }
              builder.put(key, gson.fromJson(entry.getValue(), key.getType()));
            }
          }

          return Optional.of(builder.build());
        },
        executor);
  }

  @Override
  public ListenableFuture<Void> upsert(String contextId, Metadata metadata) {
    return Futures.submit(
        () -> {
          JsonObject json = new JsonObject();

          for (MetadataKey<?> key : metadata.identifiedKeys()) {
            Object value = metadata.get(key);
            json.add(key.getIdentifier(), gson.toJsonTree(value));
          }

          try (Writer writer = openWriter(contextId)) {
            gson.toJson(json, writer);
          }

          return null;
        },
        executor);
  }
}
