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

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.enums.CommandType;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class CommandsManager implements CommandExecutor {

    protected final ServersNPC serversNPC;

    protected LinkedHashSet<ZNCommand> znCommands;

    public CommandsManager(final String cmd , final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;
        this.serversNPC.getCommand(cmd).setExecutor(this);
        this.znCommands = new LinkedHashSet<>();
    }

    /**
     * Create a new command
     *
     * @param znCommand add command
     */
    public final void addCommands(final ZNCommand... znCommand) {
        znCommands.addAll(Arrays.asList(znCommand));
    }

    public LinkedHashSet<ZNCommand> getZnCommands() {
        return znCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg ,String[] args) {
        final ZNCommand znCommand = (args.length >= 1 ? znCommands.stream().filter(znCommand1 -> znCommand1.getCommandType() == CommandType.PLAYER && sender instanceof Player && znCommand1.getCmd().equalsIgnoreCase(args[0])).findFirst().orElse(null) : znCommands.stream().filter(znCommand1 -> znCommand1.getCmd().length() <= 0).findFirst().orElse(null));

        if (znCommand == null)
            sender.sendMessage(ChatColor.RED + "Command not found.");
        else {
            if (znCommand.getPermission().length() >= 1 && !sender.hasPermission(znCommand.getPermission())) {
                sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("no-permission")));
                return false;
            }

            try {
                znCommand.dispatchCommand(sender , args);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Unable to run command.");
            }
        }
        return true;
    }
}
