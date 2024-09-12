package io.github.gonalez.znpcs.skin;

/** General exception for any {@link SkinFetcher} errors. */
public final class SkinException extends Exception {

  public SkinException(String message) {
    super(message);
  }

  public SkinException(String message, Throwable cause) {
    super(message, cause);
  }
}
