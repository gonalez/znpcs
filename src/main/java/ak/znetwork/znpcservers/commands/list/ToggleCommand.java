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
import ak.znetwork.znpcservers.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@CMDInfo(autoFill = true, getArguments = {"-id" , "-holo" , "-name" , "-glow", "-mirror", "-look"})
public class ToggleCommand extends ZNCommand {

    public ToggleCommand(final ServersNPC serversNPC) {
        super(serversNPC , "toggle" , "znpcs.cmd.toggle", CommandType.PLAYER);
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

        znArgumentStringMap.forEach((key, value) -> {
            try {
                switch (key.toLowerCase().trim()) {
                    case "holo":
                        npcOptional.get().toggleHolo();
                        break;
                    case "name":
                        npcOptional.get().toggleName(true);
                        break;
                    case "glow":
                        final String[] split = value.split(" ");

                        if (split.length <= 1) {
                            return;
                        }

                        npcOptional.get().toggleGlow(((Player)sender), split[1],true);
                        break;
                    case "mirror":
                        npcOptional.get().toggleMirror();
                        break;
                    case "look":
                        npcOptional.get().toggleLookAt();
                        break;
                    default:break;
                }
            } catch (Exception exception) {
                //Bukkit.getLogger().log(Level.WARNING , "name", exception);
                sender.sendMessage(ChatColor.RED + "An error occurred.");
            }
        });
        sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        return false;
    }
}
