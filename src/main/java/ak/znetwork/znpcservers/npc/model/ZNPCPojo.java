package ak.znetwork.znpcservers.npc.model;

import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.npc.ZNPCType;
import ak.znetwork.znpcservers.npc.ZNPCSkin;
import ak.znetwork.znpcservers.utility.location.ZLocation;

import org.bukkit.inventory.ItemStack;

import java.util.*;

import lombok.Data;

/**
 * NPC POJO model class.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Data
public class ZNPCPojo {
    /**
     * The npc identifier.
     */
    private int id;

    /**
     * Toggle variables.
     */
    private boolean hasGlow, hasLookAt, hasMirror;

    /**
     * Toggle variables.
     */
    private boolean hasToggleHolo = true;

    /**
     * NPC name y height.
     */
    private double hologramHeight;

    /**
     * The skin value & signature.
     */
    private String skin, signature;

    /**
     * The path name.
     */
    private String pathName;

    /**
     * The glow color name.
     */
    private String glowName;

    /**
     * The npc location
     */
    private ZLocation location;

    /**
     * The npc entity type.
     */
    private ZNPCType npcType;

    /**
     * The hologram lines.
     */
    private List<String> hologramLines;

    /**
     * The actions to be executed when the npc is clicked.
     */
    private final List<ZNPCAction> clickActions;

    /**
     * The npc equipment values.
     */
    private final Map<ZNPCSlot, ItemStack> npcEquip;

    /**
     * The npc customizations values.
     */
    private final Map<String, String[]> customizationMap;

    /**
     * Creates a new class with the given values.
     *
     * @param id       The npc id identifier.
     * @param lines    The npc hologram lines.
     * @param npcSkin  The npc skin values.
     * @param location The npc location.
     * @param npcType  The npc entity type.
     */
    public ZNPCPojo(int id,
                    List<String> lines,
                    ZNPCSkin npcSkin,
                    ZLocation location,
                    ZNPCType npcType) {
        this();
        this.id = id;
        this.hologramLines = lines;
        this.skin = npcSkin.getValue();
        this.signature = npcSkin.getSignature();
        this.location = location;
        this.npcType = npcType;
    }

    /**
     * Default no-args constructor, this would be used by gson,
     * initializes default variables for missing fields since gson doesn't support it.
     */
    protected ZNPCPojo() {
        hologramLines = Collections.singletonList("/znpcs lines");
        clickActions = new ArrayList<>();
        npcEquip = new HashMap<>();
        customizationMap = new HashMap<>();
    }
}
