package io.github.gonalez.znpcs.model;

import static com.google.common.truth.Truth.assertThat;

import io.github.gonalez.znpcs.model.WorldLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link WorldLocation}. */
@RunWith(JUnit4.class)
public class WorldLocationTest {

  @Test
  public void compareTo_expectedResult() {
    WorldLocation a = new WorldLocation("world", 0, 0, 0, 0f, 0f);
    WorldLocation b = new WorldLocation("world", 1, 0, 0, 0f, 0f);
    WorldLocation c = new WorldLocation("world", 1, 1, 0, 0f, 0f);
    WorldLocation d = new WorldLocation("world", 1, 1, 1, 0f, 0f);

    assertThat(a.compareTo(b)).isLessThan(0);
    assertThat(b.compareTo(c)).isLessThan(0);
    assertThat(c.compareTo(d)).isLessThan(0);
    assertThat(b.compareTo(a)).isGreaterThan(0);
    assertThat(a.compareTo(c)).isLessThan(0);
    assertThat(a.compareTo(d)).isLessThan(0);
  }
}
