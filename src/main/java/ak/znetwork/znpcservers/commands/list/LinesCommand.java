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
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class LinesCommand extends ZNCommand {

    public LinesCommand(final ServersNPC serversNPC) {
        super(serversNPC , "lines" , "lines <text... text>" , "znpcs.cmd.lines" ,CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Player player = (Player) sender;

        if (args.length >= 2) {
            final NPC npc = serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getLocation().getWorld() == player.getWorld() && npc1.getLocation().distanceSquared(player.getLocation()) <= 20D).min(Comparator.comparing(npc1 -> npc1.getLocation().distanceSquared(player.getLocation()))).orElse(null);

            if (npc == null) {
                player.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("npc-not-found")));
                return true;
            }

            final StringBuilder stringBuilder = new StringBuilder();

            for (int i=1; i<=args.length - 1; i++)
                stringBuilder.append(args[i]).append(":");

            final String toString = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();

            final String[] strings = new String[toString.split(":").length];

            for (int i=0; i <= strings.length - 1; i++)
                strings[i] = toString.split(":")[i];

            Collections.reverse(Arrays.asList(strings));

            npc.getHologram().lines = strings;
            npc.getHologram().createHolos();

            player.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
            return true;
        }
        runUsage(sender);
        return false;
    }
}
