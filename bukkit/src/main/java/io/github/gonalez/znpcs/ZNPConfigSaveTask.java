package io.github.gonalez.znpcs;

 import static com.google.common.base.Predicates.alwaysTrue;

import org.bukkit.scheduler.BukkitRunnable;

class ZNPConfigSaveTask extends BukkitRunnable {

  @Override
  public void run() {
    ZNPConfigUtils.rewriteConfigs(alwaysTrue());
  }
}
