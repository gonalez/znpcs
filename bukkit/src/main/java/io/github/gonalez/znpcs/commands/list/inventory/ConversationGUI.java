package io.github.gonalez.znpcs.commands.list.inventory;

import static io.github.gonalez.znpcs.ZNPConfigUtils.getConfig;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.configuration.ConversationsConfiguration;
import io.github.gonalez.znpcs.configuration.MessagesConfiguration;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.npc.conversation.Conversation;
import io.github.gonalez.znpcs.npc.conversation.ConversationKey;
import io.github.gonalez.znpcs.user.EventService;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.Utils;
import io.github.gonalez.znpcs.utility.inventory.ZInventory;
import io.github.gonalez.znpcs.utility.inventory.ZInventoryPage;
import io.github.gonalez.znpcs.utility.itemstack.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;

public class ConversationGUI extends ZInventory {

  private static final String WHITESPACE = " ";

  private static final Splitter SPACE_SPLITTER = Splitter.on(" ");

  private static final Joiner SPACE_JOINER = Joiner.on(" ");

  public ConversationGUI(Player player) {
    super(player);
    setCurrentPage(new MainPage(this));
  }

  static class MainPage extends ZInventoryPage {

    public MainPage(ZInventory inventory) {
      super(inventory, "Conversations", 5);
    }

    public void update() {
      List<Conversation> conversations = ZNPConfigUtils.getConfig(ConversationsConfiguration.class).conversationList;
      for (int i = 0; i < conversations.size(); ++i) {
        Conversation conversation = conversations.get(i);
        this.addItem(ItemStackBuilder.forMaterial(Material.PAPER)
            .setName(ChatColor.GREEN + conversation.getName())
            .setLore("&7this conversation has &b" + conversation.getTexts().size() + " &7texts,",
                "&7it will activate when a player is on a &b" + conversation.getRadius() + "x"
                    + conversation.getRadius() + " &7radius,",
                "&7or when a player interacts with an npc.",
                "&7when the conversation is finish, there is a &b" + conversation.getDelay()
                    + "s &7delay to start again.", "&f&lUSES", " &bLeft-click &7to manage texts.",
                " &bRight-click &7to add a new text.", " &bQ &7to change the radius.",
                " &bMiddle-click &7to change the cooldown.").build(), i, (clickEvent) -> {
          if (clickEvent.getClick() == ClickType.DROP) {
            Utils.sendTitle(this.getPlayer(), "&b&lCHANGE RADIUS", "&7Type the new radius...");
            EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                .addConsumer((event) -> {
                  if (!conversations.contains(conversation)) {
                    Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                  } else {
                    Integer radius = Ints.tryParse(event.getMessage());
                    if (radius == null || radius < 0) {
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).invalidNumber);
                    } else {
                      conversation.setRadius(radius);
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                    }
                  }
                }).addConsumer((event) -> {
                  this.openInventory();
                });
          } else if (clickEvent.isRightClick()) {
            Utils.sendTitle(this.getPlayer(), "&e&lADD LINE", "&7Type the new line...");
            EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                .addConsumer((event) -> {
                  if (!conversations.contains(conversation)) {
                    Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                  } else {
                    conversation.getTexts().add(new ConversationKey(event.getMessage()));
                    Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                  }
                }).addConsumer((event) -> {
                  this.openInventory();
                });
          } else if (clickEvent.isLeftClick()) {
            (new EditConversationPage(this.getInventory(), conversation)).openInventory();
          } else if (clickEvent.getClick() == ClickType.MIDDLE) {
            Utils.sendTitle(this.getPlayer(), "&6&lCHANGE COOLDOWN", "&7Type the new cooldown...");
            EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                .addConsumer((event) -> {
                    if (!conversations.contains(conversation)) {
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                    } else {
                    Integer cooldown = Ints.tryParse(event.getMessage());
                    if (cooldown == null || cooldown < 0) {
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).invalidNumber);
                    } else {
                      conversation.setDelay(cooldown);
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                    }
                  }
                }).addConsumer((event) -> {
                  this.openInventory();
                });
          }
        });
      }
    }

    static class EditConversationPage extends ZInventoryPage {
      private final Conversation conversation;

      public EditConversationPage(ZInventory inventory, Conversation conversation) {
        super(inventory, "Editing conversation " + conversation.getName(), 5);
        this.conversation = conversation;
      }

      public void update() {
        List<Conversation> conversations = ZNPConfigUtils.getConfig(ConversationsConfiguration.class).conversationList;
        for (int i = 0; i < this.conversation.getTexts().size(); ++i) {
          ConversationKey conversationKey = this.conversation.getTexts().get(i);
          this.addItem(ItemStackBuilder.forMaterial(Material.NAME_TAG)
                  .setName(ChatColor.AQUA + conversationKey.getTextFormatted() + "....").setLore(
                      "&7this conversation text has a delay of &b" + conversationKey.getDelay()
                          + "s &7to be executed,",
                      "&7the sound for the text is &b" + (conversationKey.getSoundName() == null
                          ? "NONE" : conversationKey.getSoundName()) + "&7,",
                      "&7before sending the text there is a delay of &b" + conversationKey.getDelay()
                          + "s", "&7the index for the text is &b" + i + "&7,",
                      "&7and the conversation has currently &b" + conversationKey.getActions().size()
                          + " actions&7.", "&f&lUSES", " &bLeft-click &7to change the position.",
                      " &bRight-click &7to remove text.", " &bLeft-Shift-click &7to change the sound.",
                      " &bMiddle-click &7to change the delay.",
                      " &bRight-Shift-click &7to edit the text.", " &bQ &7to manage actions.").build(),
              i, (clickEvent) -> {
                if (clickEvent.getClick() == ClickType.SHIFT_LEFT) {
                  Utils.sendTitle(this.getPlayer(), "&c&lCHANGE SOUND", "&7Type the new sound...");
                  EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                      .addConsumer((event) -> {
                        if (conversations.contains(this.conversation)
                            && this.conversation.getTexts().contains(conversationKey)) {
                          String sound = event.getMessage().trim();
                          conversationKey.setSoundName(sound);
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                        } else {
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                        }
                      }).addConsumer((event) -> {
                        this.openInventory();
                      });
                } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                  Utils.sendTitle(this.getPlayer(), "&a&lEDIT TEXT", "&7Type the new text...");
                  EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                      .addConsumer((event) -> {
                        if (conversations.contains(this.conversation)
                            && this.conversation.getTexts().contains(conversationKey)) {
                          conversationKey.getLines().clear();
                          conversationKey.getLines()
                              .addAll(SPACE_SPLITTER.splitToList(event.getMessage()));
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                        } else {
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                        }
                      }).addConsumer((event) -> {
                        this.openInventory();
                      });
                } else if (clickEvent.isLeftClick()) {
                  Utils.sendTitle(this.getPlayer(),
                      "&e&lCHANGE POSITION &a>=0&c<=" + this.conversation.getTexts().size(),
                      "&7Type the new position...");
                  EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                      .addConsumer((event) -> {
                        if (conversations.contains(this.conversation)
                            && this.conversation.getTexts().contains(conversationKey)) {
                          Integer position = Ints.tryParse(event.getMessage());
                          if (position == null) {
                            Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).invalidNumber);
                          } else if (position >= 0
                              && position <= this.conversation.getTexts().size() - 1) {
                            Collections.swap(this.conversation.getTexts(),
                                this.conversation.getTexts().indexOf(conversationKey), position);
                            Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                          } else {
                            Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).invalidSize);
                          }
                        } else {
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                        }
                      }).addConsumer((event) -> {
                        this.openInventory();
                      });
                } else if (clickEvent.isRightClick()) {
                  this.conversation.getTexts().remove(conversationKey);
                  Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                  this.openInventory();
                } else if (clickEvent.getClick() == ClickType.MIDDLE) {
                  Utils.sendTitle(this.getPlayer(), "&d&lCHANGE DELAY", "&7Type the new delay...");
                  EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                      .addConsumer((event) -> {
                        if (conversations.contains(this.conversation)
                            && this.conversation.getTexts().contains(conversationKey)) {
                          Integer delay = Ints.tryParse(event.getMessage());
                          if (delay == null || delay < 0) {
                            Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).invalidNumber);
                          } else {
                            conversationKey.setDelay(delay);
                            Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                          }
                        } else {
                          Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                        }
                      }).addConsumer((event) -> {
                        this.openInventory();
                      });
                } else if (clickEvent.getClick() == ClickType.DROP) {
                  (new ActionManagementPage(this.getInventory(), this.conversation,
                      conversationKey)).openInventory();
                }

              });
        }
      }
    }

    static class ActionManagementPage extends ZInventoryPage {

      private final Conversation conversation;

      private final ConversationKey conversationKey;

      public ActionManagementPage(ZInventory inventory, Conversation conversation,
          ConversationKey conversationKey) {
        super(inventory, "Editing " + conversationKey.getTextFormatted(), 5);
        this.conversation = conversation;
        this.conversationKey = conversationKey;
      }


      public void update() {
        List<Conversation> conversations = ZNPConfigUtils.getConfig(ConversationsConfiguration.class).conversationList;
        for (int i = 0; i < this.conversationKey.getActions().size(); ++i) {
          NPCAction znpcAction = this.conversationKey.getActions().get(i);
          this.addItem(ItemStackBuilder.forMaterial(Material.ANVIL).setName(
                  ChatColor.AQUA + znpcAction.getAction()
                      .substring(0, Math.min(znpcAction.getAction().length(), 24)) + "....")
              .setLore("&7this action type is &b" + znpcAction.getActionType(), "&f&lUSES",
                  " &bRight-click &7to remove text.").build(), i, (clickEvent) -> {
            if (clickEvent.isRightClick()) {
              this.conversationKey.getActions().remove(znpcAction);
              Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
              this.openInventory();
            }
          });
        }
        this.addItem(ItemStackBuilder.forMaterial(Material.EMERALD)
                .setName(ChatColor.AQUA + "ADD A NEW ACTION").setLore("&7click here...").build(),
            this.getRows() - 5, (clickEvent) -> {
              Utils.sendTitle(this.getPlayer(), "&d&lADD ACTION", "&7Type the action...");
              EventService.addService(ZUser.find(this.getPlayer()), AsyncPlayerChatEvent.class)
                  .addConsumer((event) -> {
                    if (conversations.contains(this.conversation)
                        && this.conversation.getTexts().contains(this.conversationKey)) {
                      List<String> stringList = SPACE_SPLITTER.splitToList(event.getMessage());
                      if (stringList.size() < 2) {
                        Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).incorrectUsage);
                      } else {
                        this.conversationKey.getActions().add(
                            new NPCAction(stringList.get(0).toUpperCase(),
                                SPACE_JOINER.join(Iterables.skip(stringList, 1))));
                        Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).success);
                      }
                    } else {
                      Utils.sendMessage(getPlayer(), getConfig(MessagesConfiguration.class).noConversationFound);
                    }
                  }).addConsumer((event) -> {
                    this.openInventory();
                  });
            });
      }
    }
  }
}