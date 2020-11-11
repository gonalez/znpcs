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
package ak.znetwork.znpcservers.serializer;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.utils.LocationUtils;
import ak.znetwork.znpcservers.utils.Utils;
import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NPCSerializer implements JsonSerializer<NPC>, JsonDeserializer<NPC> {

    protected final ServersNPC serversNPC;

    public NPCSerializer(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;
    }

    @Override
    public JsonElement serialize(NPC npc, Type type, JsonSerializationContext jsonSerializationContext) {
        final JsonObject obj = new JsonObject();

        obj.addProperty("id" , npc.getId());
        obj.addProperty("type" , npc.getNpcType().name());

        obj.addProperty("skin" ,npc.getSkin() + ":" + npc.getSignature());

        obj.addProperty("location" , LocationUtils.getStringLocation(npc.getLocation()));

        obj.addProperty("lines" , npc.getHologram().getLinesFormatted());

        if (npc.getActions() != null) obj.add("actions" , new Gson().toJsonTree(npc.getActions()).getAsJsonArray());

        npc.getNpcItemSlotMaterialHashMap().forEach((npcItemSlot, material) -> obj.addProperty("equipment." + npcItemSlot.name(), material.name()));

        obj.addProperty("toggle.holo" , npc.isHasToggleHolo());
        obj.addProperty("toggle.look" ,npc.isHasLookAt());
        obj.addProperty("toggle.name" , npc.isHasToggleName());
        obj.addProperty("toggle.mirror" , npc.isHasMirror());
        obj.addProperty("toggle.glow.has" , npc.isHasGlow());
        obj.addProperty("toggle.glow.color" , (npc.getGlowName() != null ? npc.getGlowName().toUpperCase() : "WHITE"));
        return obj;
    }

    @Override
    public NPC deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        final int id = jsonObject.get("id").getAsInt();
        try {
            final Location location = LocationUtils.getLocationString(jsonObject.get("location").getAsString());

            if (Utils.containsStep(location)) location.add(0, 0.5, 0);

            final NPC npc = new NPC(this.serversNPC , id, jsonObject.get("skin").getAsString().split(":")[0], jsonObject.get("skin").getAsString().split(":")[1], location, NPCType.valueOf(jsonObject.get("type").getAsString()), new Hologram(this.serversNPC,  LocationUtils.getLocationString(jsonObject.get("location").getAsString()), jsonObject.get("lines").getAsString().split(":")) , true);

            if (jsonObject.get("actions").isJsonArray()) npc.setActions(StreamSupport.stream(jsonObject.get("actions").getAsJsonArray().spliterator(), false).map(JsonElement::getAsString).collect(Collectors.toList()));

            npc.setHasToggleHolo(jsonObject.get("toggle.holo").getAsBoolean());
            npc.setHasLookAt(jsonObject.get("toggle.look").getAsBoolean());
            npc.setHasToggleName(jsonObject.get("toggle.name").getAsBoolean());
            npc.setHasMirror(jsonObject.get("toggle.mirror").getAsBoolean());
            npc.setHasGlow(jsonObject.get("toggle.glow.has").getAsBoolean());

            if (npc.isHasGlow()) npc.toggleGlow(null, jsonObject.get("toggle.glow.color").getAsString(), false);

            Stream.of(NPC.NPCItemSlot.values()).map(Enum::name).filter(s -> jsonObject.get("equipment." + s) != null).forEach(s -> {
                try {
                    npc.equip(null, NPC.NPCItemSlot.fromString(s), Material.getMaterial(jsonObject.get("equipment." + s).getAsString()));
                } catch (Exception e) {}
            });

            return npc;
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while trying to deserialize npc " +  id, e);
        }
    }
}
