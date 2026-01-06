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

public class AllRequirementsCollection
implements ItemRequirement {
    private String name;
    private ItemRequirement[] requirements;

    public AllRequirementsCollection(String name, ItemRequirement ... requirements) {
        this.name = name;
        this.requirements = requirements;
    }

    public AllRequirementsCollection(ItemRequirement ... requirements) {
        this("N/A", requirements);
    }

    @Override
    public boolean fulfilledBy(int itemId) {
        for (ItemRequirement requirement : this.requirements) {
            if (!requirement.fulfilledBy(itemId)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean fulfilledBy(Item[] items) {
        for (ItemRequirement requirement : this.requirements) {
            if (requirement.fulfilledBy(items)) continue;
            return false;
        }
        return true;
    }

    @Override
    public String getCollectiveName(Client client) {
        return this.name;
    }
}

