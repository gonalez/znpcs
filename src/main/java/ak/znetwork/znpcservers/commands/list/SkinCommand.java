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
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.utils.JSONUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ExecutionException;

public class SkinCommand extends ZNCommand {

    public SkinCommand(final ServersNPC serversNPC) {
        super(serversNPC , "skin" , "skin <id> <name>" , "znpcs.cmd.skin", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        switch (args.length) {
            case 3:
                final NPC npc = serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getId() == Integer.parseInt(args[1])).findFirst().orElse(null);

                if (npc == null) {
                    sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("npc-not-found")));
                    return true;
                }

                final SkinFetch skinFetcher = JSONUtils.getSkin(args[2]);

                if (skinFetcher == null) {
                    sender.sendMessage(Utils.tocolor("&cError!"));
                    return true;
                }

                npc.updateSkin(skinFetcher);
                sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
                return true;
            default:
                runUsage(sender);
                return false;
        }
    }
}
