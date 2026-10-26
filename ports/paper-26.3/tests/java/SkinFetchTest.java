package io.github.gonalez.znpcs.skin;

import com.google.common.collect.ImmutableList;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Standalone integration tests: real local HTTP, provider fallback and exactly-once callbacks. */
public final class SkinFetchTest {
  private static int checks;
  private static final String UUID = "069a79f444e94726a5befca90e38aaf5";
  private static final String PROFILE =
      "{\"properties\":[{\"name\":\"irrelevant\",\"value\":\"ignore\"},{\"name\":\"textures\",\"value\":\"texture-value\",\"signature\":\"signed-value\"}]}";
  private static final String SKIN =
      "{\"skin\":{\"texture\":{\"data\":{\"value\":\"texture-value\",\"signature\":\"signed-value\"}}}}";
  private static final AtomicInteger nameRequests = new AtomicInteger(),
      queueRequests = new AtomicInteger();
  private static volatile int queueMode;

  private static void check(boolean condition, String label) {
    if (!condition) throw new AssertionError(label);
    checks++;
  }

  private record Result(
      String provider, SkinProperties properties, Throwable error, String thread) {}

  private static Result fetch(SkinFetcher fetcher, String name) throws Exception {
    CompletableFuture<Result> result = new CompletableFuture<>();
    AtomicInteger callbacks = new AtomicInteger();
    fetcher.fetchSkin(
        name,
        new SkinFetcher.SkinFetchListener() {
          @Override
          public void onSuccess(SkinFetcherServer s, SkinProperties p) {
            callbacks.incrementAndGet();
            result.complete(new Result(s.getName(), p, null, Thread.currentThread().getName()));
          }

          @Override
          public void onError(Throwable e) {
            callbacks.incrementAndGet();
            result.complete(new Result(null, null, e, Thread.currentThread().getName()));
          }
        });
    Result value = result.get(8, TimeUnit.SECONDS);
    check(callbacks.get() == 1, "exactly one callback");
    return value;
  }

  private static SkinFetcherServer badProvider(URI endpoint, boolean runtime) {
    return new SkinFetcherServer() {
      @Override
      public String getName() {
        return "Broken fixture provider";
      }

      @Override
      protected HttpURLConnection createConnection(String name) throws IOException {
        return SkinFetchHttp.openConnection(SkinFetchHttp.RequestMethod.GET, endpoint.toString());
      }

      @Override
      protected SkinProperties decodeProperties(SkinFetchHttp.HttpResponse response) {
        if (runtime) throw new IllegalStateException("malformed provider data");
        return new SkinProperties("", "");
      }
    };
  }

  public static void main(String[] args) throws Exception {
    var io = Executors.newFixedThreadPool(2);
    var callbacks = Executors.newSingleThreadExecutor(r -> new Thread(r, "test-main-callback"));
    HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/",
        exchange -> {
          String path = exchange.getRequestURI().getPath();
          int code = 200;
          String body = "{}";
          if (path.equals("/names/Notch")) {
            nameRequests.incrementAndGet();
            body = "{\"id\":\"" + UUID + "\"}";
          } else if (path.equals("/names/Broken")) body = "<not-json>";
          else if (path.equals("/profiles/" + UUID)) body = PROFILE;
          else if (path.equals("/rate")) code = 429;
          else if (path.equals("/broken")) body = "bad-response";
          else if (path.equals("/v2/queue")) {
            queueRequests.incrementAndGet();
            String request =
                new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            check(exchange.getRequestMethod().equals("POST"), "URL skin is posted to queue");
            check(request.contains("unlisted"), "URL skin is not publicly listed");
            check(
                exchange.getRequestHeaders().getFirst("User-Agent").startsWith("ZNPCs/"),
                "MineSkin user agent");
            if (queueMode == 3) code = 401;
            else if (queueMode == 0) body = SKIN;
            else {
              code = 202;
              body = "{\"job\":{\"id\":\"not-a-uuid:42\",\"status\":\"waiting\"}}";
            }
          } else if (path.equals("/v2/queue/not-a-uuid:42")) {
            if (queueMode == 1) body = SKIN;
            else if (queueMode == 2)
              body = "{\"job\":{\"status\":\"completed\",\"result\":\"skin-123\"}}";
            else if (queueMode == 4) body = "{\"job\":{\"status\":\"failed\"}}";
            else {
              code = 202;
              body = "{\"job\":{\"id\":\"not-a-uuid:42\",\"status\":\"waiting\"}}";
            }
          } else if (path.equals("/v2/skins/skin-123")) body = SKIN;
          else code = 404;
          byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(code, bytes.length);
          try (var out = exchange.getResponseBody()) {
            out.write(bytes);
          }
        });
    server.start();
    URI base = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
    try {
      var mojang = new MojangNameSkinFetch(base.resolve("names/"), base.resolve("profiles/"));
      var mine = new MineSkinFetch(base.resolve("v2/"), "fixture-key", 5000);
      var normal = new SkinFetcher(io, ImmutableList.of(mojang, mine), callbacks);
      Result name = fetch(normal, "Notch");
      check(name.error == null, "name succeeds");
      check(name.provider.equals("Mojang"), "name routes to Mojang");
      check(
          name.properties.getValue().equals("texture-value")
              && name.properties.getSignature().equals("signed-value"),
          "signed textures selected by property name");
      check(name.thread.equals("test-main-callback"), "configured callback executor used");
      check(queueRequests.get() == 0, "names do not hit MineSkin");
      int before = nameRequests.get();
      Result uuid = fetch(normal, UUID);
      check(uuid.error == null, "UUID succeeds");
      check(nameRequests.get() == before, "UUID skips name API");
      Result missing = fetch(normal, "NotAPlayer");
      check(
          missing.error != null && missing.error.getSuppressed()[0].getMessage().contains("404"),
          "404 is reported");
      Result malformed = fetch(normal, "Broken");
      check(malformed.error != null, "malformed JSON reports failure");
      var fallback =
          new SkinFetcher(
              io, ImmutableList.of(badProvider(base.resolve("broken"), true), mojang), callbacks);
      check(fetch(fallback, "Notch").error == null, "runtime decoder failure falls back");
      var empty =
          new SkinFetcher(
              io, ImmutableList.of(badProvider(base.resolve("broken"), false)), callbacks);
      check(fetch(empty, "Notch").error != null, "empty unsigned texture data is rejected");
      var rate =
          new SkinFetcher(
              io, ImmutableList.of(badProvider(base.resolve("rate"), false)), callbacks);
      check(
          fetch(rate, "Notch").error.getSuppressed()[0].getMessage().contains("429"),
          "rate limit remains diagnosable");
      check(fetch(normal, "").error != null, "empty input calls onError");
      SkinFetcher rejected =
          new SkinFetcher(
              r -> {
                throw new RejectedExecutionException();
              },
              ImmutableList.of(mojang),
              callbacks);
      check(fetch(rejected, "Notch").error != null, "full executor queue calls onError");
      for (int mode : new int[] {0, 1, 2}) {
        queueMode = mode;
        Result url = fetch(normal, "https://example.invalid/skin.png");
        check(url.error == null, "MineSkin V2 response mode " + mode);
        check(url.provider.equals("MineSkin"), "URL routes to MineSkin");
      }
      queueMode = 3;
      Result unauthorized = fetch(normal, "https://example.invalid/skin.png");
      check(
          unauthorized.error.getSuppressed()[0].getMessage().contains("mineskin-api-key"),
          "missing key has an actionable error");
      queueMode = 4;
      check(
          fetch(normal, "https://example.invalid/skin.png").error != null,
          "failed MineSkin job calls onError");
      queueMode = 5;
      var shortWait =
          new SkinFetcher(
              io, ImmutableList.of(new MineSkinFetch(base.resolve("v2/"), "", 5)), callbacks);
      check(
          fetch(shortWait, "https://example.invalid/skin.png").error != null,
          "waiting job times out");
      System.out.println("PASS " + checks + " skin-fetch integration checks");
    } finally {
      server.stop(0);
      io.shutdownNow();
      callbacks.shutdownNow();
    }
  }
}
