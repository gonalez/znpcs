package io.github.gonalez.znpcs.config;

public class ConfigConfig extends Config {

  @ConfigOption(
      name = "VIEW_DISTANCE",
      description = "View distance in blocks")
  public int viewDistance = 32;

  @ConfigOption(
      name = "REPLACE_SYMBOL",
      description = "Symbol for replacement")
  public String replaceSymbol = "-";

  @ConfigOption(
      name = "SAVE_NPCS_DELAY_SECONDS",
      description = "Delay in seconds to save NPCs")
  public int saveNpcsDelaySeconds = 600;

  @ConfigOption(
      name = "MAX_PATH_LOCATIONS",
      description = "Maximum path locations")
  public int maxPathLocations = 500;

  @ConfigOption(
      name = "DEBUG_ENABLED",
      description = "Is debug enabled")
  public boolean debugEnabled = true;

  @ConfigOption(
      name = "LINE_SPACING",
      description = "Spacing between lines")
  public double lineSpacing = 0.3;

  @ConfigOption(
      name = "ANIMATION_RGB",
      description = "Is animation RGB enabled")
  public boolean animationRgb = false;
}
