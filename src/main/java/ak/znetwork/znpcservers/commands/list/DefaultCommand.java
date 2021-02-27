package ak.znetwork.znpcservers.commands.list;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.npc.path.writer.ZNPCPathWriter;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZNPCUser;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;

import static ak.znetwork.znpcservers.commands.impl.ZNCommandImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class DefaultCommand {

    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * The plugin instance.
     */
    private ServersNPC serversNPC;

    /**
     * Creates a new command.
     *
     * @param serversNPC The plugin instance.
     */
    public DefaultCommand(ServersNPC serversNPC) {
        this.serversNPC = serversNPC;
    }

    @ZNCommandSub(aliases = {}, name = "", permission = "")
    public void defaultCommand(ZNCommandSender sender, Map<String, String> args) {
        sender.sendMessage("&6&m------------------------------------------");
        sender.sendMessage("&a&lZNPCS ZNETWORK &8(&6https://www.spigotmc.org/resources/znpcs-1-8-1-16-bungeecord-serversnpcs-open-source.80940/&8)");
        serversNPC.getCommandsManager().getZnCommands().forEach(znCommand -> znCommand.getConsumerSet().keySet().forEach(subCommand -> sender.sendMessage(ChatColor.YELLOW + ("/znpcs " + subCommand.name() + " " + String.join(" ", subCommand.aliases())))));
        sender.sendMessage("&6&m------------------------------------------");
    }

    @ZNCommandSub(aliases = {"-id", "-skin", "-name"}, name = "create", permission = "znpcs.cmd.create")
    public void createNPC(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 3) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        boolean foundNPC = ConfigTypes.NPC_LIST.stream().anyMatch(npc -> npc.getId() == id);

        if (foundNPC) {
            sender.sendMessage(ChatColor.RED + "I have found an npc with this id, try putting a unique id..");
            return;
        }

        String skin = args.get("skin");

        if (skin.length() < 3 || skin.length() > 16) {
            sender.sendMessage(ChatColor.RED + "The skin name is too short or long, it must be in the range of (3 to 16) characters.");
            return;
        }

        String name = args.get("name");

        try {
            // All success!
            serversNPC.createNPC(id, sender.getPlayer().getLocation(), skin, (name.length() > 0 ? name : "NPC"));

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception e) {
            throw new CommandExecuteException("An error occurred while creating npc", e);
        }
    }

    @ZNCommandSub(aliases = {"-id"}, name = "delete", permission = "znpcs.cmd.delete")
    public void deleteNPC(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        boolean foundNPC = ConfigTypes.NPC_LIST.stream().anyMatch(npc -> npc.getId() == id);

        if (!foundNPC) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        serversNPC.deleteNPC(id);

        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
    }

    @ZNCommandSub(aliases = {}, name = "list", permission = "znpcs.cmd.list")
    public void list(ZNCommandSender sender, Map<String, String> args) {
        if (ConfigTypes.NPC_LIST.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No NPC found.");
        } else
            ConfigTypes.NPC_LIST.forEach(npc -> sender.sendMessage("&f&l * &a" + npc.getId() + " " + npc.getTextFormatted(npc.getHologram().getLines()) + " &7(&e" + npc.getLocation().getWorld().getName() + " " + npc.getLocation().getBlockX() + " " + npc.getLocation().getBlockY() + " " + npc.getLocation().getBlockZ() + "&7)"));
    }

    @ZNCommandSub(aliases = {"-id", "-skin"}, name = "skin", permission = "znpcs.cmd.skin")
    public void setSkin(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String skin = args.get("skin");

        foundNPC.changeSkin(ZNPCSkin.forName(skin));

        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
    }

    @ZNCommandSub(aliases = {"-id", "-hand", "-offhand", "-helmet", "-chestplate", "-leggings", "-boots"}, name = "equip", permission = "znpcs.cmd.equip")
    public void equip(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        args.forEach((key, value) -> {
            NPCItemSlot npcItemSlot = NPCItemSlot.fromString(key.toUpperCase());
            Material material = Material.getMaterial(value.toUpperCase());

            if (npcItemSlot != null && material != null) {
                foundNPC.equip(null, npcItemSlot, material);
            }
        });

        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
    }

    @ZNCommandSub(aliases = {"-id", "-lines"}, name = "lines", permission = "znpcs.cmd.lines")
    public void changeLines(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String lines = args.get("lines");
        try {
            List<String> stringList = Lists.reverse(Arrays.asList(lines.split(WHITESPACE)));

            foundNPC.getHologram().setLines(stringList.toArray(new String[0]));
            foundNPC.getHologram().createHologram();

            // Update lines
            foundNPC.setLines(foundNPC.getTextFormatted(foundNPC.getHologram().getLines()));

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing hologram for npc " + foundNPC.getId(), exception);
        }
    }

    @ZNCommandSub(aliases = {"-id"}, name = "move", permission = "znpcs.cmd.move")
    public void move(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            if (foundNPC.isHasLookAt()) foundNPC.toggleLookAt();

            Location location = sender.getPlayer().getLocation().clone();
            foundNPC.setLocation(location.getBlock().getType().name().contains("STEP") ? location.subtract(0, 0.5, 0) : location);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while moving npc", exception);
        }
    }


    @ZNCommandSub(aliases = {"-id", "-type"}, name = "type", permission = "znpcs.cmd.type")
    public void changeType(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        NPCType npcType = NPCType.fromString(args.get("type").toUpperCase());

        if (npcType == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not found");
            sender.sendMessage(ChatColor.GOLD + "Valid Types:");

            for (NPCType npcTypes : NPCType.values()) sender.sendMessage(ChatColor.RED + npcTypes.name());
            return;
        }

        if (npcType != NPCType.PLAYER && npcType.getConstructor() == null) {
            sender.sendMessage(ChatColor.RED + "NPC type not available for your current version.");
            return;
        }

        try {
            foundNPC.changeType(npcType);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception e) {
            throw new CommandExecuteException("An error occurred while changing npc type", e);
        }
    }

    @ZNCommandSub(aliases = {"-id", "-add", "-remove", "-cooldown", "-list"}, name = "action", permission = "znpcs.cmd.action")
    public void action(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        if (args.containsKey("add")) {
            String[] split = args.get("add").split(WHITESPACE);

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <CMD:CONSOLE:SERVER> <action>");
                return;
            }

            NPCAction npcAction = NPCAction.fromString(split[0]);
            if (npcAction == null)
                throw new UnsupportedOperationException(String.format("The action type %s was not found", split[0]));

            foundNPC.getActions().add(npcAction.name() + ":" + String.join(" ", Arrays.copyOfRange(split, 1, split.length)));
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } else if (args.containsKey("remove")) {
            Integer actionId = Ints.tryParse(args.get("remove"));

            if (actionId == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            } else {
                boolean found = (!foundNPC.getActions().isEmpty() && (foundNPC.getActions().size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    foundNPC.getActions().remove(actionId.intValue());
                    ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
                }
            }
        } else if (args.containsKey("cooldown")) {
            String[] split = args.get("cooldown").split(WHITESPACE);

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <action_id> <seconds>");
                return;
            }

            Integer actionId = Ints.tryParse(split[0]);
            if (actionId == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            } else {
                boolean found = (!foundNPC.getActions().isEmpty() && (foundNPC.getActions().size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    int action = Integer.parseInt(split[0]);
                    int seconds = Integer.parseInt(split[1]);

                    foundNPC.getActions().set(action, String.join(":", Arrays.copyOfRange(foundNPC.getActions().get(action).split(":"), 0, 2)) + ":" + seconds);
                    ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
                }
            }
        } else if (args.containsKey("list")) {
            if (foundNPC.getActions().isEmpty()) sender.sendMessage(ChatColor.GREEN + "No actions found.");
            else
                foundNPC.getActions().forEach(s -> sender.sendMessage("&8(&a" + foundNPC.getActions().indexOf(s) + "&8) &6" + s));
        }
    }

    @ZNCommandSub(aliases = {"-id", "-holo", "-name", "-glow", "-mirror", "-look", "-pathreverse"}, name = "toggle", permission = "znpcs.cmd.toggle")
    public void toggle(ZNCommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            if (args.containsKey("holo")) foundNPC.toggleHolo();
            else if (args.containsKey("glow")) foundNPC.toggleGlow(args.get("glow"), true);
            else if (args.containsKey("mirror")) foundNPC.toggleMirror();
            else if (args.containsKey("look")) foundNPC.toggleLookAt();
            else if (args.containsKey("pathreverse")) foundNPC.setReversePath(!foundNPC.isReversePath());

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing toggle command", exception);
        }
    }

    @ZNCommandSub(aliases = {"-id", "-customize"}, name = "customize", permission = "znpcs.cmd.customize")
    public void customize(ZNCommandSender sender, Map<String, String> args) throws Exception {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String[] value = args.get("customize").split(WHITESPACE);

        NPCType npcType = foundNPC.getNpcType();

        String methodName = value[0];
        if (npcType.getCustomizationMethods().containsKey(methodName)) {
            String[] split = Arrays.copyOfRange(value, 1, value.length);

            Method method = npcType.getCustomizationMethods().get(methodName);

            if ((value.length) - 1 < method.getParameterTypes().length) {
                sender.sendMessage(ChatColor.RED + "Too few arguments");
                return;
            }

            try {
                Object[] objects = NPCType.arrayToPrimitive(split, method);

                npcType.invokeMethod(methodName, foundNPC.getZnEntity(), objects);
                foundNPC.customize(methodName, split);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException exception) {
                throw new CommandExecuteException("An error occurred while customizing npc", exception);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Method not found");
            sender.sendMessage(ChatColor.GOLD + "Valid Methods:");

            for (Method method : npcType.getCustomizationMethods().values())
                sender.sendMessage(ChatColor.RED + method.getName() + " " + Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(" ")));
        }
    }

    @ZNCommandSub(aliases = {"-set", "-create", "-exit", "-id", "-path", "-list"}, name = "path", permission = "znpcs.cmd.path")
    public void path(ZNCommandSender sender, Map<String, String> args) throws Exception {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        ZNPCUser znpcUser = NPCManager.getNpcUsers().stream().filter(znpcUser1 -> znpcUser1.getUuid().equals(sender.getPlayer().getUniqueId())).findFirst().orElse(null);
        if (znpcUser == null) return;

        if (args.containsKey("set")) {
            if (!args.containsKey("id") || !args.containsKey("path")) {
                sender.sendMessage(ChatColor.RED + "Correct usage /znpcs path -set -id <npc_id> -path <path_name>");
                return;
            }

            Integer id = Ints.tryParse(args.get("id"));

            if (id == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
                return;
            }

            ZNPC foundNPC = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

            if (foundNPC == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
                return;
            }

            String pathName = args.get("path");

            foundNPC.setPath(ZNPCPathReader.find(pathName));

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } else if (args.containsKey("create")) {
            String pathName = args.get("create");

            if (pathName.length() < 3 || pathName.length() > 16) {
                sender.sendMessage(ChatColor.RED + "The path name is too short or long, it must be in the range of (3 to 16) characters.");
                return;
            }

            boolean exists = ZNPCPathReader.find(pathName) != null;

            if (exists) {
                sender.getPlayer().sendMessage(ChatColor.RED + "There is already a path with this name.");
                return;
            }

            boolean hasActiveCreator = znpcUser.isHasPath();
            if (hasActiveCreator) {
                sender.getPlayer().sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path -exit.");
                return;
            }

            new ZNPCPathWriter(serversNPC, znpcUser, pathName);
            sender.getPlayer().sendMessage(ChatColor.GREEN + "Done, now walk where you want the npc to, when u finish type /znpcs path -exit");
        } else if (args.containsKey("exit")) {
            znpcUser.setHasPath(false);

            sender.getPlayer().sendMessage(ChatColor.RED + "You have exited the waypoint creation.");
        } else if (args.containsKey("list")) {
            if (ZNPCPathReader.getPaths().isEmpty())
                sender.getPlayer().sendMessage(ChatColor.RED + "No PATH found!");
            else ZNPCPathReader.getPaths().forEach(pathReader -> sender.getPlayer().sendMessage(ChatColor.GREEN + pathReader.getName()));
        }
    }
}
