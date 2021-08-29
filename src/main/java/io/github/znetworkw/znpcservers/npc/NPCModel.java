package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.npc.conversation.ConversationModel;
import io.github.znetworkw.znpcservers.utility.location.ZLocation;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Contains information about a {@link NPC}.
 */
public class NPCModel {
    private static final String EMPTY_STRING = "";
    /**
     * The npc id.
     * <p>
     * This id is used to identify a npc for this model.
     */
    private int id;
    /** The npc hologram height. */
    private double hologramHeight;
    /** The texture & signature for the npc skin. */
    private String skin, signature="";
    /** The npc path name. */
    private String pathName;
    /** The name for the glow color. */
    private String glowName;
    /** The npc conversation. */
    private ConversationModel conversation;
    /** The npc location. */
    private ZLocation location;
    /** The entity type for the npc. */
    private NPCType npcType;
    /** The npc hologram lines. */
    private List<String> hologramLines;
    /** The actions to be executed when the npc is interacted. */
    private List<NPCAction> clickActions;
    /** The npc equipment. */
    private Map<ItemSlot, ItemStack> npcEquip;
    /** The npc toggle values. */
    private Map<String, Boolean> npcFunctions;
    /** The npc customization. */
    private Map<String, String[]> customizationMap;

    public NPCModel(int id) {
        this();
        this.id = id;
        skin = EMPTY_STRING;
        signature = EMPTY_STRING;
        npcType = NPCType.PLAYER;
    }

    /**
     * Default no-args constructor, this would be used by gson. Initializes variables
     * for missing fields since gson doesn't support it.
     */
    private NPCModel() {
        hologramLines = Collections.singletonList("/znpcs lines");
        clickActions = new ArrayList<>();
        npcEquip = new HashMap<>();
        customizationMap = new HashMap<>();
        npcFunctions = new HashMap<>();
        npcFunctions.put("holo", true);
    }

    /**
     * Returns the npc id identifier.
     *
     * @return The npc identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the {@link #getId()} of this object.
     *
     * @param id The npc identifier.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the current object with the new {@code id}.
     */
    public NPCModel withId(int id) {
        setId(id);
        return this;
    }

    /**
     * Returns the npc hologram height.
     *
     * @return The npc hologram height.
     */
    public double getHologramHeight() {
        return hologramHeight;
    }

    /**
     * Sets the {@link #getHologramHeight()} of this object.
     *
     * @param hologramHeight The npc hologram height.
     */
    public void setHologramHeight(double hologramHeight) {
        this.hologramHeight = hologramHeight;
    }

    /**
     * Returns the current object with a new {@code hologramHeight}.
     */
    public NPCModel withHologramHeight(double hologramHeight) {
        setHologramHeight(hologramHeight);
        return this;
    }

    /**
     * Returns the npc skin texture.
     *
     * @return The npc skin texture.
     */
    public String getSkin() {
        return skin;
    }

    /**
     * Sets the {@link #getSkin()} of this object.
     *
     * @param skin The npc texture.
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * Returns the current object with a new {@code skin}.
     */
    public NPCModel withSkin(String skin) {
        setSkin(skin);
        return this;
    }

    /**
     * Returns the npc skin signature.
     *
     * @return The npc skin signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the {@link #getSignature()} of this object.
     *
     * @param signature The npc signature.
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /**
     * Returns the current object with a new {@code signature}.
     */
    public NPCModel withSignature(String signature) {
        setSignature(signature);
        return this;
    }

    /**
     * Returns the npc path name.
     *
     * @return The npc path name.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * Sets the {@link #getPathName()} of this object.
     *
     * @param pathName The npc path name.
     */
    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    /**
     * Returns the current object with a new {@code pathName}.
     */
    public NPCModel withPathName(String pathName) {
        setPathName(pathName);
        return this;
    }

    /**
     * Returns the npc glow name.
     *
     * @return The npc glow name.
     */
    public String getGlowName() {
        return glowName;
    }

    /**
     * Sets the {@link #getGlowName()} of this object.
     *
     * @param glowName The npc glow name color.
     */
    public void setGlowName(String glowName) {
        this.glowName = glowName;
    }

    /**
     * Returns the current object with a new {@code glowName}.
     */
    public NPCModel withGlowName(String glowName) {
        setGlowName(pathName);
        return this;
    }

    /**
     * Returns the npc conversation.
     *
     * @return The npc conversation.
     */
    public ConversationModel getConversation() {
        return conversation;
    }

    /**
     * Sets the {@link #getConversation()} of this object.
     *
     * @param conversation The npc conversation.
     */
    public void setConversation(ConversationModel conversation) {
        this.conversation = conversation;
    }

    /**
     * Returns the current object with a new {@code conversation}.
     */
    public NPCModel withConversation(ConversationModel conversation) {
        setConversation(conversation);
        return this;
    }

    /**
     * Returns the npc hologram lines.
     *
     * @return The npc hologram lines.
     */
    public List<String> getHologramLines() {
        return hologramLines;
    }


    /**
     * Sets the {@link #getHologramLines()} of this object.
     *
     * @param hologramLines The npc hologram lines.
     */
    public void setHologramLines(List<String> hologramLines) {
        this.hologramLines = hologramLines;
    }

    /**
     * Returns the current object with a new {@code hologramLines}.
     */
    public NPCModel withHologramLines(List<String> hologramLines) {
        setHologramLines(hologramLines);
        return this;
    }

    /**
     * Returns the npc location.
     *
     * @return The npc location.
     */
    public ZLocation getLocation() {
        return location;
    }

    /**
     * Sets the {@link #getLocation()} of this object.
     *
     * @param location The npc location.
     */
    public void setLocation(ZLocation location) {
        this.location = location;
    }

    /**
     * Returns the current object with the new {@code location}.
     */
    public NPCModel withLocation(ZLocation location) {
        setLocation(location);
        return this;
    }

    /**
     * Returns the npc type.
     *
     * @return The npc type.
     */
    public NPCType getNpcType() {
        return npcType;
    }

    /**
     * Sets the {@link #getNpcType()} of this object.
     *
     * @param npcType The npc entity type.
     */
    public void setNpcType(NPCType npcType) {
        this.npcType = npcType;
    }

    /**
     * Returns the current object with the new {@code npcType}.
     */
    public NPCModel withNpcType(NPCType npcType) {
        setNpcType(npcType);
        return this;
    }

    /**
     * Returns the npc click actions.
     *
     * @return The npc click actions.
     */
    public List<NPCAction> getClickActions() {
        return clickActions;
    }

    /**
     * Sets the {@link #getClickActions()} of this object.
     *
     * @param clickActions The npc click actions.
     */
    public void setClickActions(List<NPCAction> clickActions) {
        this.clickActions = clickActions;
    }

    /**
     * Returns the current object with the new {@code clickActions}.
     */
    public NPCModel withClickActions(List<NPCAction> clickActions) {
        setClickActions(clickActions);
        return this;
    }

    /**
     * Returns the npc equipment map.
     *
     * @return The npc equipment map.
     */
    public Map<ItemSlot, ItemStack> getNpcEquip() {
        return npcEquip;
    }

    /**
     * Sets the {@link #getNpcEquip()} of this object.
     *
     * @param npcEquip The npc equipment.
     */
    public void setNpcEquip(Map<ItemSlot, ItemStack> npcEquip) {
        this.npcEquip = npcEquip;
    }

    /**
     * Returns the current object with the new {@code npcEquip}.
     */
    public NPCModel withNpcEquip(Map<ItemSlot, ItemStack> npcEquip) {
        setNpcEquip(npcEquip);
        return this;
    }

    /**
     * Returns the npc customization map.
     *
     * @return The npc customization map.
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

    /**
     * Returns the current object with the new {@code customizationMap}.
     */
    public NPCModel withCustomizationMap(Map<String, String[]> customizationMap) {
        setCustomizationMap(customizationMap);
        return this;
    }

    /**
     * Returns the npc function values.
     *
     * @return The npc function values.
     */
    public Map<String, Boolean> getFunctions() {
        return npcFunctions;
    }

    /**
     * Sets the {@link #getFunctions()} of this object.
     *
     * @param npcFunctions The npc function values.
     */
    public void setFunctions(Map<String, Boolean> npcFunctions) {
        this.npcFunctions = npcFunctions;
    }

    /**
     * Returns the current object with the new {@code npcFunctions}.
     */
    public NPCModel withFunctionValues(Map<String, Boolean> npcFunctions) {
        setFunctions(npcFunctions);
        return this;
    }
}
