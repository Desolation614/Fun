/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Item
 *  net.runelite.api.ItemComposition
 */
package net.runelite.client.plugins.cluescrolls.clues.item;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.client.plugins.cluescrolls.clues.item.ItemRequirement;

public class SingleItemRequirement
implements ItemRequirement {
    private final int itemId;

    public SingleItemRequirement(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean fulfilledBy(int itemId) {
        return this.itemId == itemId;
    }

    @Override
    public boolean fulfilledBy(Item[] items) {
        for (Item item : items) {
            if (item.getId() != this.itemId) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getCollectiveName(Client client) {
        ItemComposition definition = client.getItemDefinition(this.itemId);
        if (definition == null) {
            return "N/A";
        }
        return definition.getName();
    }
}

