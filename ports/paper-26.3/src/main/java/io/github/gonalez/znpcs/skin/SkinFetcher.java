/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.skin;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.ServersNPC;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** The 5.3 provider API, with deterministic routing and a callback on every completed request. */
public class SkinFetcher {
  private static final Logger LOGGER = Logger.getLogger(SkinFetcher.class.getName());
  private final Executor executor;
  private final Executor callbackExecutor;
  private final ImmutableList<SkinFetcherServer> servers;

  public SkinFetcher(Executor executor, ImmutableList<SkinFetcherServer> servers) {
    this(executor, servers, SkinFetcher::runOnMain);
  }

  public SkinFetcher(
      Executor executor, ImmutableList<SkinFetcherServer> servers, Executor callbacks) {
    this.executor = Objects.requireNonNull(executor);
    this.servers = ImmutableList.copyOf(servers);
    this.callbackExecutor = Objects.requireNonNull(callbacks);
  }

  public void fetchSkin(String name, SkinFetchListener listener) {
    Objects.requireNonNull(listener);
    if (name == null || name.isBlank()) {
      deliver(
          () ->
              listener.onError(
                  new IOException("A Minecraft name, UUID or skin image URL is required")));
      return;
    }
    String input = name.trim();
    try {
      executor.execute(() -> fetch(input, listener));
    } catch (RejectedExecutionException e) {
      deliver(
          () ->
              listener.onError(
                  new IOException("The skin request queue is full or the plugin is stopping", e)));
    }
  }

  private void fetch(String name, SkinFetchListener listener) {
    IOException failure = new IOException("No skin provider succeeded for " + describe(name));
    boolean url = isUrl(name);
    for (SkinFetcherServer server : servers) {
      if ((server instanceof MineSkinFetch && !url)
          || (server instanceof MojangNameSkinFetch && url)) continue;
      HttpURLConnection connection = null;
      SkinProperties properties;
      try {
        connection = server.createConnection(name);
        properties = server.decodeProperties(server.readHttpResponse(connection));
        if (properties == null
            || properties.getValue() == null
            || properties.getValue().isBlank()
            || properties.getSignature() == null
            || properties.getSignature().isBlank())
          throw new IOException("Provider did not return signed texture data");
      } catch (Exception e) {
        failure.addSuppressed(new IOException(server.getName() + ": " + e.getMessage(), e));
        continue;
      } finally {
        if (connection != null) connection.disconnect();
      }
      // Callback exceptions are not provider failures and must never trigger a second callback.
      SkinProperties result = properties;
      deliver(() -> listener.onSuccess(server, result));
      return;
    }
    StringBuilder reason = new StringBuilder(failure.getMessage());
    for (Throwable error : failure.getSuppressed()) reason.append("; ").append(error.getMessage());
    LOGGER.warning(reason.toString());
    deliver(() -> listener.onError(failure));
  }

  public static boolean isUrl(String value) {
    try {
      String scheme = URI.create(value).getScheme();
      return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static String describe(String value) {
    if (isUrl(value)) return "skin image host " + URI.create(value).getHost();
    return value.replaceAll("[^A-Za-z0-9_-]", "?");
  }

  private void deliver(Runnable callback) {
    callbackExecutor.execute(callback);
  }

  private static void runOnMain(Runnable task) {
    var plugin = JavaPlugin.getPlugin(ServersNPC.class);
    if (!plugin.isEnabled()) return;
    if (Bukkit.isPrimaryThread()) task.run();
    else Bukkit.getScheduler().runTask(plugin, task);
  }

  public interface SkinFetchListener {
    default void onSuccess(SkinFetcherServer server, SkinProperties properties) {}

    default void onError(Throwable throwable) {}
  }
}
