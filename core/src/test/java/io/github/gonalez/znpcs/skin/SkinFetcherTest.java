package io.github.gonalez.znpcs.skin;

import static com.google.common.truth.Truth.assertThat;
import static java.net.http.HttpClient.Redirect.ALWAYS;
import static org.junit.Assert.assertThrows;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link SkinFetcher}. */
@RunWith(JUnit4.class)
public class SkinFetcherTest {
  private SkinFetcher skinFetcher;

  @Before
  public void setup() {
    HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(ALWAYS)
            .proxy(ProxySelector.getDefault())
            .build();
    skinFetcher =
        SkinFetcherImpl.builder()
            .setSkinExecutor(Executors.newCachedThreadPool())
            .addSkinFetcherServer(
                new AshconGameProfileProvider(httpClient),
                new MineSkinGameProfileProvider(httpClient),
                new MojangGameProfileProvider(httpClient))
            .build();
  }

  @Test
  public void testSkinFetcher_name() throws Exception {
    Future<GameProfile> fetched = skinFetcher.fetchGameProfile("Qentin", null);

    GameProfile gameProfile = fetched.get();
    Property property = GameProfiles.getTextureProperty(gameProfile);
    assertThat(property).isNotNull();

    assertThat(gameProfile.getId())
        .isEqualTo(UUID.fromString("51800622-9dae-4b23-84a7-26d6a27c60db"));
  }

  @Test
  public void testSkinFetcher_image() throws Exception {
    Future<GameProfile> fetched =
        skinFetcher.fetchGameProfile("https://i.imgur.com/pVeIlTz.png", null);

    GameProfile gameProfile = fetched.get();
    Property property = GameProfiles.getTextureProperty(gameProfile);
    assertThat(property).isNotNull();

    assertThat(property.getSignature())
        .isEqualTo(
            "Ix3UhKVhw9tSnhtGeZZn+Sr1cySHz53bZw/oGIW5z+FdOWo7Q2swRO1o/zDXsbRV6oHETQm3sWhwQWbnuaDPQfMZIwpsO91xCXJ4dYwQrwxNzd3/cX8Tu8IQGvVCm0UyXq4mL2qGIJKYLgEbi8C29AWjee8zKQ2K0TOaiTCQBmy/mfpQHLrk4abDo/Jy+DS3CYm8ENLmIvPy0ERd967vv7Yz7KONjUPaacGQx5oY2q4AS4Plg9wDGdwmAL6CNC+iMLM0ajdjztxAhSZboM5bnd3BzPdKArIQmLuI95b4Qko45uXFS/kEDh3uey3QL2IBxqgpdDXPKgLkAKW3meqeQPq6UNhT3zoBxUPf9uhPHxVpfbWfizqHHFgANaz23QqIUsdhqkTbwgBkpDYzCJ7HEwIyNEpkMAT3AZ/xc4ny03QcnSvPLgD329UNZQvmAHwuNihX/3XrZaFx6467n9B66YAPfuWGi6P8lFoaehxDIWqxicANTZtVXv5JBdIgMpV+uwinO04amKC3nuE8chFig9WMFcitjpuJ9ZGRAf+LVSLiUQWo7Gx+EOw0T2nrac26+Vslxg50qWHOzYIIJD88X0iWibufPg4M4v23Y6jUOZ0QYVEJJA7OoQ8h+bPtT88on8UELdckh/4ltiRE3myYAhfodvfgbgdIxFJlxGJe4qU=");
    assertThat(property.getValue())
        .isEqualTo(
            "ewogICJ0aW1lc3RhbXAiIDogMTYyODY2ODM4MTc0MiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQyZjQ3YzBiNWQyZGQ3NDc1ZDNlNzg3NzVhMzE5NGE3ZTU2YWQxNmE3ZTVlNmM0YTI0YjJiOWVmODY3YjViNyIKICAgIH0KICB9Cn0=");
  }

  @Test
  public void testSkinFetcher_fail() throws Exception {
    ListenableFuture<GameProfile> fetched =
        skinFetcher.fetchGameProfile("https://textures.minecraft.net", null);

    ExecutionException e = assertThrows(ExecutionException.class, fetched::get);
    assertThat(e).hasCauseThat().hasMessageThat().startsWith("No skin found for");
  }
}
