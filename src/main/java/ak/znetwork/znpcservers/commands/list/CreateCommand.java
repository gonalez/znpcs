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
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends ZNCommand {

    public CreateCommand(final ServersNPC serversNPC) {
        super(serversNPC , "create" , "create <id> <skin> <text... text>" , "znpcs.cmd.create", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        if (args.length >= 3 && Utils.isInteger(args[1])) {
            if (serversNPC.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == Integer.parseInt(args[1])))  {
                sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("npc-found")));
                return true;
            }

            final StringBuilder stringBuilder = new StringBuilder();

            for (int i=3; i<=args.length - 1; i++)
                stringBuilder.append(args[i]).append(":");

            serversNPC.createNPC(Integer.parseInt(args[1]) , ((Player)sender) ,args[2], stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
            return true;
        }
        runUsage(sender);
        return false;
    }
}
