package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.npc.function.NpcFunctionContext;
import io.github.znetworkw.znpcservers.npc.function.NpcFunctionModel;
import io.github.znetworkw.znpcservers.utility.PluginLocation;

import java.io.Serializable;
import java.util.*;

/**
 * Information about an {@link Npc}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class NpcModel implements Cloneable, Serializable {
    /**
     * The npc unique id.
     */
    private int id;

    /**
     * The npc hologram height.
     */
    private double hologramHeight;

    /**
     * The texture and signature
     * of the npc skin.
     */
    private String skin, signature = "";

    /**
     * The npc path name.
     */
    private String pathName;

    /**
     * The npc glow color name.
     */
    private String glowName;

    /**
     * The npc location.
     */
    private PluginLocation location;

    /**
     * The npc type name.
     */
    private String npcType;

    /**
     * The npc hologram lines.
     */
    private List<String> hologramLines;

    /**
     * The npc functions.
     */
    private Map<String, NpcFunctionModel> npcFunctions;

    /**
     * The npc customization.
     */
    private Map<String, String[]> customizationMap;

    /**
     * Constructs a new {@link NpcModel}.
     *
     * @param id the unique id for the model.
     */
    public NpcModel(int id) {
        this();
        this.id = id;
        this.npcType = "player";
        skin = "";
        signature = "";
    }

    /**
     * Returns the npc id identifier.
     *
     * @return the npc id identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the {@link #getId()} of this object.
     *
     * @param id the npc id identifier.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Returns the current object with the new {@code id}. */
    public NpcModel withId(int id) {
        setId(id);
        return this;
    }

    /**
     * Returns the npc hologram height.
     *
     * @return the npc hologram height.
     */
    public double getHologramHeight() {
        return hologramHeight;
    }

    /**
     * Sets the {@link #getHologramHeight()} of this object.
     *
     * @param hologramHeight the npc hologram height.
     */
    public void setHologramHeight(double hologramHeight) {
        this.hologramHeight = hologramHeight;
    }

    /** Returns the current object with a new {@code hologramHeight}. */
    public NpcModel withHologramHeight(double hologramHeight) {
        setHologramHeight(hologramHeight);
        return this;
    }

    /**
     * Returns the npc skin texture.
     *
     * @return the npc skin texture.
     */
    public String getSkin() {
        return skin;
    }

    /**
     * Sets the {@link #getSkin()} of this object.
     *
     * @param skin the npc texture.
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /** Returns the current object with a new {@code skin}. */
    public NpcModel withSkin(String skin) {
        setSkin(skin);
        return this;
    }

    /**
     * Returns the npc skin signature.
     *
     * @return the npc skin signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the {@link #getSignature()} of this object.
     *
     * @param signature the npc signature.
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /** Returns the current object with a new {@code signature}. */
    public NpcModel withSignature(String signature) {
        setSignature(signature);
        return this;
    }

    /**
     * Returns the npc path name.
     *
     * @return the npc path name.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Sets the {@link #getPathName()} of this object.
     *
     * @param pathName the npc path name.
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /** Returns the current object with a new {@code pathName}. */
    public NpcModel withPathName(String pathName) {
        setPathName(pathName);
        return this;
    }

    /**
     * Returns the npc glow name.
     *
     * @return the npc glow name.
     */
    public String getGlowName() {
        return glowName;
    }

    /**
     * Sets the {@link #getGlowName()} of this object.
     *
     * @param glowName the npc glow name color.
     */
    public void setGlowName(String glowName) {
        this.glowName = glowName;
    }

    /**
     *  Returns the current object with a new {@code glowName}.
     */
    public NpcModel withGlowName(String glowName) {
        setGlowName(pathName);
        return this;
    }

    /**
     * Returns the npc hologram lines.
     *
     * @return the npc hologram lines.
     */
    public List<String> getHologramLines() {
        return hologramLines;
    }

    /**
     * Sets the {@link #getHologramLines()} of this object.
     *
     * @param hologramLines the npc hologram lines.
     */
    public void setHologramLines(List<String> hologramLines) {
        this.hologramLines = hologramLines;
    }

    /** Returns the current object with a new {@code hologramLines}. */
    public NpcModel withHologramLines(List<String> hologramLines) {
        setHologramLines(hologramLines);
        return this;
    }

    /**
     * Returns the npc location.
     *
     * @return the npc location.
     */
    public PluginLocation getLocation() {
        return location;
    }

    /**
     * Sets the {@link #getLocation()} of this object.
     *
     * @param location the npc location.
     */
    public void setLocation(PluginLocation location) {
        this.location = location;
    }

    /** Returns the current object with the new {@code location}. */
    public NpcModel withLocation(PluginLocation location) {
        setLocation(location);
        return this;
    }

    /**
     * Returns the npc type name.
     *
     * @return the npc type name.
     */
    public String getNpcType() {
        return npcType;
    }

    /**
     * Sets the {@link #getNpcType()} of this object.
     *
     * @param npcType the npc entity type.
     */
    public void setNpcType(String npcType) {
        this.npcType = npcType;
    }

    /**
     * Returns the current object with the new {@code npcType}.
     */
    public NpcModel withNpcType(String npcType) {
        setNpcType(npcType);
        return this;
    }

    /**
     * Returns the npc customization map.
     *
     * @return the npc customization map.
     */
    public Map<String, String[]> getCustomizationMap() {
        return customizationMap;
    }

    /**
     * Sets the {@link #getCustomizationMap()} of this object.
     *
     * @param customizationMap The npc customization.
     */
    public void setCustomizationMap(Map<String, String[]> customizationMap) {
        this.customizationMap = customizationMap;
    }

    /** Returns the current object with the new {@code customizationMap}. */
    public NpcModel withCustomizationMap(Map<String, String[]> customizationMap) {
        setCustomizationMap(customizationMap);
        return this;
    }

    /**
     * Returns the npc function values.
     *
     * @return the npc function values.
     */
    public Map<String, NpcFunctionModel> getFunctions() {
        return npcFunctions;
    }

    /**
     * Sets the {@link #getFunctions()} of this object.
     *
     * @param npcFunctions the npc function values.
     */
    public void setFunctions(Map<String, NpcFunctionModel> npcFunctions) {
        this.npcFunctions = npcFunctions;
    }

    /**
     * Returns the current object with the new {@code npcFunctions}.
     */
    public NpcModel withFunctionValues(Map<String, NpcFunctionModel> npcFunctions) {
        setFunctions(npcFunctions);
        return this;
    }

    /**
     * Required by gson.
     */
    private NpcModel() {
        hologramLines = Collections.singletonList("/znpcs lines");
        customizationMap = new HashMap<>();
        npcFunctions = new HashMap<>();
        npcFunctions.put("holo", new NpcFunctionModel(NpcFunctionContext.NULL_CONTEXT/*no data*/, true));
    }
}
