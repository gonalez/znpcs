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
import ak.znetwork.znpcservers.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@CMDInfo(getArguments = {"-id"})
public class DeleteCommand extends ZNCommand {

    public DeleteCommand(final ServersNPC serversNPC) {
        super(serversNPC , "delete" , "znpcs.cmd.delete", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Map<String, String> znArgumentStringMap = getAnnotations(args);

        if (znArgumentStringMap.size() <= 0) {
            sender.sendMessage(ChatColor.RED + "Correct usage: " + getUsage());
            return true;
        }

        final String id = znArgumentStringMap.get("id").trim();

        if (!StringUtils.isNumeric(id)) {
            sender.sendMessage(ChatColor.RED + "The id of the npc must be a number.");
            return false;
        }

        int npcId = Integer.parseInt(id);

        boolean foundNPC = serversNPC.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == npcId);

        if (!foundNPC) {
            sender.sendMessage(ChatColor.RED + "Can't find this npc with this id, try putting a valid id..");
            return false;
        }

        try {
            serversNPC.deleteNPC(npcId);

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Unable to delete this npc.");
        }
        return false;
    }
}
