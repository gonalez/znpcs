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

@CMDInfo(getArguments = {})
public class ListCommand extends ZNCommand {

    public ListCommand(final ServersNPC serversNPC) {
        super(serversNPC , "list" , "znpcs.cmd.list", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        if (serversNPC.getNpcManager().getNpcs().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No NPC found.");
        } else serversNPC.getNpcManager().getNpcs().forEach(npc -> sender.sendMessage(Utils.color("&f&l * &a" + npc.getId() + " " + npc.getHologram().getLinesFormatted() + " &7(&e" + npc.getLocation().getWorld().getName() + " " + npc.getLocation().getBlockX() + " " + npc.getLocation().getBlockY() + " " + npc.getLocation().getBlockZ() + "&7)")));
        return false;
    }
}
