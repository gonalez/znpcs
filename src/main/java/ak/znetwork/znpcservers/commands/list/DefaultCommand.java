package ak.znetwork.znpcservers.commands.list;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCToggle;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static ak.znetwork.znpcservers.npc.path.ZNPCPathImpl.AbstractTypeWriter.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class DefaultCommand extends ZNCommand {

    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * Creates a new command.
     */
    public DefaultCommand(String command) {
        super(command);
    }

    @ZNCommandSub(aliases = {}, name = "", permission = "")
    public void defaultCommand(ZNCommandSender sender, Map<String, String> args) {
        sender.sendMessage("&6&m------------------------------------------");
        sender.sendMessage("&a&lZNPCS ZNETWORK &8(&6https://www.spigotmc.org/resources/znpcs-1-8-1-16-bungeecord-serversnpcs-open-source.80940/&8)");
        getCommands().forEach(subCommand -> sender.sendMessage(ChatColor.YELLOW + ("/znpcs " + subCommand.name() + " " + String.join(" ", subCommand.aliases()))));
        sender.sendMessage("&6&m------------------------------------------");
    }

    @ZNCommandSub(aliases = {"-id", "-type", "-name"}, name = "create", permission = "znpcs.cmd.create")
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
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_FOUND);
            return;
        }

        String name = args.get("name").trim();

        if (name.length() < 3 || name.length() > 16) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NAME_LENGTH);
            return;
        }

        String type = args.get("type").toUpperCase();

        try {
            ServersNPC.createNPC(id,
                    NPCType.valueOf(type),
                    sender.getPlayer().getLocation(),
                    name
            );

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

        ServersNPC.deleteNPC(id);
        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
    }

    @ZNCommandSub(aliases = {}, name = "list", permission = "znpcs.cmd.list")
    public void list(ZNCommandSender sender, Map<String, String> args) {
        if (ConfigTypes.NPC_LIST.isEmpty()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NO_NPC_FOUND);
        } else {
            ConfigTypes.NPC_LIST.forEach(npc -> sender.sendMessage("&f&l * &a" + npc.getId() + " " + npc.getHologramLines().toString() + " &7(&e" + npc.getLocation().getWorld() + " " + npc.getLocation().getX() + " " + npc.getLocation().getY() + " " + npc.getLocation().getZ() + "&7)"));
        }
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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String skin = args.get("skin");
        foundNPC.changeSkin(ZNPCSkin.forName(skin));

        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
    }

    @ZNCommandSub(aliases = {"-id", "-slot"}, name = "equip", permission = "znpcs.cmd.equip")
    public void equip(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String slot = args.get("slot").toUpperCase();

        NPCItemSlot npcItemSlot = NPCItemSlot.valueOf(slot);

        ItemStack itemStack = sender.getPlayer().getInventory().getItemInMainHand();
        foundNPC.getNpcPojo().getNpcEquip().put(npcItemSlot, itemStack);
        foundNPC.updateEquipment();

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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String lines = args.get("lines");
        try {
            List<String> stringList = Lists.reverse(Arrays.asList(lines.split(WHITESPACE)));

            // Update lines
            foundNPC.getNpcPojo().setHologramLines(stringList);
            foundNPC.getHologram().createHologram();

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing hologram for npc " + foundNPC.getNpcPojo().getId(), exception);
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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            if (foundNPC.getNpcPojo().isHasLookAt()) {
                foundNPC.getNpcPojo().setHasLookAt(!foundNPC.getNpcPojo().isHasLookAt());
            }

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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        NPCType npcType = NPCType.valueOf(args.get("type").toUpperCase());

        if (npcType != NPCType.PLAYER && npcType.getConstructor() == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.UNSUPPORTED_ENTITY);
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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        final List<String> actions = foundNPC.getNpcPojo().getActions();
        if (args.containsKey("add")) {
            String[] split = args.get("add").split(WHITESPACE);

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <CMD:CONSOLE:SERVER> <action>");
                return;
            }

            NPCAction npcAction = NPCAction.valueOf(split[0].toUpperCase());
            actions.add(npcAction.name() + ":" + String.join(" ", Arrays.copyOfRange(split, 1, split.length)));

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } else if (args.containsKey("remove")) {
            Integer actionId = Ints.tryParse(args.get("remove"));

            if (actionId == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            } else {
                boolean found = (!foundNPC.getNpcPojo().getActions().isEmpty() && (actions.size() - 1) >= actionId);

                if (!found) {
                    sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                } else {
                    actions.remove(actionId.intValue());
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
                boolean found = (!actions.isEmpty() && (actions.size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    int action = Integer.parseInt(split[0]);
                    int seconds = Integer.parseInt(split[1]);

                    actions.set(action, String.join(":", Arrays.copyOfRange(actions.get(action).split(":"), 0, 2)) + ":" + seconds);
                    ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
                }
            }
        } else if (args.containsKey("list")) {
            if (actions.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "No actions found.");
            } else {
                actions.forEach(s -> sender.sendMessage("&8(&a" + actions.indexOf(s) + "&8) &6" + s));
            }
        }
    }

    @ZNCommandSub(aliases = {"-id", "-type", "-value"}, name = "toggle", permission = "znpcs.cmd.toggle")
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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        NPCToggle npcToggle = NPCToggle.valueOf(args.get("type").toUpperCase());

        try {
            npcToggle.toggle(foundNPC, args.get("value"));
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

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String[] value = args.get("customize").split(WHITESPACE);

        NPCType npcType = foundNPC.getNpcPojo().getNpcType();

        String methodName = value[0];
        if (npcType.getCustomizationMethods().containsKey(methodName)) {
            String[] split = Arrays.copyOfRange(value, 1, value.length);

            Method method = npcType.getCustomizationMethods().get(methodName);

            if ((value.length) - 1 < method.getParameterTypes().length) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.TOO_FEW_ARGUMENTS);
                return;
            }

            try {
                Object[] objects = NPCType.arrayToPrimitive(split, method);

                npcType.invokeMethod(methodName, foundNPC.getNmsEntity(), objects);
                foundNPC.customize(methodName, split);
            } catch (IllegalAccessException | InvocationTargetException exception) {
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
    public void path(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        ZNPCUser znpcUser = ZNPCUser.registerOrGet(sender.getPlayer());
        if (znpcUser == null)
            return;

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

            ZNPC foundNPC = ZNPC.find(id);

            if (foundNPC == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
                return;
            }

            String pathName = args.get("path");

            foundNPC.setPath(AbstractTypeWriter.find(pathName));

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.SUCCESS);
        } else if (args.containsKey("create")) {
            String pathName = args.get("create");

            if (pathName.length() < 3 || pathName.length() > 16) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NAME_LENGTH);
                return;
            }

            boolean exists = AbstractTypeWriter.find(pathName) != null;

            if (exists) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.PATH_FOUND);
                return;
            }

            boolean hasActiveCreator = znpcUser.isHasPath();
            if (hasActiveCreator) {
                sender.getPlayer().sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path -exit.");
                return;
            }

            new TypeMovement(pathName, znpcUser);
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.START_PATH);
        } else if (args.containsKey("exit")) {
            znpcUser.setHasPath(false);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.EXIT_PATH);;
        } else if (args.containsKey("list")) {
            if (AbstractTypeWriter.getPaths().isEmpty())
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NO_PATH_FOUND);
            else AbstractTypeWriter.getPaths().forEach(path -> sender.getPlayer().sendMessage(ChatColor.GREEN + path.getName()));
        }
    }

    @ZNCommandSub(aliases = {"-id"}, name = "teleport", permission = "znpcs.cmd.teleport")
    public void teleport(ZNCommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        sender.getPlayer().teleport(foundNPC.getLocation());
    }
}
