/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.deserializer;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class ZNPCDeserializer implements JsonDeserializer<ZNPC> {

    private final ServersNPC serversNPC;

    public ZNPCDeserializer(ServersNPC serversNPC) {
        this.serversNPC = serversNPC;
    }

    public ZNPC deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        HashMap<String, String> npcEquipments = ServersNPC.getGson().fromJson(jsonObject.get("npcEquipments"), HashMap.class);

        // Load npc equipment.
        EnumMap<NPCItemSlot, Material> loadMap = new EnumMap<>(NPCItemSlot.class);
        npcEquipments.forEach((s, s2) -> loadMap.put(NPCItemSlot.fromString(s), Material.getMaterial(s2)));

        ZNPC npc = new ZNPC(this.serversNPC, jsonObject.get("id").getAsInt(), jsonObject.get("lines").getAsString(), jsonObject.get("skin").getAsString(), jsonObject.get("signature").getAsString(), ServersNPC.getGson().fromJson(jsonObject.get("location"), Location.class), NPCType.fromString(jsonObject.get("npcType").getAsString()), loadMap, jsonObject.get("save").getAsBoolean());

        // Set world name.
        npc.setWorldName(jsonObject.get("location").getAsJsonObject().get("world").getAsString());

        JsonElement pathObject = jsonObject.get("pathName");
        JsonElement actionsObject = jsonObject.get("actions");

        if (pathObject != null) serversNPC.getNpcManager().getNpcPaths().stream().filter(pathReader -> pathReader.getName().equalsIgnoreCase(pathObject.getAsString())).findFirst().ifPresent(npc::setPath);
        if (actionsObject != null) npc.setActions(ServersNPC.getGson().fromJson(actionsObject, List.class)); // Load actions..

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
            npc.setCustomizationMap(ServersNPC.getGson().fromJson(customizationObject, new TypeToken<HashMap<String, List>>() {
            }.getType())); // Load actions..
        }
        return npc;
    }
}
