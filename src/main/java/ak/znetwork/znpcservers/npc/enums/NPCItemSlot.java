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

/**
 * NPC ITEM SLOT
 *
 * Get slot by id
 */
public enum NPCItemSlot  {
    HAND(0 , 0) , HELMET(4 , 5) , CHESTPLATE(3 , 4) , LEGGINGS(2 , 3) , BOOTS(1 ,  2);

    int id;
    int newerv;

    NPCItemSlot(int id , int newerv) {
        this.id = id;
        this.newerv = newerv;
    }

    public int getId() {
        return id;
    }

    public int getNewerv() {
        return newerv;
    }

    public static NPCItemSlot fromString(String text) {
        for (NPCItemSlot b : NPCItemSlot.values()) {
            if (b.name().toUpperCase().equalsIgnoreCase(text.toUpperCase())) {
                return b;
            }
        }
        return null;
    }
}