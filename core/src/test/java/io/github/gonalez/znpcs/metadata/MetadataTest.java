package io.github.gonalez.znpcs.metadata;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Metadata}. */
@RunWith(JUnit4.class)
public class MetadataTest {
  private static final MetadataKey<String> STRING_METADATA_KEY =
      MetadataKey.of("test", String.class);
  private static final MetadataKey<Integer> INTEGER_METADATA_KEY =
      MetadataKey.of("integer", Integer.class);
  private static final MetadataKey<List<Integer>> INTEGER_LIST_KEY =
      MetadataKey.of("integers", new TypeToken<List<Integer>>() {}.getType());

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private static final Executor executor = directExecutor();
  private MetadataStore metadataStore;

  @Before
  public void setUp() throws IOException {
    Path directory = temporaryFolder.getRoot().toPath();
    metadataStore =
        new GsonMetadataStore(new Gson(), executor) {
          @Override
          protected Reader openReader(String contextId) throws IOException {
            return Files.newBufferedReader(
                directory.resolve(contextId + ".json"), StandardCharsets.UTF_8);
          }

          @Override
          protected Writer openWriter(String contextId) throws IOException {
            return Files.newBufferedWriter(
                directory.resolve(contextId + ".json"), StandardCharsets.UTF_8);
          }
        };
  }

  @Test
  public void testBuilder_put_returnValues() throws Exception {
    MetadataKey<String> key = MetadataKey.of("foo", String.class);

    Metadata context = Metadata.builder().put(key, "bar").put(String.class, "baz").build();

    assertThat(context.get(key)).isEqualTo("bar");
    assertThat(context.get(String.class)).isEqualTo("baz");
  }

  @Test
  public void upsertThenRead_returnsStoredMetadata() throws Exception {
    List<Integer> integers = ImmutableList.of(1, 2, 3, 4, 5);
    Metadata metadata =
        Metadata.builder()
            .put(STRING_METADATA_KEY, "hello world")
            .put(INTEGER_METADATA_KEY, 10)
            .put(INTEGER_LIST_KEY, integers)
            .build();

    metadataStore.upsert("test", metadata).get();

    Optional<Metadata> result =
        metadataStore
            .read(
                "test",
                MetadataKey.toMap(
                    ImmutableList.of(STRING_METADATA_KEY, INTEGER_METADATA_KEY, INTEGER_LIST_KEY)))
            .get();

    assertThat(result).isPresent();
    assertThat(result.get().get(STRING_METADATA_KEY)).isEqualTo("hello world");
    assertThat(result.get().get(INTEGER_METADATA_KEY)).isEqualTo(10);
    assertThat(result.get().get(INTEGER_LIST_KEY)).isEqualTo(integers);
  }
}
