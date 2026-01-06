/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 */
package net.runelite.client.plugins.itemstats.potions;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.itemstats.StatBoost;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class StaminaPotion
extends StatBoost {
    public StaminaPotion() {
        super(Stats.RUN_ENERGY, false);
    }

    @Override
    public int heals(Client client) {
        Item ring;
        ItemContainer equipContainer = client.getItemContainer(94);
        if (equipContainer != null && (ring = equipContainer.getItem(EquipmentInventorySlot.RING.getSlotIdx())) != null && ring.getId() == 24736) {
            return 40;
        }
        return 20;
    }
}

