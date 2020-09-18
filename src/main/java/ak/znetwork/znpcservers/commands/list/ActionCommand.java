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
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
TODO: Optimize more
 */
public class ActionCommand extends ZNCommand {

    public ActionCommand(final ServersNPC serversNPC) {
        super(serversNPC , "action" , "action <id> <add:remove:cooldown:list>" , "znpcs.cmd.action" , CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Player player = (Player) sender;

        if (args.length >= 3) {
            if (!Utils.isInteger(args[1])) return false;

            final NPC npc = this.serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getId() == Integer.parseInt(args[1])).findFirst().orElse(null);

            if (npc == null) {
                player.sendMessage(ChatColor.RED + "NPC not found.");
                return false;
            }

            switch (args[2]) {
                case "add":
                    if (args.length <= 4) sender.sendMessage(ChatColor.RED + "Correct usage: <add> <cmd:console:server> <action>");
                    else if (NPCAction.fromString(args[3]) == null) sender.sendMessage(ChatColor.RED + "Insert a valid action type.");
                    else {
                        npc.getActions().add(NPCAction.fromString(args[3]).name() + ":" + Arrays.stream(args, 4, args.length).collect(Collectors.joining(" ")));                            sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
                    }
                    break;
                case "cooldown":
                    if (args.length <= 4) sender.sendMessage(ChatColor.RED + "Correct usage: <cooldown> <action_id> <seconds>");
                    else if (npc.getActions().isEmpty() || Integer.parseInt(args[3]) > npc.getActions().size() - 1) sender.sendMessage(ChatColor.RED +  "Action not found.");
                    else {
                        if (!Utils.isInteger(args[4])) player.sendMessage(ChatColor.RED + "You need to put a valid number");
                        else {
                            int action = Integer.parseInt(args[3]);

                            String value = npc.getActions().get(action);
                            String[] keys = value.split(":");

                            if (keys.length > 2) keys = Arrays.copyOf(keys, keys.length - 1);

                            npc.getActions().set(action, String.join(":", keys) + ":" + Integer.parseInt(args[4]));
                            sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
                        }
                    }
                    break;
                case "remove":
                    if (!Utils.isInteger(args[3])) player.sendMessage(ChatColor.RED + "You need to put a valid number");
                    else {
                        if (npc.getActions().isEmpty() || Integer.parseInt(args[3]) > npc.getActions().size() - 1) player.sendMessage(ChatColor.RED + "Action not found.");
                        else {
                            npc.getActions().remove(Integer.parseInt(args[3]));

                            sender.sendMessage(Utils.tocolor(serversNPC.getMessages().getConfig().getString("success")));
                        }
                    }
                    break;
                case "list":
                    if (npc.getActions().isEmpty()) player.sendMessage(ChatColor.GREEN + "No actions found.");
                    else npc.getActions().forEach(s -> player.sendMessage(Utils.tocolor("&8(&a" + npc.getActions().indexOf(s) + "&8) &6" + s)));
                    break;
                default:
                    runUsage(sender);
                    break;
            }
            return true;
        }
        runUsage(sender);
        return false;
    }
}
