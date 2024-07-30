package io.github.gonalez.znpcs.configuration;

import io.github.gonalez.znpcs.npc.NamingType;

public class ConfigConfiguration extends Configuration {

  @ConfigurationKey(
      name = "VIEW_DISTANCE",
      description = "View distance in blocks")
  public int viewDistance = 32;

  @ConfigurationKey(
      name = "REPLACE_SYMBOL",
      description = "Symbol for replacement")
  public String replaceSymbol = "-";

  @ConfigurationKey(
      name = "SAVE_NPCS_DELAY_SECONDS",
      description = "Delay in seconds to save NPCs")
  public int saveNpcsDelaySeconds = 600;

  @ConfigurationKey(
      name = "MAX_PATH_LOCATIONS",
      description = "Maximum path locations")
  public int maxPathLocations = 500;

  @ConfigurationKey(
      name = "NAMING_METHOD",
      description = "Method for naming")
  public NamingType namingMethod = NamingType.DEFAULT;

  @ConfigurationKey(
      name = "DEBUG_ENABLED",
      description = "Is debug enabled")
  public boolean debugEnabled = true;

  @ConfigurationKey(
      name = "LINE_SPACING",
      description = "Spacing between lines")
  public double lineSpacing = 0.3;

  @ConfigurationKey(
      name = "ANIMATION_RGB",
      description = "Is animation RGB enabled")
  public boolean animationRgb = false;
}
