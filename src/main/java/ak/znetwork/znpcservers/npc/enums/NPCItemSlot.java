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
package ak.znetwork.znpcservers.npc.enums;

import lombok.Getter;

/**
 * NPC ITEM SLOT
 * <p>
 * Get slot by id
 */
public enum NPCItemSlot {
    HAND(0), HELMET(4), CHESTPLATE(3), LEGGINGS(2), BOOTS(1);

    @Getter private final int id;

    NPCItemSlot(int id) {
        this.id = id;
    }

    public static NPCItemSlot fromString(String text) {
        for (NPCItemSlot b : NPCItemSlot.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}