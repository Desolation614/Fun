/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  net.runelite.api.ChatMessageType
 *  net.runelite.api.Client
 *  net.runelite.api.ItemComposition
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.GameStateChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.widgets.Widget
 *  net.runelite.api.widgets.WidgetUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.examine;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.examine.PendingExamine;
import net.runelite.client.util.QuantityFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="Examine", description="Shows additional examine information (eg. GE Average, HA Value)", tags={"npcs", "items", "inventory", "objects", "prices", "high alchemy"})
public class ExaminePlugin
extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ExaminePlugin.class);
    private final Deque<PendingExamine> pending = new ArrayDeque<PendingExamine>();
    @Inject
    private Client client;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ChatMessageManager chatMessageManager;

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        this.pending.clear();
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        int id;
        ChatMessageType type;
        if (!event.getMenuOption().equals("Examine")) {
            return;
        }
        int quantity = -1;
        switch (event.getMenuAction()) {
            case EXAMINE_ITEM_GROUND: {
                type = ChatMessageType.ITEM_EXAMINE;
                id = event.getId();
                break;
            }
            case CC_OP_LOW_PRIORITY: {
                type = ChatMessageType.ITEM_EXAMINE;
                int[] qi = this.findItemFromWidget(event.getParam1(), event.getParam0());
                if (qi == null) {
                    log.debug("Examine for item with unknown widget: {}", (Object)event);
                    return;
                }
                quantity = qi[0];
                id = qi[1];
                break;
            }
            default: {
                return;
            }
        }
        PendingExamine pendingExamine = new PendingExamine();
        pendingExamine.setResponseType(type);
        pendingExamine.setId(id);
        pendingExamine.setQuantity(quantity);
        this.pending.push(pendingExamine);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (this.pending.isEmpty()) {
            return;
        }
        PendingExamine pendingExamine = this.pending.poll();
        if (pendingExamine.getResponseType() != event.getType()) {
            log.debug("Type mismatch for pending examine: {} != {}", (Object)pendingExamine.getResponseType(), (Object)event.getType());
            this.pending.clear();
            return;
        }
        log.debug("Got examine type {} {}: {}", new Object[]{pendingExamine.getResponseType(), pendingExamine.getId(), event.getMessage()});
        int itemId = pendingExamine.getId();
        int itemQuantity = pendingExamine.getQuantity();
        if (itemId == 995) {
            return;
        }
        ItemComposition itemComposition = this.itemManager.getItemComposition(itemId);
        this.getItemPrice(itemComposition.getId(), itemComposition, itemQuantity);
    }

    private int[] findItemFromWidget(int widgetId, int childIdx) {
        int widgetGroup = WidgetUtil.componentToInterface((int)widgetId);
        Widget widget = this.client.getWidget(widgetId);
        if (widget == null) {
            return null;
        }
        if (387 == widgetGroup) {
            Widget widgetItem = widget.getChild(1);
            if (widgetItem != null) {
                return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
            }
        } else if (312 == widgetGroup) {
            Widget widgetItem = widget.getChild(2);
            if (widgetItem != null) {
                return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
            }
        } else if (300 == widgetGroup) {
            Widget widgetItem = widget.getChild(childIdx);
            if (widgetItem != null) {
                return new int[]{1, widgetItem.getItemId()};
            }
        } else {
            Widget widgetItem = widget.getChild(childIdx);
            if (widgetItem != null && widgetItem.getItemId() > -1) {
                return new int[]{widgetItem.getItemQuantity(), widgetItem.getItemId()};
            }
        }
        return null;
    }

    @VisibleForTesting
    void getItemPrice(int id, ItemComposition itemComposition, int quantity) {
        quantity = Math.max(1, quantity);
        int gePrice = this.itemManager.getItemPrice(id);
        int alchPrice = itemComposition.getHaPrice();
        if (gePrice > 0 || alchPrice > 0) {
            ChatMessageBuilder message = new ChatMessageBuilder().append(ChatColorType.NORMAL).append("Price of ").append(ChatColorType.HIGHLIGHT);
            if (quantity > 1) {
                message.append(QuantityFormatter.formatNumber(quantity)).append(" x ");
            }
            message.append(itemComposition.getMembersName()).append(ChatColorType.NORMAL).append(":");
            if (gePrice > 0) {
                message.append(ChatColorType.NORMAL).append(" GE average ").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber((long)gePrice * (long)quantity));
                if (quantity > 1) {
                    message.append(ChatColorType.NORMAL).append(" (").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber(gePrice)).append(ChatColorType.NORMAL).append("ea)");
                }
            }
            if (alchPrice > 0) {
                message.append(ChatColorType.NORMAL).append(" HA value ").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber((long)alchPrice * (long)quantity));
                if (quantity > 1) {
                    message.append(ChatColorType.NORMAL).append(" (").append(ChatColorType.HIGHLIGHT).append(QuantityFormatter.formatNumber(alchPrice)).append(ChatColorType.NORMAL).append("ea)");
                }
            }
            this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.ITEM_EXAMINE).runeLiteFormattedMessage(message.build()).build());
        }
    }
}

