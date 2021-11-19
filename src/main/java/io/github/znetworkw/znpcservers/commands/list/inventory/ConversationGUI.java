package io.github.znetworkw.znpcservers.commands.list.inventory;

import io.github.znetworkw.znpcservers.configuration.Configuration;
import io.github.znetworkw.znpcservers.configuration.ConfigurationValue;
import io.github.znetworkw.znpcservers.npc.NPCAction;
import io.github.znetworkw.znpcservers.npc.conversation.Conversation;
import io.github.znetworkw.znpcservers.npc.conversation.ConversationKey;
import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.user.EventService;
import io.github.znetworkw.znpcservers.user.ZUser;
import io.github.znetworkw.znpcservers.utility.Utils;
import io.github.znetworkw.znpcservers.utility.inventory.ZInventory;
import io.github.znetworkw.znpcservers.utility.inventory.ZInventoryPage;
import io.github.znetworkw.znpcservers.utility.itemstack.ItemStackBuilder;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.List;

public class ConversationGUI extends ZInventory {
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
     * Creates a new inventory for the given player.
     *
     * @param player The player to create the inventory for.
     */
    public ConversationGUI(Player player) {
        super(player);
        setCurrentPage(new MainPage(this));
    }

    /**
     * Default/main page for gui.
     */
    static class MainPage extends ZInventoryPage {

        /**
         * Current Page ID.
         */
        int pageID = 1;

        /**
         * Creates a new page for the inventory.
         *
         * @param inventory The inventory to create the page for.
         */
        public MainPage(ZInventory inventory) {
            super(inventory, "Conversations", 6);
        }

        @Override
        public void update() {

            List<Conversation> npc_convs = ConfigurationConstants.NPC_CONVERSATIONS;

            if (pageID > 1) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Previous page")
                                .build(),
                        getRows() - 6,
                        clickEvent -> {
                            pageID -= 1;
                            openInventory();
                        });
            }
            if (npc_convs.size() > 45 * pageID) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Next page")
                                .build(),
                        getRows() - 4,
                        clickEvent -> {
                            pageID += 1;
                            openInventory();
                        });
            }
            int slots = (getRows() - 9) * pageID;
            int min = Math.min(slots, npc_convs.size());

            for (int i = slots - (getRows() - 9); i < min; i++) {
                Conversation conversation = ConfigurationConstants.NPC_CONVERSATIONS.get(i);
                addItem(ItemStackBuilder.forMaterial(Material.PAPER)
                        .setName(ChatColor.GREEN + conversation.getName())
                        .setLore("&7this conversation has &b" + conversation.getTexts().size() + " &7texts,"
                            , "&7it will activate when a player is on a &b" + conversation.getRadius() + "x" + conversation.getRadius() + " &7radius,"
                            , "&7or when a player interacts with an npc."
                            , "&7when the conversation is finish, there is a &b" + conversation.getDelay() + "s &7delay to start again."
                            , "&f&lUSES"
                            , " &bLeft-click &7to manage texts.", " &bRight-click &7to add a new text."
                            , " &bQ &7to change the radius.", " &bMiddle-click &7to change the cooldown.")
                        .build(),
                    i - ((getRows() - 9) * (pageID - 1)), clickEvent -> {
                        if (clickEvent.getClick() == ClickType.DROP) {
                            Utils.sendTitle(getPlayer(), "&b&lCHANGE RADIUS", "&7Type the new radius...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                    if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation)) {
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                        return;
                                    }
                                    Integer radius = Ints.tryParse(event.getMessage());
                                    if (radius == null) {
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                        return;
                                    }
                                    // delay must be >0
                                    if (radius < 0) {
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                        return;
                                    }
                                    conversation.setRadius(radius);
                                    Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                }).addConsumer(event -> openInventory());
                        } else if (clickEvent.isRightClick()) {
                            Utils.sendTitle(getPlayer(), "&e&lADD LINE", "&7Type the new line...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                        if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation)) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                            return;
                                        }
                                        conversation.getTexts().add(new ConversationKey(event.getMessage()));
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                    }
                                ).addConsumer(event -> openInventory());
                        } else if (clickEvent.isLeftClick()) {
                            new EditConversationPage(getInventory(), conversation).openInventory();
                        } else if (clickEvent.getClick() == ClickType.MIDDLE) {
                            Utils.sendTitle(getPlayer(), "&6&lCHANGE COOLDOWN", "&7Type the new cooldown...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                        if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation)) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                            return;
                                        }
                                        Integer cooldown = Ints.tryParse(event.getMessage());
                                        if (cooldown == null) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                            return;
                                        }
                                        // cooldown must be >0
                                        if (cooldown < 0) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                            return;
                                        }
                                        conversation.setDelay(cooldown);
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                    }
                                ).addConsumer(event -> openInventory());
                        }
                    }
                );
            }
        }
    }

    /**
     * Manages the conversations.
     */
    static class EditConversationPage extends ZInventoryPage {

        /**
         * Current Page ID.
         */
        int pageID = 1;

        /**
         * The conversation key to edit.
         */
        private final Conversation conversation;

        /**
         * Creates a new page for the inventory.
         *
         * @param inventory    The inventory to create the page for.
         * @param conversation The conversation to edit in the page.
         */
        public EditConversationPage(ZInventory inventory,
                                    Conversation conversation) {
            super(inventory, "Editing conversation " + conversation.getName(), 6);
            this.conversation = conversation;
        }

        @Override
        public void update() {

            if (pageID > 1) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Previous page")
                                .build(),
                        getRows() - 6,
                        clickEvent -> {
                            pageID -= 1;
                            openInventory();
                        });
            }
            if (conversation.getTexts().size() > 45 * pageID) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Next page")
                                .build(),
                        getRows() - 4,
                        clickEvent -> {
                            pageID += 1;
                            openInventory();
                        });
            }
            int slots = (getRows() - 9) * pageID;
            int min = Math.min(slots, conversation.getTexts().size());

            for (int i = slots - (getRows() - 9); i < min; i++) {
                ConversationKey conversationKey = conversation.getTexts().get(i);
                addItem(ItemStackBuilder.forMaterial(Material.NAME_TAG)
                        .setName(ChatColor.AQUA + conversationKey.getTextFormatted() + "....")
                        .setLore("&7this conversation text has a delay of &b" + conversationKey.getDelay() + "s &7to be executed,"
                            , "&7the sound for the text is &b" + (conversationKey.getSoundName() == null ? "NONE" : conversationKey.getSoundName()) + "&7,"
                            , "&7before sending the text there is a delay of &b" + conversationKey.getDelay() + "s"
                            , "&7the index for the text is &b" + i + "&7,"
                            , "&7and the conversation has currently &b" + conversationKey.getActions().size() + " actions&7."
                            , "&f&lUSES"
                            , " &bLeft-click &7to change the position.", " &bRight-click &7to remove text."
                            , " &bLeft-Shift-click &7to change the sound.", " &bMiddle-click &7to change the delay."
                            , " &bRight-Shift-click &7to edit the text.",
                            " &bQ &7to manage actions.")
                        .build(),
                    i - ((getRows() - 9) * (pageID - 1)), clickEvent -> {
                        if (clickEvent.getClick() == ClickType.SHIFT_LEFT) {
                            Utils.sendTitle(getPlayer(), "&c&lCHANGE SOUND", "&7Type the new sound...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                    if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation) ||
                                        !conversation.getTexts().contains(conversationKey)) {
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                        return;
                                    }
                                    String sound = event.getMessage().trim();
                                    conversationKey.setSoundName(sound);
                                    Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                }).addConsumer(event -> openInventory());
                        } else if (clickEvent.getClick() == ClickType.SHIFT_RIGHT) {
                            Utils.sendTitle(getPlayer(), "&a&lEDIT TEXT", "&7Type the new text...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                        if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation) ||
                                            !conversation.getTexts().contains(conversationKey)) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                            return;
                                        }
                                        conversationKey.getLines().clear();
                                        conversationKey.getLines().addAll(SPACE_SPLITTER.splitToList(event.getMessage()));
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                    }
                                ).addConsumer(event -> openInventory());
                        } else if (clickEvent.isLeftClick()) {
                            Utils.sendTitle(getPlayer(), "&e&lCHANGE POSITION &a>=" + 0 + "&c<=" + conversation.getTexts().size(),
                                "&7Type the new position...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                        if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation) ||
                                            !conversation.getTexts().contains(conversationKey)) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                            return;
                                        }
                                        Integer position = Ints.tryParse(event.getMessage());
                                        if (position == null) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                            return;
                                        }
                                        // check if position is within conversation texts size
                                        if (position < 0 || position > conversation.getTexts().size() - 1) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_SIZE);
                                            return;
                                        }
                                        Collections.swap(conversation.getTexts(), conversation.getTexts().indexOf(conversationKey), position);
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                    }
                                ).addConsumer(event -> openInventory());
                        } else if (clickEvent.isRightClick()) {
                            conversation.getTexts().remove(conversationKey);
                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                            // update gui
                            openInventory();
                        } else if (clickEvent.getClick() == ClickType.MIDDLE) {
                            Utils.sendTitle(getPlayer(), "&d&lCHANGE DELAY", "&7Type the new delay...");
                            EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                                .addConsumer(event -> {
                                        if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation) ||
                                            !conversation.getTexts().contains(conversationKey)) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                            return;
                                        }
                                        Integer delay = Ints.tryParse(event.getMessage());
                                        if (delay == null) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                            return;
                                        }
                                        // delay must be >0
                                        if (delay < 0) {
                                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INVALID_NUMBER);
                                            return;
                                        }
                                        conversationKey.setDelay(delay);
                                        Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                                    }
                                ).addConsumer(event -> openInventory());
                        } else if (clickEvent.getClick() == ClickType.DROP) {
                            new ActionManagementPage(getInventory(), conversation, conversationKey).openInventory();
                        }
                    }
                );
            }
        }
    }

    /**
     * Manages a conversation actions.
     */
    static class ActionManagementPage extends ZInventoryPage {
        /**
         * The conversation.
         */
        private final Conversation conversation;

        /**
         * The conversation text to edit.
         */
        private final ConversationKey conversationKey;

        /**
         * Current Page ID.
         */
        int pageID = 1;

        /**
         * Creates a new page for the inventory.
         *
         * @param inventory       The inventory to create the page for.
         * @param conversation    The conversation.
         * @param conversationKey The conversation text to edit in the page.
         */
        public ActionManagementPage(ZInventory inventory,
                                    Conversation conversation,
                                    ConversationKey conversationKey) {
            super(inventory, "Editing " + conversationKey.getTextFormatted(), 6);
            this.conversation = conversation;
            this.conversationKey = conversationKey;
        }

        @Override
        public void update() {

            if (pageID > 1) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Previous page")
                                .build(),
                        getRows() - 6,
                        clickEvent -> {
                            pageID -= 1;
                            openInventory();
                        });
            }
            if (conversationKey.getActions().size() > 45 *pageID) {
                addItem(ItemStackBuilder.forMaterial(Material.ARROW)
                                .setName(ChatColor.GRAY + "Next page")
                                .build(),
                        getRows() - 4,
                        clickEvent -> {
                            pageID += 1;
                            openInventory();
                        });
            }
            int slots = (getRows() - 9) * pageID;
            int min = Math.min(slots, conversationKey.getActions().size());

            for (int i = slots - (getRows() - 9); i < min; i++) {
                NPCAction znpcAction = conversationKey.getActions().get(i);
                addItem(ItemStackBuilder.forMaterial(Material.ANVIL)
                        .setName(ChatColor.AQUA + znpcAction.getAction().substring(0, Math.min(znpcAction.getAction().length(), 24)) + "....")
                        .setLore("&7this action type is &b" + znpcAction.getActionType()
                            , "&f&lUSES"
                            , " &bRight-click &7to remove text.")
                        .build(),
                    i - ((getRows() - 9) * (pageID - 1)), clickEvent -> {
                        if (clickEvent.isRightClick()) {
                            conversationKey.getActions().remove(znpcAction);
                            Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                            openInventory();
                        }
                    }
                );
            }
            // item for creating a new action for the conversation
            addItem(ItemStackBuilder.forMaterial(Material.EMERALD)
                    .setName(ChatColor.AQUA + "ADD A NEW ACTION")
                    .setLore("&7click here...")
                    .build(),
                getRows() - 5, clickEvent -> {
                    Utils.sendTitle(getPlayer(), "&d&lADD ACTION", "&7Type the action...");
                    EventService.addService(ZUser.find(getPlayer()), AsyncPlayerChatEvent.class)
                        .addConsumer(event -> {
                                if (!ConfigurationConstants.NPC_CONVERSATIONS.contains(conversation) ||
                                    !conversation.getTexts().contains(conversationKey)) {
                                    Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.NO_CONVERSATION_FOUND);
                                    return;
                                }
                                List<String> stringList = SPACE_SPLITTER.splitToList(event.getMessage());
                                if (stringList.size() < 2) {
                                    Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.INCORRECT_USAGE);
                                    return;
                                }
                                conversationKey.getActions().add(new NPCAction(stringList.get(0).toUpperCase(), SPACE_JOINER.join(Iterables.skip(stringList, 1))));
                                Configuration.MESSAGES.sendMessage(getPlayer(), ConfigurationValue.SUCCESS);
                            }
                        ).addConsumer(event -> openInventory());
                }
            );
        }
    }
}