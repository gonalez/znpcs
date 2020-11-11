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
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.utils.JSONUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
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

    @CMDInfo(aliases = {"-id" , "-skin" , "-name"}, required = "create", permission = "znpcs.cmd.create")
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
        serversNPC.createNPC(id, Optional.of(sender), ((Player)sender).getLocation(), skin, (name.length() > 0 ? name : "NPC"), true);
    }

    @CMDInfo(aliases = {"-id"}, required = "delete", permission = "znpcs.cmd.delete")
    public void deleteNPC(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
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
            throw new CommandExecuteException("An error occurred while deleting npc npc " + id);
        }
    }

    @CMDInfo(aliases = {}, required = "list", permission = "znpcs.cmd.list")
    public void list(final CommandSender sender, Map<String, String> args) {
        if (serversNPC.getNpcManager().getNpcs().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No NPC found.");
        } else serversNPC.getNpcManager().getNpcs().forEach(npc -> sender.sendMessage(Utils.color("&f&l * &a" + npc.getId() + " " + npc.getHologram().getLinesFormatted() + " &7(&e" + npc.getLocation().getWorld().getName() + " " + npc.getLocation().getBlockX() + " " + npc.getLocation().getBlockY() + " " + npc.getLocation().getBlockZ() + "&7)")));
    }

    @CMDInfo(aliases = {"-id" , "-skin"}, required = "skin", permission = "znpcs.cmd.skin")
    public void setSkin(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
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

        Optional<SkinFetch> skinFetch = Optional.empty();
        try {
            URL url = new URL(skin);

            try {
                skinFetch = Optional.of(JSONUtils.getByURL(skin));
            } catch (Exception exception) {
                exception.printStackTrace();

                throw new CommandExecuteException("Could not connect to url");
            }
        } catch (MalformedURLException e) {
            // It is not a url, set default skin method
            if (skin.length() < 3 || skin.length() > 16) {
                sender.sendMessage(ChatColor.RED + "The skin name is too short or long, it must be in the range of (3 to 16) characters.");
                return;
            }

            try {
                skinFetch = Optional.of(JSONUtils.getSkin(skin));
            } catch (ExecutionException | InterruptedException executionException) {
                throw new CommandExecuteException("An error occurred while changing skin for npc " + foundNPC.get().getId());
            }
        } finally {
            if (skinFetch.isPresent()) { // All success
                try {
                    foundNPC.get().updateSkin(skinFetch.get());

                    sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
                } catch (Exception exception) {
                    throw new CommandExecuteException("An error occurred while changing skin for npc " + foundNPC.get().getId());
                }
            }
        }
    }

    @CMDInfo(aliases = {"-id" , "-hand" , "-helmet" , "-chestplate" , "-leggings" , "-boots"}, required = "equip", permission = "znpcs.cmd.equip")
    public void equip(final CommandSender sender, Map<String, String> args) {
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

        args.forEach((key, value) -> {
            final NPC.NPCItemSlot npcItemSlot = NPC.NPCItemSlot.fromString(key.toUpperCase());
            final Material material = Material.getMaterial(value.toUpperCase().trim());

            if (npcItemSlot != null && material != null) {
                try {
                    foundNPC.get().equip(null , npcItemSlot, material);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
    }

    @CMDInfo(aliases = {"-id" , "-lines"}, required = "lines", permission = "znpcs.cmd.lines")
    public void changeLines(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
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

        final String lines = args.get("lines");
        try {
            final List<String> stringList = Arrays.asList(lines.split(" "));
            Collections.reverse(stringList);

            foundNPC.get().getHologram().lines = stringList.toArray(new String[0]);
            foundNPC.get().getHologram().createHolos();

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing hologram for npc " + foundNPC.get().getId());
        }
    }

    @CMDInfo(aliases = {"-id"}, required = "move", permission = "znpcs.cmd.move")
    public void move(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
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

        try {
            if (foundNPC.get().isHasLookAt()) foundNPC.get().toggleLookAt();

            final Location location = ((Player)sender).getLocation();

            foundNPC.get().setLocation((location.getBlock().getType().name().contains("STEP") ? location.subtract(0, 0.5, 0) : location));

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while moving npc");
        }
    }


    @CMDInfo(aliases = {"-id", "-type"}, required = "type", permission = "znpcs.cmd.type")
    public void changeType(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
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

        final NPCType npcType = NPCType.fromString(args.get("type").trim().toUpperCase());

        if (npcType == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not found");
            sender.sendMessage(ChatColor.GOLD + "Valid Types:");

            for (NPCType npcTypes : NPCType.values()) sender.sendMessage(ChatColor.RED + npcTypes.name);
            return;
        }

        if (npcType.constructor == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not available for your current version.");
            return;
        }

        try {
            foundNPC.get().changeType(npcType);

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception e) {
            throw new CommandExecuteException("An error occurred while changing npc type");
        }
    }

    @CMDInfo(aliases = {"-id", "-add" , "-remove" , "-cooldown" , "-list"}, required = "action", permission = "znpcs.cmd.action")
    public void action(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
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

        if (args.containsKey("add")) {
            String[] split = args.get("add").split(" ");

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <CMD:CONSOLE:SERVER> <action>");
                return;
            }

            NPCAction npcAction = NPCAction.fromString(split[0]);
            if (npcAction == null) throw new CommandExecuteException(String.format("The action type %s was not found" , split[0]));

            foundNPC.get().getActions().add(npcAction.name() + ":" + String.join(" ", Arrays.copyOfRange(split, 1, split.length)));
            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } else if (args.containsKey("remove")) {
            final Integer actionId = Ints.tryParse(args.get("remove").trim());

            if (actionId == null) {
                sender.sendMessage(ChatColor.RED + "Action id must be a valid number...");
            } else {
                boolean found = (!foundNPC.get().getActions().isEmpty() && (foundNPC.get().getActions().size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    foundNPC.get().getActions().remove(actionId.intValue());
                    sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
                }
            }
        } else if (args.containsKey("cooldown")) {
            String[] split = args.get("cooldown").split(" ");

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <action_id> <seconds>");
                return;
            }

            final Integer actionId = Ints.tryParse(args.get("remove").trim());

            if (actionId == null) {
                sender.sendMessage(ChatColor.RED + "Action id must be a valid number...");
            } else {
                boolean found = (!foundNPC.get().getActions().isEmpty() && actionId >= foundNPC.get().getActions().size());

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    int action = Integer.parseInt(split[0]);
                    int seconds = Integer.parseInt(split[1]);

                    foundNPC.get().getActions().set(action, String.join(":", Arrays.copyOfRange(foundNPC.get().getActions().get(action).split(":"), 0, 2))  + ":" + seconds);
                    sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
                }
            }
        } else if (args.containsKey("list")) {
            if (foundNPC.get().getActions().isEmpty()) sender.sendMessage(ChatColor.GREEN + "No actions found.");
            else foundNPC.get().getActions().forEach(s -> sender.sendMessage(Utils.color("&8(&a" + foundNPC.get().getActions().indexOf(s) + "&8) &6" + s)));
        }
    }

    @CMDInfo(aliases = {"-id", "-holo" , "-name" , "-glow", "-mirror", "-look"}, required = "toggle", permission = "znpcs.cmd.toggle")
    public void toggle(final CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
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

        try {
            if (args.containsKey("holo")) foundNPC.get().toggleHolo();
            else if (args.containsKey("name")) foundNPC.get().toggleName(true);
            else if (args.containsKey("glow")) foundNPC.get().toggleGlow(Optional.ofNullable((Player)sender), args.get("glow"),true);
            else if (args.containsKey("mirror")) foundNPC.get().toggleMirror();
            else if (args.containsKey("look")) foundNPC.get().toggleLookAt();

            sender.sendMessage(Utils.color(serversNPC.getMessages().getConfig().getString("success")));
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing toggle command");
        }
    }
}
