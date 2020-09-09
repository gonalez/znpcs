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
import com.google.gson.*;

import java.lang.reflect.Type;

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
        obj.addProperty("action" , npc.getNpcAction().name());

        obj.addProperty("skin" , npc.getSkin() + ":" + npc.getSignature());

        obj.addProperty("location" ,  LocationUtils.getStringLocation(npc.getLocation()));

        obj.addProperty("lines" , npc.getHologram().getLinesFormatted());
        obj.addProperty("actions" , (npc.getActions() != null ? String.join(":", npc.getActions()) : ""));

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
            final NPC npc = new NPC(this.serversNPC , id, jsonObject.get("skin").getAsString().split(":")[0], jsonObject.get("skin").getAsString().split(":")[1], LocationUtils.getLocationString(jsonObject.get("location").getAsString()), NPCType.valueOf(jsonObject.get("type").getAsString()), NPCAction.fromString(jsonObject.get("action").getAsString()), new Hologram(this.serversNPC,  LocationUtils.getLocationString(jsonObject.get("location").getAsString()), jsonObject.get("lines").getAsString().split(":")) , true);

            npc.setAction(jsonObject.get("actions").getAsString().split(":"));
            npc.setHasToggleHolo(jsonObject.get("toggle.holo").getAsBoolean());
            npc.setHasLookAt(jsonObject.get("toggle.look").getAsBoolean());
            npc.setHasToggleName(jsonObject.get("toggle.name").getAsBoolean());
            npc.setHasMirror(jsonObject.get("toggle.mirror").getAsBoolean());
            npc.setHasGlow(jsonObject.get("toggle.glow.has").getAsBoolean());

            if (npc.isHasGlow()) npc.toggleGlow(null, jsonObject.get("toggle.glow.color").getAsString(), false);

            return npc;
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while trying to deserialize npc " +  id, e);
        }
    }
}
