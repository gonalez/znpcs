package io.github.gonalez.znpcs.entity;

/** Base class for all exceptions that can occur when interacting with an entity. */
public class EntityException extends Exception {

  public EntityException(String message) {
    super(message);
  }

  public EntityException(String message, Throwable cause) {
    super(message, cause);
  }
}
