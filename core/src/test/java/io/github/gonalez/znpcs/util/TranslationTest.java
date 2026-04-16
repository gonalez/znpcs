package io.github.gonalez.znpcs.util;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Translation}. */
@RunWith(JUnit4.class)
public class TranslationTest {
  private static final String TEST_TRANSLATION_KEY = "test";
  private static final String INVALID_TRANSLATION_KEY = "test key";

  @Test
  public void register_translation_return_value() throws Exception {
    assertThat(Translation.has(TEST_TRANSLATION_KEY)).isFalse();
    Translation.register(TEST_TRANSLATION_KEY, "hello world");

    assertThat(Translation.has(TEST_TRANSLATION_KEY)).isTrue();
    assertThat(Translation.get(TEST_TRANSLATION_KEY)).isEqualTo("hello world");
  }

  @Test
  public void test_invalid_key_characters_should_fail() throws Exception {
    IllegalArgumentException expected =
        assertThrows(
            IllegalArgumentException.class, () -> Translation.register(INVALID_TRANSLATION_KEY, TEST_TRANSLATION_KEY));
    assertThat(expected)
        .hasMessageThat()
        .isEqualTo(String.format("'%s' must only contain letters and/or digits", INVALID_TRANSLATION_KEY));
  }
}
