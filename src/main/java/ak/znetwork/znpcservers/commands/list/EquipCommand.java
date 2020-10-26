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
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@CMDInfo(getArguments = {"-id" , "-hand" , "-helmet" , "-chestplate" , "-leggings" , "-boots"})
public class EquipCommand extends ZNCommand {

    public EquipCommand(final ServersNPC serversNPC) {
        super(serversNPC , "equip" , "znpcs.cmd.equip", CommandType.PLAYER);
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
            final NPC.NPCItemSlot npcItemSlot = NPC.NPCItemSlot.fromString(key.toUpperCase());
            final Material material = Material.getMaterial(value.toUpperCase().trim());

            if (npcItemSlot != null && material != null) {
                try {
                    i.getAndIncrement();

                    npcOptional.get().equip(null , npcItemSlot, material);
                } catch (Exception exception) {}
            }
        });
        if (i.get() > 0) sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.GOLD + i.get() + ChatColor.GREEN + " slots.");
        return false;
    }
}
