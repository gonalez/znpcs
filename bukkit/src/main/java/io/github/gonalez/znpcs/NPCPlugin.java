package io.github.gonalez.znpcs;

import io.github.gonalez.znpcs.skin.AshconSkinFetcherServer;
import io.github.gonalez.znpcs.skin.MineSkinFetcher;
import io.github.gonalez.znpcs.skin.SkinFetcher;
import io.github.gonalez.znpcs.skin.SkinFetcherImpl;
import java.util.concurrent.Executors;
import org.bukkit.plugin.java.JavaPlugin;

public class NPCPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    SkinFetcher skinFetcher =
        SkinFetcherImpl.builder()
            .setSkinExecutor(Executors.newSingleThreadExecutor())
            .addSkinFetcherServer(new AshconSkinFetcherServer(), new MineSkinFetcher())
            .build();
  }
}
