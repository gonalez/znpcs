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
import ak.znetwork.znpcservers.commands.annotation.CMDInfo;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.utils.JSONUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class DefaultCommand {

    protected final ServersNPC serversNPC;

    public DefaultCommand(final ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

    }

    @CMDInfo(aliases = {}, required = "", permission = "")
    public void defaultCommand(final CommandSender sender, Map<String, String> args) {
        sender.sendMessage(Utils.color("&6&m------------------------------------------"));
        sender.sendMessage(Utils.color("&a&lZNPCS ZNETWORK &8(&6https://www.spigotmc.org/resources/znpcs-1-8-1-16-bungeecord-serversnpcs-open-source.80940/&8)"));
        serversNPC.getCommandsManager().getZnCommands().forEach(znCommand -> znCommand.getConsumerSet().forEach((cmdInfo, commandInvoker) -> sender.sendMessage(ChatColor.YELLOW + ("/znpcs " + cmdInfo.required() + " " + String.join(" " , cmdInfo.aliases())))));
        sender.sendMessage(Utils.color("&6&m------------------------------------------"));
    }

    @CMDInfo(aliases = {"-id" , "-skin" , "-name"}, required = "create", permission = "")
    public void createNPC(final CommandSender sender, Map<String, String> args) {
        if (args.size() < 3) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage.");
            return;
        }

        final Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            sender.sendMessage(ChatColor.RED + "NPC id must be a valid number...");
            return;
        }

        boolean foundNPC = serversNPC.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == id);

        if (foundNPC) {
            sender.sendMessage(ChatColor.RED + "I have found an npc with this id, try putting a unique id..");
            return;
        }

        final String skin = args.get("skin").trim();

        if (skin.length() < 3 || skin.length() > 16) {
            sender.sendMessage(ChatColor.RED + "The skin name is too short or long, it must be in the range of (3 to 16) characters.");
            return;
        }

        final String name = args.get("name").trim();

        // All success!
        serversNPC.createNPC(id, Optional.of(sender), ((Player)sender).getLocation(), skin, (name.length() > 0 ? name : "NPC"), false);
    }

    @CMDInfo(aliases = {"-id"}, required = "delete", permission = "")
    public void deleteNPC(final CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage.");
            return;
        }

        final Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            sender.sendMessage(ChatColor.RED + "NPC id must be a valid number...");
            return;
        }

        boolean foundNPC = serversNPC.getNpcManager().getNpcs().stream().anyMatch(npc -> npc.getId() == id);

        if (!foundNPC) {
            sender.sendMessage(ChatColor.RED + "Can't find this npc with this id, try putting a valid id..");
            return;
        }

        try {
            serversNPC.deleteNPC(id);

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "Unable to delete this npc.");
        }
    }

    @CMDInfo(aliases = {"-id" , "-skin"}, required = "delete", permission = "")
    public void setSkin(final CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            sender.sendMessage(ChatColor.RED + "Incorrect usage.");
            return;
        }

        final Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            sender.sendMessage(ChatColor.RED + "NPC id must be a valid number...");
            return;
        }

        Optional<NPC> foundNPC = serversNPC.getNpcManager().getNpcs().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Can't find this npc with this id, try putting a valid id..");
            return;
        }

        final String skin = args.get("skin").trim();

        if (skin.length() < 3 || skin.length() > 16) {
            sender.sendMessage(ChatColor.RED + "The skin name is too short or long, it must be in the range of (3 to 16) characters.");
            return;
        }

        Optional<SkinFetch> skinFetch = Optional.empty();
        try {
            URL url = new URL(skin);

            try {
                skinFetch = Optional.of(JSONUtils.getByURL(url.getPath()));
            } catch (Exception exception) {
                sender.sendMessage(ChatColor.RED + "Could not connect to url.");
            }
        } catch (MalformedURLException e) {
            // It is not a url, set default skin method
            try {
                skinFetch = Optional.of(JSONUtils.getSkin(skin));
            } catch (ExecutionException | InterruptedException executionException) {
                sender.sendMessage(ChatColor.RED + "Could not create npc.");
            }
        } finally {
            if (skinFetch.isPresent()) { // All success
                skinFetch.get()
            }
        }
    }
}
