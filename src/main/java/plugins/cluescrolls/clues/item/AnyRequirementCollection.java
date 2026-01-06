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

public class AnyRequirementCollection
implements ItemRequirement {
    private final String name;
    private final ItemRequirement[] requirements;

    public AnyRequirementCollection(String name, ItemRequirement ... requirements) {
        this.name = name;
        this.requirements = requirements;
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
            if (!requirement.fulfilledBy(items)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getCollectiveName(Client client) {
        return this.name;
    }
}

