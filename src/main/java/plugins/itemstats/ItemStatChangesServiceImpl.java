/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 */
package net.runelite.client.plugins.itemstats;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.ItemStatChanges;
import net.runelite.client.plugins.itemstats.ItemStatChangesService;

@Singleton
class ItemStatChangesServiceImpl
implements ItemStatChangesService {
    private final ItemStatChanges itemstatchanges;

    @Inject
    private ItemStatChangesServiceImpl(ItemStatChanges itemstatchanges) {
        this.itemstatchanges = itemstatchanges;
    }

    @Override
    public Effect getItemStatChanges(int id) {
        return this.itemstatchanges.get(id);
    }
}

