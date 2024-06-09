package io.github.gonalez.znpcs.commands.list;

import static io.github.gonalez.znpcs.utility.Utils.toColor;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.commands.Command;
import io.github.gonalez.znpcs.commands.CommandInformation;
import io.github.gonalez.znpcs.commands.CommandSenderUtil;
import io.github.gonalez.znpcs.commands.list.inventory.ConversationGUI;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.configuration.ConfigurationValue;
import io.github.gonalez.znpcs.npc.FunctionContext;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.npc.NPCFunction;
import io.github.gonalez.znpcs.npc.NPCModel;
import io.github.gonalez.znpcs.npc.NPCPath;
import io.github.gonalez.znpcs.npc.NPCSkin;
import io.github.gonalez.znpcs.npc.NPCType;
import io.github.gonalez.znpcs.npc.conversation.Conversation;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.skin.SkinFetcherResult;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.PlaceholderUtils;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultCommand extends Command {
  private static final Joiner SPACE_JOINER = Joiner.on(" ");

  private static final SkinFunction DO_APPLY_SKIN = new SkinFunction() {
    @Override
    public void apply(CommandSender commandSender, NPC npc, String skinName,
                      @Nullable SkinFetcherResult result) {
      NPCSkin.forName(skinName, (paramArrayOfString, throwable) -> {
        if (throwable != null) {
          Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.CANT_GET_SKIN, skinName);
        } else {
          npc.changeSkin(NPCSkin.forValues(paramArrayOfString));
          Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.GET_SKIN);
        }
        if (result != null) result.onDone(paramArrayOfString, throwable);
      });
    }
  };

  private final Path path;

  public DefaultCommand(Path path) {
    super("znpcs");
    this.path = path;
  }
  
  @CommandInformation(arguments = {}, name = "", permission = "")
  public void defaultCommand(CommandSender sender, ImmutableList<String> args) {
    sender.sendMessage(toColor("&6&m------------------------------------------"));
    sender.sendMessage(toColor("&b&lZNPCS &8» &7ZNetwork"));
    sender.sendMessage(toColor("&6https://www.spigotmc.org/resources/znpcs.80940"));
    Objects.requireNonNull(sender);
    getCommands().forEach(commandInformation -> CommandSenderUtil.sendMessage(sender, commandInformation));
    sender.sendMessage(ChatColor.DARK_GRAY + "Hover over the commands to see help for the command.");
    sender.sendMessage(toColor("&6&m------------------------------------------"));
  }
  
  @CommandInformation(arguments = {"id", "type", "name"}, name = "create", permission = "znpcs.cmd.create", help = {" &f&l* &e/znpcs create <npc_id> PLAYER Qentin"})
  public void createNPC(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 3) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    }
    boolean foundNPC = ConfigurationConstants.NPC_LIST.stream().anyMatch(npc -> (npc.getId() == id));
    if (foundNPC) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_FOUND);
      return;
    }
    NPCType npcType = NPCType.valueOf(args.get(1).toUpperCase());
    String name = args.get(2).trim();
    if (name.length() < 3 || name.length() > 16) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
      return;
    }
    NPC npc = ServersNPC.createNPC(id, npcType, ((Player)sender).getLocation(), name);
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
    if (npcType == NPCType.PLAYER) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.FETCHING_SKIN, name);
      DO_APPLY_SKIN.apply(sender, npc, name, null);

      if (args.size() >= 3) {
        sender.sendMessage("");
      }
    }
  }
  
  @CommandInformation(arguments = {"id"}, name = "delete", permission = "znpcs.cmd.delete", help = {" &f&l* &e/znpcs delete <npc_id>"})
  public void deleteNPC(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }
    if (!args.get(1).contains("true")){
      sender.sendMessage("Do you really want to delete the NPC? Type the command again with true behind it");
      return;
    }
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    ServersNPC.deleteNPC(id);
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {}, name = "list", permission = "znpcs.cmd.list")
  public void list(CommandSender sender, ImmutableList<String> args) {
    if (ConfigurationConstants.NPC_LIST.isEmpty()) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_NPC_FOUND);
    } else {
      sender.sendMessage(ChatColor.DARK_GREEN + "NPC list:");
      for (NPCModel npcModel : ConfigurationConstants.NPC_LIST) {
        List<BaseComponent> parts = new ArrayList<>();

        String message = "- "
            + npcModel.getId() + " "
            + npcModel.getHologramLines().toString()
            + " (" + npcModel.getLocation().getWorldName() + " "
            + (int)npcModel.getLocation().getX() + " "
            + (int)npcModel.getLocation().getY() + " "
            + (int)npcModel.getLocation().getZ() + ") ";
        TextComponent textComponent = new TextComponent(message);
        textComponent.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        parts.add(textComponent);

        TextComponent textComponent2 = new TextComponent("[TELEPORT]");
        textComponent2.setBold(true); textComponent2.setColor(net.md_5.bungee.api.ChatColor.DARK_GREEN);
        textComponent2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder("Click to teleport this npc!")
                .color(net.md_5.bungee.api.ChatColor.GREEN).create()));
        textComponent2.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/znpcs teleport " + npcModel.getId()));
        parts.add(textComponent2);
        parts.add(new TextComponent(" "));

        TextComponent textComponent3 = new TextComponent("[DELETE]");
        textComponent3.setBold(true);
        textComponent3.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        textComponent3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new ComponentBuilder("Click to delete this npc!")
                .color(net.md_5.bungee.api.ChatColor.RED).create()));
        textComponent3.setClickEvent(
            new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/znpcs delete " + npcModel.getId()));
        parts.add(textComponent3);

        ((Player)sender).spigot().sendMessage(parts.toArray(new BaseComponent[0]));
      } 
    } 
  }
  
  @CommandInformation(arguments = {"id", "skin"},
      name = "skin", permission = "znpcs.cmd.skin",
      help = {
      " &f&l* &e/znpcs skin <npc_id> Notch",
      " &f&l* &e/znpcs skin <npc_id> %someplugin_balance_top_1% 60",
      "",
      "&7If &erefreshSkinDuration &7is present in the command arguments",
      "&7the skin will be 'refreshed' periodically every that duration, ",
      "&7in seconds. The skin will be refreshed using the skin name, ",
      "&7placeholders are supported, example: &e%someplugin_balance_top_1%"})
  public void setSkin(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    }

    String skin = args.get(1).trim();
    foundNPC.getNpcPojo().setSkinName(skin);

    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.FETCHING_SKIN, skin);
    DO_APPLY_SKIN.apply(sender, foundNPC, PlaceholderUtils.formatPlaceholders(skin), new SkinFetcherResult() {
      @Override
      public void onDone(String[] paramArrayOfString, Throwable paramThrowable) {
        if (args.size() == 3) {
          Integer refreshSkinDuration = Ints.tryParse(args.get(2));
          if (refreshSkinDuration != null) {
            foundNPC.getNpcPojo().setRefreshSkinDuration(refreshSkinDuration);
            sender.sendMessage(ChatColor.GREEN + "The skin will refresh every: " +
                ChatColor.YELLOW +  refreshSkinDuration + ChatColor.GREEN + " seconds.");
          }
        } else {
          foundNPC.getNpcPojo().setRefreshSkinDuration(0);
        }
      }
    });
  }
  
  @CommandInformation(arguments = {"id", "slot"}, name = "equip", permission = "znpcs.cmd.equip", help = {" &f&l* &e/znpcs equip <npc_id> [HAND,OFFHAND,HELMET,CHESTPLATE,LEGGINGS,BOOTS]", "&8(You need to have the item in your hand.)"})
  public void equip(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    foundNPC.getNpcPojo().getNpcEquip().put(
        ItemSlot.valueOf(args.get(1).toUpperCase()),
            ((Player)sender).getInventory().getItemInHand());
    foundNPC.getPackets().flushCache("equipPackets");
    Objects.requireNonNull(foundNPC);
    foundNPC.getViewers().forEach(foundNPC::sendEquipPackets);
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"id", "set", "add", "list", "remove"}, name = "lines",
      permission = "znpcs.cmd.lines",
      help = {
      "&f&l* &e/znpcs lines <npc_id> add Example Line",
      "&f&l* &e/znpcs lines <npc_id> remove 0",
      "&f&l* &e/znpcs lines <npc_id> set First Second Third-Space"
  })
  public void changeLines(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }

    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    }

    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    }

    if (args.get(1).equals("clear")) {
      foundNPC.getNpcPojo().getHologramLines().clear();
    } else {
      List<String> toAdd = new ArrayList<>(args.subList(2, args.size()));
      switch (args.get(1)) {
        case "set":
          Collections.reverse(toAdd);
          foundNPC.getNpcPojo().setHologramLines(toAdd);
          break;
        case "add":
          foundNPC.getNpcPojo().getHologramLines().add(SPACE_JOINER.join(toAdd));
          break;
        case "list":
          sender.sendMessage(ChatColor.YELLOW + "NPC Lines:");
          for (int i = 0; i < foundNPC.getNpcPojo().getHologramLines().size(); i++) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                String.format("&a%d &7- " + foundNPC.getNpcPojo().getHologramLines().get(i), i)));
          }
          break;
        case "remove":
          Integer num = Ints.tryParse(args.get(2));
          if (num == null) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
            return;
          }
          if (num >= foundNPC.getNpcPojo().getHologramLines().size()) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_LINE_FOUND);
            return;
          }
          foundNPC.getNpcPojo().getHologramLines().remove(
              foundNPC.getNpcPojo().getHologramLines().get(num));
          break;
        default:
          break;
      }
    }
    foundNPC.getHologram().createHologram();
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"id"}, name = "move", permission = "znpcs.cmd.move", help = {" &f&l* &e/znpcs move <npc_id>"})
  public void move(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    }

    foundNPC.getNpcPojo().setLocation(new ZLocation(((Player)sender).getLocation()));
    foundNPC.changeType(foundNPC.getNpcPojo().getNpcType());

    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"id", "type"}, name = "type", permission = "znpcs.cmd.type", help = {" &f&l* &e/znpcs type <npc_id> ZOMBIE"})
  public void changeType(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    NPCType npcType = NPCType.valueOf(args.get(1).toUpperCase());
    if (npcType != NPCType.PLAYER && npcType.getConstructor() == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.UNSUPPORTED_ENTITY);
      return;
    } 
    foundNPC.changeType(npcType);
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"add", "remove", "cooldown", "list"}, name = "action", isMultiple = true, permission = "znpcs.cmd.action",
      help = {
      " &f&l* &e/znpcs action <npc_id> add SERVER skywars",
          " &f&l* &e/znpcs action <npc_id> add CMD spawn",
          " &f&l* &e/znpcs action <npc_id> remove <action_id>",
          " &f&l* &e/znpcs action <npc_id> cooldown <action_id> <delay_in_seconds>", " &f&l* &e/znpcs action list <npc_id>"})
  public void action(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }

    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    }
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    }

    List<String> split = args.subList(1, args.size());
    switch (args.get(1)) {
      case "add":
        if (split.isEmpty()) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.ACTION_ADD_INCORRECT_USAGE);
          return;
        }
        foundNPC.getNpcPojo().getClickActions().add(new NPCAction(split.get(1).toUpperCase(), SPACE_JOINER.join(Iterables.skip(split, 2))));
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        break;
      case "remove":
        Integer actionId = Ints.tryParse(split.get(1));
        if (actionId == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
        } else {
          if (actionId >= foundNPC.getNpcPojo().getClickActions().size()) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
            return;
          }
          foundNPC.getNpcPojo().getClickActions().remove(actionId.intValue());
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        }
        break;
      case "cooldown":
        Integer actionDelay = Ints.tryParse(split.get(2));
        actionId = Ints.tryParse(split.get(1));
        if (actionId == null || id == null || actionDelay == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
        } else {
          if (actionId >= foundNPC.getNpcPojo().getClickActions().size()) {
            Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
            return;
          }
          foundNPC.getNpcPojo().getClickActions().get(actionId).setDelay(actionDelay);
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        }
        break;
      case "list":
        if (foundNPC.getNpcPojo().getClickActions().isEmpty()) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_ACTION_FOUND);
        } else {
          foundNPC.getNpcPojo().getClickActions().forEach(s -> sender.sendMessage("&8(&a" + foundNPC.getNpcPojo().getClickActions().indexOf(s) + "&8) &6" + s.toString()));
        }
        break;
      default:
        break;
    }
  }
  
  @CommandInformation(arguments = {"id", "type", "valueOptional"}, name = "toggle", permission = "znpcs.cmd.toggle", help = {" &f&l* &e/znpcs toggle <npc_id> look"})
  public void toggle(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    NPCFunction npcFunction = FunctionFactory.findFunctionForName(args.get(1));
    if (npcFunction.getName().equalsIgnoreCase("glow")) {
      npcFunction.doRunFunction(foundNPC, new FunctionContext.ContextWithValue(foundNPC, args.get(2)));
    } else {
      npcFunction.doRunFunction(foundNPC, new FunctionContext.DefaultContext(foundNPC));
    } 
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"id", "customizeValues"}, name = "customize", permission = "znpcs.cmd.customize", help = {" &f&l* &e/znpcs customize <npc_id> <customization>"})
  public void customize(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    NPCType npcType = foundNPC.getNpcPojo().getNpcType();
    List<String> customizeOptions = args.subList(1, args.size());
    String methodName = customizeOptions.get(0);
    if (npcType.getCustomizationLoader().contains(methodName)) {
      Method method = npcType.getCustomizationLoader().getMethods().get(methodName);
      Iterable<String> split = Iterables.skip(customizeOptions, 1);
      if (Iterables.size(split) < (method.getParameterTypes()).length) {
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.TOO_FEW_ARGUMENTS);
        return;
      } 
      String[] values = Iterables.toArray(split, String.class);
      npcType.updateCustomization(foundNPC, methodName, values);
      foundNPC.getNpcPojo().getCustomizationMap().put(methodName, values);
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
    } else {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.METHOD_NOT_FOUND);
      for (Map.Entry<String, Method> method : npcType.getCustomizationLoader().getMethods().entrySet())
        sender.sendMessage(ChatColor.YELLOW + method.getKey() + " " + SPACE_JOINER.join(method.getValue().getParameterTypes()));
    } 
  }
  
  @CommandInformation(arguments = {"set", "create", "exit", "path", "list"}, name = "path", isMultiple = true, permission = "znpcs.cmd.path", help = {" &f&l* &e/znpcs path create name", " &f&l* &e/znpcs path set <npc_id> name"})
  public void path(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }

    ZUser znpcUser = ZUser.find(((Player) sender).getPlayer());
    if (znpcUser == null)
      return;

    switch (args.get(0)) {
      case "set":
        Integer id = Ints.tryParse(args.get(1));
        if (id == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
          return;
        }

        NPC foundNPC = NPC.find(id);
        if (foundNPC == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
          return;
        }
        foundNPC.setPath(NPCPath.AbstractTypeWriter.find(args.get(2)));
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        break;
      case "create":
        String pathName = args.get(1);
        if (pathName.length() < 3 || pathName.length() > 16) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
          return;
        }
        if (NPCPath.AbstractTypeWriter.find(pathName) != null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.PATH_FOUND);
          return;
        }
        if (znpcUser.isHasPath()) {
          sender.sendMessage(ChatColor.RED + "You already have a path creator active, to remove it use /znpcs path exit.");
          return;
        }

        File file = path.resolve(pathName + ServersNPC.PATH_EXTENSION).toFile();
        NPCPath.AbstractTypeWriter.forCreation(file, znpcUser, NPCPath.AbstractTypeWriter.TypeWriter.MOVEMENT);
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.PATH_START);
        break;
      case "exit":
        znpcUser.setHasPath(false);
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.EXIT_PATH);
        break;
      case "list":
        if (NPCPath.AbstractTypeWriter.getPaths().isEmpty()) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_PATH_FOUND);
        } else {
          NPCPath.AbstractTypeWriter.getPaths().forEach(path -> sender.sendMessage(ChatColor.GREEN + path.getName()));
        }
        break;
      default:
        break;
    }
  }
  
  @CommandInformation(arguments = {"id"}, name = "teleport", permission = "znpcs.cmd.teleport", help = {" &f&l* &e/znpcs teleport <npc_id>"})
  public void teleport(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    ((Player)sender).teleport(foundNPC.getLocation());
  }
  
  @CommandInformation(arguments = {"id", "height"}, name = "height", permission = "znpcs.cmd.height", help = {" &f&l* &e/znpcs height <npc_id> 2", "&8Add more height to the hologram of the npc"})
  public void changeHologramHeight(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 2) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    } 
    Integer id = Ints.tryParse(args.get(0));
    if (id == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    NPC foundNPC = NPC.find(id);
    if (foundNPC == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
      return;
    } 
    Double givenHeight = Doubles.tryParse(args.get(1));
    if (givenHeight == null) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
      return;
    } 
    foundNPC.getNpcPojo().setHologramHeight(givenHeight);
    foundNPC.getHologram().createHologram();
    Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
  }
  
  @CommandInformation(arguments = {"create", "remove", "gui", "set"}, name = "conversation", isMultiple = true, permission = "znpcs.cmd.conversation", help = {" &f&l* &e/znpcs conversation create first", " &f&l* &e/znpcs conversation remove first", " &f&l* &e/znpcs conversation set <npc_id> first [CLICK:RADIUS]", " &f&l* &e/znpcs conversation gui &8(&7Open a gui to manage the conversations&8)", "&8RADIUS: &7it is activated when the player is near the npc", "&8CLICK: &7it is activated when the player interacts with the npc"})
  public void conversations(CommandSender sender, ImmutableList<String> args) {
    if (args.size() < 1) {
      Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INCORRECT_USAGE);
      return;
    }

    switch (args.get(0)) {
      case "create":
        String conversationName = args.get(1);
        if (Conversation.exists(conversationName)) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.CONVERSATION_FOUND);
          return;
        }
        if (conversationName.length() < 3 || conversationName.length() > 16) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NAME_LENGTH);
          return;
        }
        ConfigurationConstants.NPC_CONVERSATIONS.add(new Conversation(conversationName));
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        break;
      case "remove":
        conversationName = args.get(1);
        if (!Conversation.exists(conversationName)) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NO_CONVERSATION_FOUND);
          return;
        }
        ConfigurationConstants.NPC_CONVERSATIONS.remove(Conversation.forName(conversationName));
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        break;
      case "gui":
        ((Player)sender).openInventory((new ConversationGUI(((Player) sender))).build());
        break;
      case "set":
        List<String> split = args.subList(1, args.size());
        if (split.size() < 2) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.CONVERSATION_SET_INCORRECT_USAGE);
          return;
        }
        Integer id = Ints.tryParse(split.get(0));
        if (id == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.INVALID_NUMBER);
          return;
        }
        NPC foundNPC = NPC.find(id);
        if (foundNPC == null) {
          Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.NPC_NOT_FOUND);
          return;
        }
        conversationName = split.get(1);
        if (Conversation.exists(conversationName)) {
          foundNPC.getNpcPojo().setConversation(new ConversationModel(conversationName, (split.size() > 1) ? split.get(2) : "CLICK"));
        } else {
          foundNPC.getNpcPojo().setConversation(null);
        }
        Configuration.MESSAGES.sendMessage(sender, ConfigurationValue.SUCCESS);
        break;
      default:
        break;
    }
  }
  
  interface SkinFunction {
    void apply(CommandSender commandSender, NPC npc, String skinName, SkinFetcherResult result);
  }
}
