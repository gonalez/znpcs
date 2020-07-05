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
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class ToggleCommand extends ZNCommand {

    public ToggleCommand(final ServersNPC serversNPC) {
        super(serversNPC , "toggle" , "toggle <holo:name:glow:look>" ,"znpcs.cmd.toggle", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Player player = (Player) sender;

        if (args.length == 2 && ((args[1]).equalsIgnoreCase("name") || (args[1]).equalsIgnoreCase("look")) || (args[1]).equalsIgnoreCase("holo") || (args[1]).equalsIgnoreCase("glow")) {
            final NPC npc = serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getLocation().getWorld() == player.getWorld() && npc1.getLocation().distanceSquared(player.getLocation()) <= 20D).min(Comparator.comparing(npc1 -> npc1.getLocation().distanceSquared(player.getLocation()))).orElse(null);

            if (npc == null) {
                player.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("npc-not-found")));
                return true;
            }

            switch (args[1]) {
                case "name":
                    npc.toggleName();
                    break;
                case "holo":
                    npc.toggleHolo();
                    break;
                case  "look":
                    npc.toggleLookAt();
                    break;
                case  "glow":
                    npc.toggleGlow();
                    break;
            }

            player.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
            return true;
        }
        runUsage(sender);
        return false;
    }
}
