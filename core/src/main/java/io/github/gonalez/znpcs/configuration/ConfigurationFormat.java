package io.github.gonalez.znpcs.configuration;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Configuration writing & reading strategies. */
public interface ConfigurationFormat {

  Reader open(InputStream inputStream) throws IOException;

  interface Reader extends ConfigurationFormat, Closeable {
    <T> T readFromStream(String key, Class<T> type) throws IOException;
  }

  Writer open(OutputStream outputStream) throws IOException;

  interface Writer extends ConfigurationFormat, Closeable {
    void addForWrite(String key, Object value) throws IOException;
    void writeToStream(Configuration configuration) throws IOException;
  }
}
