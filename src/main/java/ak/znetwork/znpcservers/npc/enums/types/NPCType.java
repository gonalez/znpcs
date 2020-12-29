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
package ak.znetwork.znpcservers.npc.enums.types;

import ak.znetwork.znpcservers.cache.ClazzCache;
import ak.znetwork.znpcservers.utils.Utils;

import java.lang.reflect.Constructor;

public enum  NPCType {

    PLAYER(ClazzCache.ENTITY_PLAYER_CLASS,  -1, 0),
    ARMOR_STAND(ClazzCache.ENTITY_ARMOR_STAND_CLASS, -1, 0),
    CREEPER(ClazzCache.ENTITY_CREEPER_CLASS,  -1, -0.15),
    BAT(ClazzCache.ENTITY_BAT_CLASS,  -1, -0.5),

    BLAZE(ClazzCache.ENTITY_BLAZE_CLASS, -1, 0),
    CAVE_SPIDER(ClazzCache.ENTITY_CAVE_SPIDER_CLASS, -1,-1),
    COW(ClazzCache.ENTITY_COW_CLASS,  -1, -0.25),
    ENDER_DRAGON(ClazzCache.ENTITY_ENDER_DRAGON_CLASS,  -1, 1.5),
    ENDERMAN(ClazzCache.ENTITY_ENDERMAN_CLASS, -1, 0.7),
    ENDERMITE(ClazzCache.ENTITY_ENDERMITE_CLASS, -1, -1.5),
    GHAST(ClazzCache.ENTITY_GHAST_CLASS,  -1, 3),
    IRON_GOLEM(ClazzCache.ENTITY_IRON_GOLEM_CLASS,  -1, 0.75),
    GIANT(ClazzCache.ENTITY_GIANT_ZOMBIE_CLASS,  -1, 11),
    GUARDIAN(ClazzCache.ENTITY_GUARDIAN_CLASS,  -1, -0.7),
    HORSE(ClazzCache.ENTITY_HORSE_CLASS,  -1, 0),
    LLAMA(ClazzCache.ENTITY_LLAMA_CLASS, -1, 0),
    MAGMA_CUBE(ClazzCache.ENTITY_MAGMA_CUBE_CLASS, -1, -1.25),
    MOOSHROOM(ClazzCache.ENTITY_MUSHROOM_COW_CLASS, -1, -0.25),
    OCELOT(ClazzCache.ENTITY_OCELOT_CLASS,  -1, -1),
    PARROT(ClazzCache.ENTITY_PARROT_CLASS,  -1, -1.5),
    PIG(ClazzCache.ENTITY_PIG_CLASS,  -1, -1),
    ZOMBIFIED_PIGLIN(ClazzCache.ENTITY_PIG_ZOMBIE_CLASS,  -1, 0),
    POLAR_BEAR(ClazzCache.ENTITY_POLAR_BEAR_CLASS, -1, -0.5),
    SHEEP(ClazzCache.ENTITY_SHEEP_CLASS, -1, -0.5),
    SILVERFISH(ClazzCache.ENTITY_SILVERFISH_CLASS,  -1, -1.5),
    SKELETON(ClazzCache.ENTITY_SKELETON_CLASS,  -1, 0),
    SLIME(ClazzCache.ENTITY_SLIME_CLASS,  -1, -1.25),
    SPIDER(ClazzCache.ENTITY_SPIDER_CLASS,  -1, -1),
    SQUID(ClazzCache.ENTITY_SQUID_CLASS,  -1, -1),
    VILLAGER(ClazzCache.ENTITY_VILLAGER_CLASS,  -1, 0),
    WITCH(ClazzCache.ENTITY_WITCH_CLASS,  -1, 0.5),
    WITHER(ClazzCache.ENTITY_WITHER_CLASS,  -1, 1.75),
    ZOMBIE(ClazzCache.ENTITY_ZOMBIE_CLASS,  -1, 0),
    WOLF(ClazzCache.ENTITY_WOLF_CLASS,  -1, -1),
    END_CRYSTAL(ClazzCache.ENTITY_END_CRYSTAL_CLASS,  51, 0);

    public final ClazzCache aClass;
    public final String name;

    public final int id;
    public final double holoHeight;

    public Constructor<?> constructor = null;

    public Object entityType;

    NPCType(final ClazzCache aClass , final int id, final double holoHeight) {
        this.aClass = aClass;

        this.name = this.name();

        this.holoHeight = holoHeight;
        this.id = id;
    }

    public void load() {
        if (aClass.aClass != null) {
            try {
                this.constructor = (id < 0 ? ClazzCache.PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.aClass.getConstructor(ClazzCache.ENTITY_LIVING_CLASS.aClass) : ClazzCache.PACKET_PLAY_OUT_ENTITY_SPAWN_CLASS.aClass.getConstructor(ClazzCache.ENTITY_CLASS.aClass , int.class));

                if (Utils.isVersionNewestThan(13)) {
                    entityType = ClazzCache.ENTITY_TYPES_CLASS.aClass.getField(name()).get(null);
                }
            } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Couldn't load entity " + name, e);
            }
        }
    }

    public static NPCType fromString(String text) {
        for (NPCType b : NPCType.values()) {
            if (text != null && b.name().toUpperCase().equalsIgnoreCase(text.toUpperCase())) {
                return b;
            }
        }
        return null;
    }
}
