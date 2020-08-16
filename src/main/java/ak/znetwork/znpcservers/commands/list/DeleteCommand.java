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
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCommand extends ZNCommand {

    public DeleteCommand(final ServersNPC serversNPC) {
        super(serversNPC , "delete" , "delete <id>" , "znpcs.cmd.delete", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        switch (args.length) {
            case 2:
                if (serversNPC.getNpcManager().getNpcs().stream().noneMatch(npc -> npc.getId() == Integer.parseInt(args[1]))) {
                    sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("npc-not-found")));
                    return true;
                }

                try {
                    serversNPC.deleteNPC(Integer.parseInt(args[1]));

                    sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
                } catch (Exception e) {
                    throw new RuntimeException("An exception occurred while trying to delete npc", e);
                }
                return true;
            default:
                runUsage(sender);
                return false;
        }
    }
}
