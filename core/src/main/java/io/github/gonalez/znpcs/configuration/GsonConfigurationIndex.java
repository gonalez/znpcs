package io.github.gonalez.znpcs.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

/** A {@link PathConfigurationIndex} that reads and writes configurations from JSON file. */
public abstract class GsonConfigurationIndex extends PathConfigurationIndex {
  private final Gson gson;

  public GsonConfigurationIndex(Gson gson) {
    this.gson = checkNotNull(gson);
  }

  @Override
  protected ImmutableMap<String, Object> readConfiguration(
      Reader reader, Class<? extends Configuration> configClass) {
    ImmutableMap<String, Field> allFields = ConfigurationCache.getConfigFields(configClass);
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    JsonElement json = JsonParser.parseReader(reader);
    if (json.isJsonObject()) {
      mergeFromJsonObject(json.getAsJsonObject(), builder, allFields);
    } else {
      Map.Entry<String, Field> first = allFields.entrySet().iterator().next();
      builder.put(first.getKey(), gson.fromJson(json, first.getValue().getType()));
    }
    return builder.build();
  }

  private void mergeFromJsonObject(JsonObject jsonObject,
      ImmutableMap.Builder<String, Object> builder, ImmutableMap<String, Field> allFields) {
    for (Entry<String, Field> entry : allFields.entrySet()) {
      JsonElement element = jsonObject.get(entry.getKey());
      if (element != null) {
        builder.put(entry.getKey(), gson.fromJson(element, entry.getValue().getType()));
      }
    }
  }

  @Override
  protected void writeConfiguration(Writer writer, ImmutableMap<String, Object> values) {
    gson.toJson(values, writer);
  }
}
