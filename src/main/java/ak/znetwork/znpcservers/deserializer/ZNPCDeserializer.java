package ak.znetwork.znpcservers.deserializer;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Location;
import org.bukkit.Material;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 *
 * TODO
 *  (-) Improve class
 */
public class ZNPCDeserializer implements JsonDeserializer<ZNPC> {

    public ZNPC deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        HashMap<String, String> npcEquipments = ServersNPC.GSON.fromJson(jsonObject.get("npcEquipments"), HashMap.class);

        // Load npc equipment.
        EnumMap<NPCItemSlot, Material> loadMap = new EnumMap<>(NPCItemSlot.class);
        npcEquipments.forEach((s, s2) -> loadMap.put(NPCItemSlot.fromString(s), Material.getMaterial(s2)));

        ZNPC npc = new ZNPC(jsonObject.get("id").getAsInt(), jsonObject.get("lines").getAsString(), jsonObject.get("skin").getAsString(), jsonObject.get("signature").getAsString(), ServersNPC.GSON.fromJson(jsonObject.get("location"), Location.class), NPCType.fromString(jsonObject.get("npcType").getAsString()), loadMap, jsonObject.get("save").getAsBoolean());

        // Set world name.
        npc.setWorldName(jsonObject.get("location").getAsJsonObject().get("world").getAsString());

        JsonElement pathObject = jsonObject.get("pathName");
        JsonElement actionsObject = jsonObject.get("actions");

        if (pathObject != null) npc.setPath(ZNPCPathReader.find(pathObject.getAsString()));
        if (actionsObject != null) npc.setActions(ServersNPC.GSON.fromJson(actionsObject, List.class)); // Load actions..

        npc.setHasLookAt(jsonObject.get("hasLookAt").getAsBoolean());
        npc.setHasGlow(jsonObject.get("hasGlow").getAsBoolean());
        npc.setHasMirror(jsonObject.get("hasMirror").getAsBoolean());
        npc.setHasToggleHolo(jsonObject.get("hasToggleHolo").getAsBoolean());
        npc.setHasToggleName(jsonObject.get("hasToggleName").getAsBoolean());
        npc.setReversePath(jsonObject.get("isReversePath").getAsBoolean());

        if (Utils.versionNewer(9))
            npc.toggleGlow(jsonObject.get("glowName").getAsString(), false);

        // Load Customization.
        JsonElement customizationObject = jsonObject.get("customizationMap");
        if (customizationObject != null) {
            npc.setCustomizationMap(ServersNPC.GSON.fromJson(customizationObject, new TypeToken<HashMap<String, List>>(){}.getType())); // Load actions..
        }
        return npc;
    }
}
