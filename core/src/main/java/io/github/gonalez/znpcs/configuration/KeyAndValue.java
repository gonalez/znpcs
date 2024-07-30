package io.github.gonalez.znpcs.configuration;

public final class KeyAndValue {
  private final String key;
  private final Object value;

  public KeyAndValue(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }
}
