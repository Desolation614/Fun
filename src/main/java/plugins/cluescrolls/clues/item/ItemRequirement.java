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

public interface ItemRequirement {
    public boolean fulfilledBy(int var1);

    public boolean fulfilledBy(Item[] var1);

    public String getCollectiveName(Client var1);
}

