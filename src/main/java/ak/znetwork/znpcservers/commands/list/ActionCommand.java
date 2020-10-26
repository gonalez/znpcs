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
import ak.znetwork.znpcservers.commands.annotations.CMDInfo;
import ak.znetwork.znpcservers.commands.enums.CommandType;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@CMDInfo(getArguments = {"-id" , "-add" , "-remove" , "-cooldown" , "-list"})
public class ActionCommand extends ZNCommand {

    public ActionCommand(final ServersNPC serversNPC) {
        super(serversNPC , "action" , "znpcs.cmd.action", CommandType.PLAYER);
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String... args) {
        final Map<String, String> znArgumentStringMap = getAnnotations(args);

        if (znArgumentStringMap.size() <= 1) {
            sender.sendMessage(ChatColor.RED + "Correct usage: " + getUsage());
            return true;
        }

        final String id = znArgumentStringMap.get("id").trim();

        if (!StringUtils.isNumeric(id)) {
            sender.sendMessage(ChatColor.RED + "The id of the npc must be a number.");
            return false;
        }

        int npcId = Integer.parseInt(id);

        final Optional<NPC> npcOptional = serversNPC.getNpcManager().getNpcs().stream().filter(npc -> npc.getId() == npcId).findFirst();

        if (!npcOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Can't find this npc with this id, try putting a valid id..");
            return false;
        }

        AtomicInteger i = new AtomicInteger();
        znArgumentStringMap.forEach((key, value) -> {
            final String[] split = value.split(" ");

            final NPCAction npcAction = NPCAction.fromString(split[0].trim().toUpperCase());

            switch (key.trim().toLowerCase()) {
                case "add":
                    if (split.length <= 1) sender.sendMessage(ChatColor.RED + "Correct usage -> <CMD:CONSOLE:SERVER> <action>");
                    else {
                        if (npcAction == null) sender.sendMessage(ChatColor.RED + "Action type not found.");
                        else {
                            String[] keys = Arrays.copyOfRange(split, 1, split.length);

                            npcOptional.get().getActions().add(npcAction.name() + ":" + String.join(" ", keys));
                            i.getAndIncrement();
                        }
                    }
                    break;
                case "remove":
                    if (!Utils.isInteger(value.trim())) sender.sendMessage(ChatColor.RED + "You need to put a valid number.");
                    else {
                        if (npcOptional.get().getActions().isEmpty() || Integer.parseInt(args[3]) > npcOptional.get().getActions().size() - 1) sender.sendMessage(ChatColor.RED + "Action not found.");
                        else {
                            npcOptional.get().getActions().remove(Integer.parseInt(args[3]));
                            i.getAndIncrement();
                        }
                    }
                    break;
                case "cooldown":
                    if (split.length <= 1) sender.sendMessage(ChatColor.RED + "Correct usage -> <action_id> <seconds>");
                    else {
                        if (!Utils.isInteger(split[0]) || !Utils.isInteger(split[1])) sender.sendMessage(ChatColor.RED + "You need to put a valid number");
                        else {
                            if (npcOptional.get().getActions().isEmpty() || Integer.parseInt(split[0]) > npcOptional.get().getActions().size() - 1) sender.sendMessage(ChatColor.RED +  "Action not found.");
                            else {
                                int action = Integer.parseInt(split[0]);
                                int seconds = Integer.parseInt(split[1]);

                                final String[] strings = npcOptional.get().getActions().get(action).split(":");

                                npcOptional.get().getActions().set(action, String.join(":", Arrays.copyOfRange(strings, 0, 2))  + ":" + seconds);
                                i.getAndIncrement();
                            }
                        }
                    }
                    break;
                case "list":
                    if (npcOptional.get().getActions().isEmpty()) sender.sendMessage(ChatColor.GREEN + "No actions found.");
                    else npcOptional.get().getActions().forEach(s -> sender.sendMessage(Utils.color("&8(&a" + npcOptional.get().getActions().indexOf(s) + "&8) &6" + s)));
                    break;
                default:break;
            }
        });
        if (i.get() > 0) sender.sendMessage(ChatColor.GREEN + "Added/Modified " + ChatColor.GOLD + i.get() + ChatColor.GREEN + " actions.");
        return false;
    }
}
