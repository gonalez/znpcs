package io.github.gonalez.znpcs.metadata;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public abstract class FileMetadataStore implements MetadataStore {

  /** Constructor for use by subclasses. */
  protected FileMetadataStore() {}

  protected abstract Reader openReader(String contextId) throws IOException;

  protected abstract Writer openWriter(String contextId) throws IOException;

  protected final void write(String contextId, CharSequence content) throws IOException {
    try (Writer writer = openWriter(contextId)) {
      writer.append(content);
    }
  }
}
