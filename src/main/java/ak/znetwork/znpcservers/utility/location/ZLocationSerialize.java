package ak.znetwork.znpcservers.utility.location;

import java.lang.reflect.Type;

import com.google.gson.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ZLocationSerialize implements JsonSerializer<ZLocation>, JsonDeserializer<ZLocation> {

    @Override
    public JsonElement serialize(ZLocation src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", src.getWorld());
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("y", src.getY());
        jsonObject.addProperty("z", src.getZ());
        jsonObject.addProperty("yaw", src.getYaw());
        jsonObject.addProperty("pitch", src.getPitch());
        return jsonObject;
    }

    @Override
    public ZLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        return new ZLocation(jsonObject.get("world").getAsString(),
                jsonObject.get("x").getAsDouble(),
                jsonObject.get("y").getAsDouble(),
                jsonObject.get("z").getAsDouble(),
                jsonObject.get("yaw").getAsFloat(),
                jsonObject.get("pitch").getAsFloat()
        );
    }
}
