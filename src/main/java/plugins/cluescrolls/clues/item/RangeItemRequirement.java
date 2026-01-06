/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 */
package net.runelite.client.plugins.cluescrolls.clues.item;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

public class RangeItemRequirement
implements ItemRequirement {
    private final String name;
    private final int startItemId;
    private final int endItemId;

    public RangeItemRequirement(String name, int startItemId, int endItemId) {
        this.name = name;
        this.startItemId = startItemId;
        this.endItemId = endItemId;
    }

    @Override
    public boolean fulfilledBy(int itemId) {
        return itemId >= this.startItemId && itemId <= this.endItemId;
    }

    @Override
    public boolean fulfilledBy(Item[] items) {
        for (Item item : items) {
            if (item.getId() < this.startItemId || item.getId() > this.endItemId) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getCollectiveName(Client client) {
        return this.name;
    }
}

