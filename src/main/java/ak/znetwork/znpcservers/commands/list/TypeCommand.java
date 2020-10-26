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
package ak.znetwork.znpcservers.commands.list;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.annotations.CMDInfo;
import ak.znetwork.znpcservers.commands.enums.CommandType;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@CMDInfo(getArguments = {"-id" , "-type"})
public class TypeCommand extends ZNCommand {

    public TypeCommand(final ServersNPC serversNPC) {
        super(serversNPC , "type" , "znpcs.cmd.type", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Map<String, String> znArgumentStringMap = getAnnotations(args);

        if (znArgumentStringMap.size() <= 1) {
            sender.sendMessage(ChatColor.RED + "Correct usage: " + getUsage());
            return true;
        }

        final String id = znArgumentStringMap.get("id").trim();

        if (!StringUtils.isNumeric(id)) {
            sender.sendMessage(ChatColor.RED + "The id of the npc must be a number.");
            return false;
        }

        int npcId = Integer.parseInt(id);

        final Optional<NPC> npcOptional = serversNPC.getNpcManager().getNpcs().stream().filter(npc -> npc.getId() == npcId).findFirst();

        if (!npcOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Can't find this npc with this id, try putting a valid id..");
            return false;
        }

        final NPCType npcType = NPCType.fromString(znArgumentStringMap.get("type").trim().toUpperCase());

        if (npcType == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not found");
            sender.sendMessage(ChatColor.GOLD + "Valid Types:");

            for (NPCType npcTypes : NPCType.values()) sender.sendMessage(ChatColor.RED + npcTypes.name);
            return true;
        }

        if (npcType.constructor == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not available for your current version.");
            return true;
        }

        try {
            npcOptional.get().changeType(npcType);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred when trying to change npc type.");
        }
        return false;
    }
}
