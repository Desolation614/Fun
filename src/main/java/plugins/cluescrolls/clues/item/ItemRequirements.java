/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.EquipmentInventorySlot
 */
package net.runelite.client.plugins.cluescrolls.clues.item;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.cluescrolls.clues.item.AllRequirementsCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.AnyRequirementCollection;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.MultipleOfItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.RangeItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.SingleItemRequirement;
import net.runelite.client.plugins.cluescrolls.clues.item.SlotLimitationRequirement;

public class ItemRequirements {
    public static SingleItemRequirement item(int itemId) {
        return new SingleItemRequirement(itemId);
    }

    public static RangeItemRequirement range(int startItemId, int endItemId) {
        return ItemRequirements.range(null, startItemId, endItemId);
    }

    public static RangeItemRequirement range(String name, int startItemId, int endItemId) {
        return new RangeItemRequirement(name, startItemId, endItemId);
    }

    public static AnyRequirementCollection any(String name, ItemRequirement ... requirements) {
        return new AnyRequirementCollection(name, requirements);
    }

    public static AllRequirementsCollection all(ItemRequirement ... requirements) {
        return new AllRequirementsCollection(requirements);
    }

    public static AllRequirementsCollection all(String name, ItemRequirement ... requirements) {
        return new AllRequirementsCollection(name, requirements);
    }

    public static SlotLimitationRequirement emptySlot(String description, EquipmentInventorySlot ... slots) {
        return new SlotLimitationRequirement(description, slots);
    }

    public static MultipleOfItemRequirement xOfItem(int itemId, int quantity) {
        return new MultipleOfItemRequirement(itemId, quantity);
    }
}

