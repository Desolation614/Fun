/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 */
package net.runelite.client.plugins.cluescrolls.clues.item;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

public class SlotLimitationRequirement
implements ItemRequirement {
    private final String description;
    private final EquipmentInventorySlot[] slots;

    public SlotLimitationRequirement(String description, EquipmentInventorySlot ... slots) {
        this.description = description;
        this.slots = slots;
    }

    @Override
    public boolean fulfilledBy(int itemId) {
        return false;
    }

    @Override
    public boolean fulfilledBy(Item[] items) {
        for (EquipmentInventorySlot slot : this.slots) {
            if (slot.getSlotIdx() >= items.length || items[slot.getSlotIdx()].getId() == -1) continue;
            return false;
        }
        return true;
    }

    @Override
    public String getCollectiveName(Client client) {
        return this.description;
    }
}

