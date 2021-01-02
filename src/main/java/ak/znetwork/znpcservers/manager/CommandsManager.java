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
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

public class CommandsManager implements CommandExecutor {

    private final ServersNPC serversNPC;

    private LinkedHashSet<ZNCommand> znCommands;

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
        try {
            final Optional<ZNCommand> znCommand = this.getZnCommands().stream().findFirst();

            if (znCommand.isPresent()) znCommand.get().execute(sender, args);
        } catch (CommandExecuteException e) {
            serversNPC.messages.sendMessage(sender, ZNConfigValue.COMMAND_ERROR);

            // if logs enabled
            e.printStackTrace();
        } catch (CommandPermissionException e) {
            serversNPC.messages.sendMessage(sender, ZNConfigValue.NO_PERMISSION);
        } catch (CommandNotFoundException e) {
            serversNPC.messages.sendMessage(sender, ZNConfigValue.COMMAND_NOT_FOUND);
        }
        return true;
    }
}
