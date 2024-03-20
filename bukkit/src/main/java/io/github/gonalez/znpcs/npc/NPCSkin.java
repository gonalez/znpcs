package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.skin.SkinFetcherBuilder;
import io.github.gonalez.znpcs.skin.SkinFetcherResult;
import io.github.gonalez.znpcs.utility.Utils;

import java.util.Objects;
import java.util.logging.Level;

public class NPCSkin {
  private static final String[] EMPTY_ARRAY = new String[] { "", "" };
  
  private final String texture;
  
  private final String signature;
  
  protected NPCSkin(String... values) {
    if (values.length < 1)
      throw new IllegalArgumentException("Length cannot be zero or negative."); 
    this.texture = values[0];
    this.signature = values[1];
  }
  
  public String getTexture() {
    return this.texture;
  }
  
  public String getSignature() {
    return this.signature;
  }
  
  public int getLayerIndex() {
    return SkinLayerValues.findLayerByVersion();
  }
  
  public static NPCSkin forValues(String... values) {
    return new NPCSkin((values.length > 0) ? values : EMPTY_ARRAY);
  }
  
  public static void forName(String skin, SkinFetcherResult skinFetcherResult) {
    SkinFetcherBuilder.withName(skin).toSkinFetcher().doReadSkin(skinFetcherResult);
  }
  
  enum SkinLayerValues {
    V8(8, 12),
    V9(10, 13),
    V14(14, 15),
    V16(15, 16),
    V17(17, 17),
    V18(18, 17);
    
    final int minVersion;
    
    final int layerValue;
    
    SkinLayerValues(int minVersion, int layerValue) {
      this.minVersion = minVersion;
      this.layerValue = layerValue;
    }
    
    static int findLayerByVersion() {
      try{
        if (CacheRegistry.GET_PLAYER_MODEL_PARTS != null){
          //for static field's null is okay
          Object playerModelParts = CacheRegistry.GET_PLAYER_MODEL_PARTS.load().get(null);
          return (int) CacheRegistry.GET_TRACKED_DATA_ID.load().invoke(Objects.requireNonNull(playerModelParts));
        }else{
          ((java.util.logging.Logger)org.bukkit.Bukkit.getServer().getLogger()).log(Level.INFO, "CacheRegistry.GET_PLAYER_MODEL_PARTS was null. " + Utils.getBukkitPackage()+ ", " +Utils.BUKKIT_VERSION);
        }
      }catch (Throwable throwable){
        org.bukkit.Bukkit.getServer().getLogger().log(Level.INFO, "Could not get Dynamic Layer Id of PlayerModel entitydata", new RuntimeException(throwable));
      } //Some error occurred. maybe the field did not exist?
      org.bukkit.Bukkit.getServer().getLogger().log(Level.INFO, "Falling back to hardcoded values.");
      
      int value = V8.layerValue;
      for (SkinLayerValues skinLayerValue : values()) {
        if (Utils.BUKKIT_VERSION >= skinLayerValue.minVersion)
          value = skinLayerValue.layerValue; 
      } 
      return value;
    }
  }
}
