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
import ak.znetwork.znpcservers.commands.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DefaultCommand extends ZNCommand {

    public DefaultCommand(final ServersNPC serversNPC) {
        super(serversNPC,"" , "" ,"", CommandType.ALL);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        sender.sendMessage(ChatColor.GREEN + "ZNPCS BY ZNETWORK <3 " + ChatColor.GOLD + "(https://www.spigotmc.org/members/znetwork.156341/)");
        sender.sendMessage(" ");
        serversNPC.getCommandsManager().getZnCommands().forEach(znCommand -> sender.sendMessage(ChatColor.RED + "/znpcs " + znCommand.getUsage()));
        return true;
    }
}
