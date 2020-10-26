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
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CMDInfo(getArguments = {"-id" , "-skin", "-type" , "-name"})
public class CreateCommand extends ZNCommand {

    public CreateCommand(final ServersNPC serversNPC) {
        super(serversNPC , "create" , "znpcs.cmd.create", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Map<String, String> znArgumentStringMap = getAnnotations(args);

        if (znArgumentStringMap.size() <= 3) {
            sender.sendMessage(ChatColor.RED + "Correct usage: " + getUsage());
            return true;
        }

        final String id = znArgumentStringMap.get("id");

        if (!StringUtils.isNumeric(id)) {
            sender.sendMessage(ChatColor.RED + "The id of the npc must be a number.");
            return false;
        }

        int npcId = Integer.parseInt(id);

        boolean foundNPC = serversNPC.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == npcId);

        if (foundNPC) {
            sender.sendMessage(ChatColor.RED + "Have found an npc with this id, try putting a unique id..");
            return false;
        }

        final String skin = znArgumentStringMap.get("skin");

        final String type = znArgumentStringMap.get("type");
        final NPCType npcType = NPCType.fromString(type.toUpperCase());

        if (npcType == null)  {
            sender.sendMessage(ChatColor.RED + "Can't find this type of npc..");
            return false;
        }

        serversNPC.createNPC(npcId, ((Player)sender), skin, znArgumentStringMap.get("name"));
        return false;
    }
}
