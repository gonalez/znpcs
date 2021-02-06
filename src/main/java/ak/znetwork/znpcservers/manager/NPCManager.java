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
package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import com.google.common.collect.Lists;

import java.util.LinkedHashSet;
import java.util.List;

public final class NPCManager {

    private final LinkedHashSet<ZNPC> npcList;

    private final List<ZNPCPathReader> npcPaths;

    public NPCManager() {
        this.npcList = new LinkedHashSet<>();

        this.npcPaths = Lists.newArrayList();
    }

    public List<ZNPCPathReader> getNPCPaths() {
        return npcPaths;
    }

    public LinkedHashSet<ZNPC> getNPCs() {
        return npcList;
    }
}
