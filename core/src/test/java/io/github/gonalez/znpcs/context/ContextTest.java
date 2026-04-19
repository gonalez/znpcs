package io.github.gonalez.znpcs.context;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Context}. */
@RunWith(JUnit4.class)
public class ContextTest {

  @Test
  public void testBuilder_put_returnValues() throws Exception {
    Context.Key<String> key = Context.Key.of("foo");

    Context context = Context.builder()
        .put(key, "bar")
        .put(String.class, "baz")
        .build();

    assertThat(context.get(key)).isEqualTo("bar");
    assertThat(context.get(String.class)).isEqualTo("baz");
  }
}
