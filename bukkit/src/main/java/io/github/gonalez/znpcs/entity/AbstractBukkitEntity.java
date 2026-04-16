package io.github.gonalez.znpcs.entity;

import io.github.gonalez.znpcs.model.WorldLocation;
import org.bukkit.entity.Entity;

public abstract class AbstractBukkitEntity implements BukkitEntity {
  public abstract Entity getEntity();

  @Override
  public WorldLocation getPosition() {
    return WorldLocation.from(getEntity().getLocation());
  }
}
