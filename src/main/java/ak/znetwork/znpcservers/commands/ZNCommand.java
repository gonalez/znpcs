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
package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class ZNCommand {

    public ServersNPC serversNPC;

    protected String cmd;
    protected String usage;

    protected CommandType commandType;

    public ZNCommand(final ServersNPC serversNPC , final String cmd , final String usage , CommandType commandType) {
        this.serversNPC = serversNPC;

        this.cmd = cmd;
        this.usage = usage;
        this.commandType = commandType;
    }

    public abstract boolean dispatchCommand(CommandSender sender, String... args);

    public String getCmd() {
        return cmd;
    }

    public String getUsage() {
        return usage;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void runUsage(final CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Correct usage: /znservers " + usage);
    }
}

