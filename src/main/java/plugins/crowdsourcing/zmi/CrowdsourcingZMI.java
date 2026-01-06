/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultiset
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 *  net.runelite.api.MenuAction
 *  net.runelite.api.Skill
 *  net.runelite.api.events.ChatMessage
 *  net.runelite.api.events.ItemContainerChanged
 *  net.runelite.api.events.MenuOptionClicked
 *  net.runelite.api.events.StatChanged
 */
package net.runelite.client.plugins.crowdsourcing.zmi;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.MenuAction;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.crowdsourcing.CrowdsourcingManager;
import net.runelite.client.plugins.crowdsourcing.zmi.ZMIData;

public class CrowdsourcingZMI {
    private static final String CHAT_MESSAGE_ZMI = "You bind the temple's power into runes.";
    @Inject
    private CrowdsourcingManager manager;
    @Inject
    private Client client;
    private int gameTickZMI = -1;
    private int illegalActionTick = -1;
    private int previousRunecraftXp = 0;
    private int runecraftXpGained = 0;
    private Multiset<Integer> previousInventorySnapshot;

    private Multiset<Integer> getInventorySnapshot() {
        ItemContainer inventory = this.client.getItemContainer(93);
        HashMultiset inventorySnapshot = HashMultiset.create();
        if (inventory != null) {
            Arrays.stream(inventory.getItems()).forEach(arg_0 -> CrowdsourcingZMI.lambda$getInventorySnapshot$0((Multiset)inventorySnapshot, arg_0));
        }
        return inventorySnapshot;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        MenuAction action = menuOptionClicked.getMenuAction();
        if (menuOptionClicked.isItemOp()) {
            this.illegalActionTick = this.client.getTickCount();
            return;
        }
        switch (action) {
            case ITEM_FIRST_OPTION: 
            case ITEM_SECOND_OPTION: 
            case ITEM_THIRD_OPTION: 
            case ITEM_FOURTH_OPTION: 
            case ITEM_FIFTH_OPTION: 
            case GROUND_ITEM_FIRST_OPTION: 
            case GROUND_ITEM_SECOND_OPTION: 
            case GROUND_ITEM_THIRD_OPTION: 
            case GROUND_ITEM_FOURTH_OPTION: 
            case GROUND_ITEM_FIFTH_OPTION: {
                this.illegalActionTick = this.client.getTickCount();
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getMessage().equals(CHAT_MESSAGE_ZMI)) {
            this.gameTickZMI = this.client.getTickCount();
            this.previousRunecraftXp = this.client.getSkillExperience(Skill.RUNECRAFT);
            this.previousInventorySnapshot = this.getInventorySnapshot();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (this.gameTickZMI == this.client.getTickCount()) {
            int currentRunecraftXp = statChanged.getXp();
            this.runecraftXpGained = currentRunecraftXp - this.previousRunecraftXp;
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        int itemContainerChangedTick = this.client.getTickCount();
        if (event.getItemContainer() != this.client.getItemContainer(93) || this.gameTickZMI != itemContainerChangedTick) {
            return;
        }
        int tickDelta = itemContainerChangedTick - this.illegalActionTick;
        boolean ardougneMedium = this.client.getVarbitValue(4459) == 1;
        int runecraftBoostedLevel = this.client.getBoostedSkillLevel(Skill.RUNECRAFT);
        Multiset<Integer> currentInventorySnapshot = this.getInventorySnapshot();
        Multiset itemsReceived = Multisets.difference(currentInventorySnapshot, this.previousInventorySnapshot);
        Multiset itemsRemoved = Multisets.difference(this.previousInventorySnapshot, currentInventorySnapshot);
        ZMIData data = new ZMIData(tickDelta, ardougneMedium, runecraftBoostedLevel, this.runecraftXpGained, (Multiset<Integer>)itemsReceived, (Multiset<Integer>)itemsRemoved);
        this.manager.storeEvent(data);
    }

    private static /* synthetic */ void lambda$getInventorySnapshot$0(Multiset inventorySnapshot, Item item) {
        inventorySnapshot.add((Object)item.getId(), item.getQuantity());
    }
}

