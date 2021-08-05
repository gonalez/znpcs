package ak.znetwork.znpcservers.commands.list;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.Command;
import ak.znetwork.znpcservers.commands.CommandInformation;
import ak.znetwork.znpcservers.commands.CommandSender;
import ak.znetwork.znpcservers.commands.list.inventory.ConversationGUI;
import ak.znetwork.znpcservers.configuration.ConfigValue;
import ak.znetwork.znpcservers.configuration.ConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.npc.ZNPCToggle;
import ak.znetwork.znpcservers.npc.ZNPCType;
import ak.znetwork.znpcservers.npc.conversation.Conversation;
import ak.znetwork.znpcservers.npc.conversation.ConversationModel;
import ak.znetwork.znpcservers.npc.ZNPCAction;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZUser;
import ak.znetwork.znpcservers.npc.ZNPCSkin;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import org.bukkit.ChatColor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ak.znetwork.znpcservers.npc.ZNPCPath.AbstractTypeWriter.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class DefaultCommand extends Command {
    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * Creates a new splitter instance for a whitespace (' ').
     */
    private static final Splitter SPACE_SPLITTER = Splitter.on(WHITESPACE);

    /**
     * Creates a new joiner instance for a whitespace (' ').
     */
    private static final Joiner SPACE_JOINER = Joiner.on(WHITESPACE);

    /**
     * Creates a new command.
     */
    public DefaultCommand(String command) {
        super(command);
    }

    @CommandInformation(aliases = {}, name = "", permission = "")
    public void defaultCommand(CommandSender sender, Map<String, String> args) {
        sender.sendMessage("&6&m------------------------------------------");
        sender.sendMessage("&a&lZNPCS ZNETWORK &8(&6https://www.spigotmc.org/resources/znpcs-1-8-1-16-bungeecord-serversnpcs-open-source.80940/&8)");
        getCommands().forEach(sender::sendMessage);
        sender.sendMessage("&6&m------------------------------------------");
    }

    @CommandInformation(
            aliases = {"-id", "-type", "-name"},
            name = "create",
            permission = "znpcs.cmd.create",
            help = {
                    " &f&l* &e/znpcs create -id <npc_id> -type PLAYER -name Qentin"
            }
    )
    public void createNPC(CommandSender sender, Map<String, String> args) {
        if (args.size() < 3) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        boolean foundNPC = ConfigTypes.NPC_LIST.stream().anyMatch(npc -> npc.getId() == id);

        if (foundNPC) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_FOUND);
            return;
        }

        String name = args.get("name").trim();

        if (name.length() < 3 || name.length() > 16) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NAME_LENGTH);
            return;
        }

        String type = args.get("type").toUpperCase();

        ZNPC znpc = ServersNPC.createNPC(id, ZNPCType.valueOf(type), sender.getPlayer().getLocation(), name);
        ZNPCSkin.forName(args.get("name"), (values) -> znpc.changeSkin(ZNPCSkin.forValues(values)));
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id"},
            name = "delete",
            permission = "znpcs.cmd.delete",
            help = {
                    " &f&l* &e/znpcs delete -id <npc_id>"
            }
    )
    public void deleteNPC(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        ServersNPC.deleteNPC(id);
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {},
            name = "list",
            permission = "znpcs.cmd.list"
    )
    public void list(CommandSender sender, Map<String, String> args) {
        if (ConfigTypes.NPC_LIST.isEmpty()) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_NPC_FOUND);
        } else {
            ConfigTypes.NPC_LIST.forEach(npc ->
                    sender.sendMessage("&f&l * &a" + npc.getId() + " " + npc.getHologramLines().toString()
                            + " &7(&e" + npc.getLocation().getWorld() + " " + npc.getLocation().getX()
                            + " " + npc.getLocation().getY() + " " + npc.getLocation().getZ() + "&7)"));
        }
    }

    @CommandInformation(
            aliases = {"-id", "-skin"},
            name = "skin",
            permission = "znpcs.cmd.skin",
            help = {
                    " &f&l* &e/znpcs skin -id <npc_id> -skin Notch"
            }
    )
    public void setSkin(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        ZNPCSkin.forName(args.get("skin"), (values) -> {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
            foundNPC.changeSkin(ZNPCSkin.forValues(values));
        });
    }

    @CommandInformation(
            aliases = {"-id", "-slot"},
            name = "equip",
            permission = "znpcs.cmd.equip",
            help = {
                    " &f&l* &e/znpcs equip -id <npc_id> -slot [HAND,OFFHAND,HELMET,CHESTPLATE,LEGGINGS,BOOTS]",
                    "&8(You need to have the item in your hand.)"
            }
    )
    public void equip(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }
        // Update new equip for npc viewers
        foundNPC.updateEquipment(ZNPCSlot.valueOf(args.get("slot").toUpperCase()), sender.getPlayer().getInventory().getItemInHand());
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id", "-lines"},
            name = "lines",
            permission = "znpcs.cmd.lines",
            help = {
                    " &f&l* &e/znpcs lines -id <npc_id> -lines First Second Third-Space"
            }
    )
    public void changeLines(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        // Update lines
        foundNPC.getNpcPojo().setHologramLines(Lists.reverse(SPACE_SPLITTER.splitToList(args.get("lines"))));
        foundNPC.getHologram().createHologram();
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id"},
            name = "move",
            permission = "znpcs.cmd.move",
            help = {
                    " &f&l* &e/znpcs move -id <npc_id>"
            }
    )
    public void move(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        foundNPC.setLocation(sender.getPlayer().getLocation());
        foundNPC.setLastMove(System.nanoTime());
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id", "-type"},
            name = "type",
            permission = "znpcs.cmd.type",
            help = {
                    " &f&l* &e/znpcs type -id <npc_id> -type ZOMBIE"
            }
    )
    public void changeType(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        ZNPCType npcType = ZNPCType.valueOf(args.get("type").toUpperCase());

        if (npcType != ZNPCType.PLAYER && npcType.getConstructor() == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.UNSUPPORTED_ENTITY);
            return;
        }

        foundNPC.changeType(npcType);
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id", "-add", "-remove", "-cooldown", "-list"},
            name = "action",
            permission = "znpcs.cmd.action",
            help = {
                    " &f&l* &e/znpcs action -id <npc_id> -add SERVER skywars",
                    " &f&l* &e/znpcs action -id <npc_id> -add CMD spawn",
                    " &f&l* &e/znpcs action -id <npc_id> -remove <action_id>",
                    " &f&l* &e/znpcs action -id <npc_id> -cooldown <action_id> <delay_in_seconds>"
            }
    )
    public void action(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        final List<ZNPCAction> actions = foundNPC.getNpcPojo().getClickActions();
        if (args.containsKey("add")) {
            List<String> split = SPACE_SPLITTER.splitToList(args.get("add"));

            if (split.size() < 2) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE_ACTION_ADD);
                return;
            }

            actions.add(new ZNPCAction(split.get(0).toUpperCase(), SPACE_JOINER.join(Iterables.skip(split, 1))));
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
        } else if (args.containsKey("remove")) {
            Integer actionId = Ints.tryParse(args.get("remove"));

            if (actionId == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            } else {
                if (actionId >= actions.size()) {
                    ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_ACTION_FOUND);
                    return;
                }
                actions.remove(actionId.intValue());
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
            }
        } else if (args.containsKey("cooldown")) {
            List<String> split = SPACE_SPLITTER.splitToList(args.get("cooldown"));

            if (split.size() < 2) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE_ACTION_DELAY);
                return;
            }

            Integer actionId = Ints.tryParse(split.get(0));
            Integer actionDelay = Ints.tryParse(split.get(1));

            if (actionId == null || actionDelay == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            } else {
                if (actionId >= actions.size()) {
                    ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_ACTION_FOUND);
                    return;
                }
                actions.get(actionId).setDelay(actionDelay);
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
            }
        } else if (actions.isEmpty()) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_ACTION_FOUND);
        } else {
            actions.forEach(s -> sender.sendMessage("&8(&a" + actions.indexOf(s) + "&8) &6" + s));
        }
    }

    @CommandInformation(
            aliases = {"-id", "-type", "-value"},
            name = "toggle",
            permission = "znpcs.cmd.toggle",
            help = {
                    " &f&l* &e/znpcs toggle -id <npc_id> -type look"
            }
    )
    public void toggle(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        ZNPCToggle npcToggle = ZNPCToggle.valueOf(args.get("type").toUpperCase());
        npcToggle.toggle(foundNPC, args.get("value"));
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-id", "-customize"},
            name = "customize",
            permission = "znpcs.cmd.customize",
            help = {
                    " &f&l* &e/znpcs customize -id <npc_id> -customize",
            }
    )
    public void customize(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        ZNPCType npcType = foundNPC.getNpcPojo().getNpcType();

        List<String> customizeOptions = SPACE_SPLITTER.splitToList(args.get("customize"));
        String methodName = customizeOptions.get(0);

        if (npcType.getCustomizationProcessor().contains(methodName)) {
            Method method = npcType.getCustomizationProcessor().getMethods().get(methodName);
            Iterable<String> split = Iterables.skip(customizeOptions, 1);
            if (Iterables.size(split) < method.getParameterTypes().length) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.TOO_FEW_ARGUMENTS);
                return;
            }

            try {
                String[] values = Iterables.toArray(split, String.class);

                npcType.updateCustomization(foundNPC, methodName, values);
                foundNPC.getNpcPojo().getCustomizationMap().put(methodName, values);

                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                throw new IllegalStateException("can't customize npc " + foundNPC.getNpcPojo().getId(), exception);
            }
        } else {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.METHOD_NOT_FOUND);
            for (Map.Entry<String, Method> method : npcType.getCustomizationProcessor().getMethods().entrySet()) {
                sender.sendMessage(ChatColor.YELLOW + method.getKey() + " " + SPACE_JOINER.join(method.getValue().getParameterTypes()));
            }
        }
    }

    @CommandInformation(
            aliases = {"-set", "-create", "-exit", "-id", "-path", "-list"},
            name = "path",
            permission = "znpcs.cmd.path",
            help = {
                    " &f&l* &e/znpcs path -create name",
                    " &f&l* &e/znpcs path -set <npc_id> -path name",
            }

    )
    public void path(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        ZUser znpcUser = ZUser.find(sender.getPlayer());
        if (znpcUser == null) {
            return;
        }

        if (args.containsKey("set")) {
            if (args.size() < 2) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE_PATH_SET);
                return;
            }

            Integer id = Ints.tryParse(args.get("set"));

            if (id == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
                return;
            }

            ZNPC foundNPC = ZNPC.find(id);

            if (foundNPC == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
                return;
            }

            foundNPC.setPath(AbstractTypeWriter.find(args.get("path")));
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
        } else if (args.containsKey("create")) {
            String pathName = args.get("create");

            if (pathName.length() < 3 || pathName.length() > 16) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NAME_LENGTH);
                return;
            }

            if (AbstractTypeWriter.find(pathName) != null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.PATH_FOUND);
                return;
            }

            if (znpcUser.isHasPath()) {
                sender.getPlayer().sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path -exit.");
                return;
            }

            AbstractTypeWriter.forCreation(pathName, znpcUser, TypeWriter.MOVEMENT);
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.START_PATH);
        } else if (args.containsKey("exit")) {
            znpcUser.setHasPath(false);
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.EXIT_PATH);;
        } else if (args.containsKey("list")) {
            if (AbstractTypeWriter.getPaths().isEmpty()) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_PATH_FOUND);
            } else {
                AbstractTypeWriter.getPaths().forEach(path -> sender.getPlayer().sendMessage(ChatColor.GREEN + path.getName()));
            }
        }
    }

    @CommandInformation(
            aliases = {"-id"},
            name = "teleport",
            permission = "znpcs.cmd.teleport",
            help = {
                    " &f&l* &e/znpcs teleport -id <npc_id>",
            }
    )
    public void teleport(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        // Teleport player to npc location
        sender.getPlayer().teleport(foundNPC.getLocation());
    }

    @CommandInformation(
            aliases = {"-id", "-height"},
            name = "height",
            permission = "znpcs.cmd.height",
            help = {
                    " &f&l* &e/znpcs height -id <npc_id> 2",
                    "&8Add more height to the hologram of the npc"
            }
    )
    public void changeHologramHeight(CommandSender sender, Map<String, String> args) {
        if (args.size() < 2) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }

        Integer id = Ints.tryParse(args.get("id"));

        if (id == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        ZNPC foundNPC = ZNPC.find(id);

        if (foundNPC == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
            return;
        }

        Double givenHeight = Doubles.tryParse(args.get("height"));

        if (givenHeight == null) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
            return;
        }

        foundNPC.getNpcPojo().setHologramHeight(givenHeight);
        foundNPC.getHologram().createHologram(); // Update hologram
        ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
    }

    @CommandInformation(
            aliases = {"-create", "-remove", "-gui", "-set"},
            name = "conversation",
            permission = "znpcs.cmd.conversation",
            help = {
                    " &f&l* &e/znpcs conversation -create first",
                    " &f&l* &e/znpcs conversation -remove first",
                    " &f&l* &e/znpcs conversation -set <npc_id> first [CLICK:RADIUS]",
                    " &f&l* &e/znpcs conversation -gui &8(&7Open a gui to manage the conversations&8)",
                    "&8RADIUS: &7it is activated when the player is near the npc",
                    "&8CLICK: &7it is activated when the player interacts with the npc"
            }
    )
    public void conversations(CommandSender sender, Map<String, String> args) {
        if (args.size() < 1) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE);
            return;
        }
        if (args.containsKey("create")) {
            String conversationName = args.get("create");
            if (Conversation.exists(conversationName)) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.CONVERSATION_FOUND);
                return;
            }
            if (conversationName.length() < 3 || conversationName.length() > 16) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NAME_LENGTH);
                return;
            }
            ConfigTypes.NPC_CONVERSATIONS.add(new Conversation(conversationName));
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
        } else if (args.containsKey("remove")) {
            String conversationName = args.get("remove");
            if (!Conversation.exists(conversationName)) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NO_CONVERSATION_FOUND);
                return;
            }
            ConfigTypes.NPC_CONVERSATIONS.remove(new Conversation(conversationName));
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
        } else if (args.containsKey("gui")) {
            sender.getPlayer().openInventory(new ConversationGUI(sender.getPlayer()).build());
        } else if (args.containsKey("set")) {
            List<String> split = SPACE_SPLITTER.splitToList(args.get("set"));
            if (split.size() < 2) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INCORRECT_USAGE_CONVERSATION_SET);
                return;
            }
            Integer id = Ints.tryParse(split.get(0));
            if (id == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.INVALID_NUMBER);
                return;
            }
            ZNPC foundNPC = ZNPC.find(id);
            if (foundNPC == null) {
                ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.NPC_NOT_FOUND);
                return;
            }
            String conversationName = split.get(1);
            if (Conversation.exists(conversationName)) {
                foundNPC.getNpcPojo().setConversation(new ConversationModel(conversationName, split.size() > 1 ? split.get(2) : "CLICK"));
            } else {
                foundNPC.getNpcPojo().setConversation(null);
            }
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender.getCommandSender(), ConfigValue.SUCCESS);
        }
    }
}
