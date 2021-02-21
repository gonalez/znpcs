package ak.znetwork.znpcservers.commands.list;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.annotation.SubCommand;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.npc.path.writer.ZNPCPathWriter;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class DefaultCommand {

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

    @SubCommand(aliases = {}, required = "", permission = "")
    public void defaultCommand(CommandSender sender, Map<String, String> args) {
        sender.sendMessage(Utils.color("&6&m------------------------------------------"));
        sender.sendMessage(Utils.color("&a&lZNPCS ZNETWORK &8(&6https://www.spigotmc.org/resources/znpcs-1-8-1-16-bungeecord-serversnpcs-open-source.80940/&8)"));
        serversNPC.getCommandsManager().getZnCommands().forEach(znCommand -> znCommand.getConsumerSet().forEach((cmdInfo, commandInvoker) -> sender.sendMessage(ChatColor.YELLOW + ("/znpcs " + cmdInfo.required() + " " + String.join(" ", cmdInfo.aliases())))));
        sender.sendMessage(Utils.color("&6&m------------------------------------------"));
    }

    @SubCommand(aliases = {"-id", "-skin", "-name"}, required = "create", permission = "znpcs.cmd.create")
    public void createNPC(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 3) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        boolean foundNPC = serversNPC.getNpcManager().getNpcList().stream().anyMatch(npc -> npc.getId() == id);

        if (foundNPC) {
            sender.sendMessage(ChatColor.RED + "I have found an npc with this id, try putting a unique id..");
            return;
        }

        String skin = args.get("skin").trim();

        if (skin.length() < 3 || skin.length() > 16) {
            sender.sendMessage(ChatColor.RED + "The skin name is too short or long, it must be in the range of (3 to 16) characters.");
            return;
        }

        String name = args.get("name").trim();

        try {
            // All success!
            serversNPC.createNPC(id, ((Player) sender).getLocation(), skin, (name.length() > 0 ? name : "NPC"), true);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception e) {
            throw new CommandExecuteException("An error occurred while creating npc", e);
        }
    }

    @SubCommand(aliases = {"-id"}, required = "delete", permission = "znpcs.cmd.delete")
    public void deleteNPC(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        boolean foundNPC = serversNPC.getNpcManager().getNpcList().stream().anyMatch(npc -> npc.getId() == id);

        if (!foundNPC) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            serversNPC.deleteNPC(id);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while deleting npc " + id, exception);
        }
    }

    @SubCommand(aliases = {}, required = "list", permission = "znpcs.cmd.list")
    public void list(CommandSender sender, Map<String, String> args) {
        if (serversNPC.getNpcManager().getNpcList().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No NPC found.");
        } else
            serversNPC.getNpcManager().getNpcList().forEach(npc -> sender.sendMessage(Utils.color("&f&l * &a" + npc.getId() + " " + npc.getHologram().getLinesFormatted() + " &7(&e" + npc.getLocation().getWorld().getName() + " " + npc.getLocation().getBlockX() + " " + npc.getLocation().getBlockY() + " " + npc.getLocation().getBlockZ() + "&7)")));
    }

    @SubCommand(aliases = {"-id", "-skin"}, required = "skin", permission = "znpcs.cmd.skin")
    public void setSkin(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String skin = args.get("skin").trim();

        ZNPCSkin skinFetch = ZNPCSkin.forName(skin);
        foundNPC.get().changeSkin(skinFetch);

        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
    }

    @SubCommand(aliases = {"-id", "-hand", "-helmet", "-chestplate", "-leggings", "-boots"}, required = "equip", permission = "znpcs.cmd.equip")
    public void equip(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        args.forEach((key, value) -> {
            NPCItemSlot npcItemSlot = NPCItemSlot.fromString(key.toUpperCase());
            Material material = Material.getMaterial(value.toUpperCase().trim());

            if (npcItemSlot != null && material != null) {
                try {
                    foundNPC.get().equip(null, npcItemSlot, material);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
    }

    @SubCommand(aliases = {"-id", "-lines"}, required = "lines", permission = "znpcs.cmd.lines")
    public void changeLines(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String lines = args.get("lines");
        try {
            List<String> stringList = Arrays.asList(lines.split(" "));
            Collections.reverse(stringList);

            foundNPC.get().getHologram().setLines(stringList.toArray(new String[0]));
            foundNPC.get().getHologram().createHologram();

            // Update lines
            foundNPC.get().setLines(foundNPC.get().getHologram().getLinesFormatted());

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing hologram for npc " + foundNPC.get().getId(), exception);
        }
    }

    @SubCommand(aliases = {"-id"}, required = "move", permission = "znpcs.cmd.move")
    public void move(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            if (foundNPC.get().isHasLookAt()) foundNPC.get().toggleLookAt();

            Location location = ((Player) sender).getLocation().clone();
            foundNPC.get().setLocation(location.getBlock().getType().name().contains("STEP") ? location.subtract(0, 0.5, 0) : location);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while moving npc", exception);
        }
    }


    @SubCommand(aliases = {"-id", "-type"}, required = "type", permission = "znpcs.cmd.type")
    public void changeType(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        NPCType npcType = NPCType.fromString(args.get("type").trim().toUpperCase());

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
            foundNPC.get().changeType(npcType);

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception e) {
            throw new CommandExecuteException("An error occurred while changing npc type", e);
        }
    }

    @SubCommand(aliases = {"-id", "-add", "-remove", "-cooldown", "-list"}, required = "action", permission = "znpcs.cmd.action")
    public void action(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        Optional<ZNPC> foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst();

        if (!foundNPC.isPresent()) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        if (args.containsKey("add")) {
            String[] split = args.get("add").split(" ");

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <CMD:CONSOLE:SERVER> <action>");
                return;
            }

            NPCAction npcAction = NPCAction.fromString(split[0]);
            if (npcAction == null)
                throw new UnsupportedOperationException(String.format("The action type %s was not found", split[0]));

            foundNPC.get().getActions().add(npcAction.name() + ":" + String.join(" ", Arrays.copyOfRange(split, 1, split.length)));
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } else if (args.containsKey("remove")) {
            Integer actionId = Ints.tryParse(args.get("remove").trim());

            if (actionId == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            } else {
                boolean found = (!foundNPC.get().getActions().isEmpty() && (foundNPC.get().getActions().size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    foundNPC.get().getActions().remove(actionId.intValue());
                    ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
                }
            }
        } else if (args.containsKey("cooldown")) {
            String[] split = args.get("cooldown").split(" ");

            if (split.length <= 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage -> <action_id> <seconds>");
                return;
            }

            Integer actionId = Ints.tryParse(split[0]);
            if (actionId == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            } else {
                boolean found = (!foundNPC.get().getActions().isEmpty() && (foundNPC.get().getActions().size() - 1) >= actionId);

                if (!found) sender.sendMessage(ChatColor.RED + "The action (" + actionId + ") was not found.");
                else {
                    int action = Integer.parseInt(split[0]);
                    int seconds = Integer.parseInt(split[1]);

                    foundNPC.get().getActions().set(action, String.join(":", Arrays.copyOfRange(foundNPC.get().getActions().get(action).split(":"), 0, 2)) + ":" + seconds);
                    ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
                }
            }
        } else if (args.containsKey("list")) {
            if (foundNPC.get().getActions().isEmpty()) sender.sendMessage(ChatColor.GREEN + "No actions found.");
            else
                foundNPC.get().getActions().forEach(s -> sender.sendMessage(Utils.color("&8(&a" + foundNPC.get().getActions().indexOf(s) + "&8) &6" + s)));
        }
    }

    @SubCommand(aliases = {"-id", "-holo", "-name", "-glow", "-mirror", "-look", "-pathreverse"}, required = "toggle", permission = "znpcs.cmd.toggle")
    public void toggle(CommandSender sender, Map<String, String> args) throws CommandExecuteException {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        try {
            if (args.containsKey("holo")) foundNPC.toggleHolo();
            else if (args.containsKey("name")) foundNPC.toggleName(true);
            else if (args.containsKey("glow")) foundNPC.toggleGlow(args.get("glow"), true);
            else if (args.containsKey("mirror")) foundNPC.toggleMirror();
            else if (args.containsKey("look")) foundNPC.toggleLookAt();
            else if (args.containsKey("pathreverse")) foundNPC.setReversePath(!foundNPC.isReversePath());

            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } catch (Exception exception) {
            throw new CommandExecuteException("An error occurred while changing toggle command", exception);
        }
    }

    @SubCommand(aliases = {"-id", "-customize"}, required = "customize", permission = "znpcs.cmd.customize")
    public void customize(CommandSender sender, Map<String, String> args) throws Exception {
        if (args.size() < 2) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id").trim());

        if (id == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

        if (foundNPC == null) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
            return;
        }

        String[] value = args.get("customize").trim().split(" ");

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

                foundNPC.customize(methodName, Arrays.asList(objects));
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

    @SubCommand(aliases = {"-set", "-create", "-exit", "-id", "-path", "-list"}, required = "path", permission = "znpcs.cmd.path")
    public void path(CommandSender sender, Map<String, String> args) throws Exception {
        if (args.size() < 1) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INCORRECT_USAGE);
            return;
        }

        Player player = (Player) sender;

        ZNPCUser znpcUser = this.serversNPC.getNpcUsers().stream().filter(znpcUser1 -> znpcUser1.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
        if (znpcUser == null) return;

        if (args.containsKey("set")) {
            if (!args.containsKey("id") || !args.containsKey("path")) {
                sender.sendMessage(ChatColor.RED + "Correct usage /znpcs path -set -id <npc_id> -path <path_name>");
                return;
            }

            Integer id = Ints.tryParse(args.get("id").trim());

            if (id == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.INVALID_NUMBER);
                return;
            }

            ZNPC foundNPC = serversNPC.getNpcManager().getNpcList().stream().filter(npc -> npc.getId() == id).findFirst().orElse(null);

            if (foundNPC == null) {
                ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NPC_NOT_FOUND);
                return;
            }

            String pathName = args.get("path").trim();

            ZNPCPathReader pathReader = serversNPC.getNpcManager().getNpcPaths().stream().filter(znpcPathReader -> znpcPathReader.getName().equalsIgnoreCase(pathName)).findFirst().orElse(null);
            foundNPC.setPath(pathReader);
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.SUCCESS);
        } else if (args.containsKey("create")) {
            String pathName = args.get("create").trim();

            if (pathName.length() < 3 || pathName.length() > 16) {
                sender.sendMessage(ChatColor.RED + "The path name is too short or long, it must be in the range of (3 to 16) characters.");
                return;
            }

            boolean exists = serversNPC.getNpcManager().getNpcPaths().stream().anyMatch(znpcPath -> znpcPath.getName().equalsIgnoreCase(pathName));

            if (exists) {
                player.sendMessage(ChatColor.RED + "There is already a path with this name.");
                return;
            }

            boolean hasActiveCreator = znpcUser.isHasPath();
            if (hasActiveCreator) {
                player.sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path -exit.");
                return;
            }

            new ZNPCPathWriter(serversNPC, znpcUser, pathName);
            player.sendMessage(ChatColor.GREEN + "Done, now walk where you want the npc to, when u finish type /znpcs path -exit");
        } else if (args.containsKey("exit")) {
            znpcUser.setHasPath(false);

            player.sendMessage(ChatColor.RED + "You have exited the waypoint creation.");
        } else if (args.containsKey("list")) {
            if (serversNPC.getNpcManager().getNpcPaths().isEmpty())
                player.sendMessage(ChatColor.RED + "No PATH found!");
            else serversNPC.getNpcManager().getNpcPaths().forEach(znpcPath -> player.sendMessage(ChatColor.GREEN + znpcPath.getName()));
        }
    }
}
