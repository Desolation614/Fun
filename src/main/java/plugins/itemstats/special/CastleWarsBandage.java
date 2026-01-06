/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.runelite.api.Client
 *  net.runelite.api.EquipmentInventorySlot
 *  net.runelite.api.Item
 *  net.runelite.api.ItemContainer
 */
package net.runelite.client.plugins.itemstats.special;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.stream.Stream;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.client.plugins.itemstats.Builders;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class CastleWarsBandage
implements Effect {
    private static final ImmutableSet<Integer> BRACELETS = ImmutableSet.of((Object)11083, (Object)11081, (Object)11079);
    private static final double BASE_HP_PERC = 0.1;
    private static final double BRACELET_HP_PERC = 0.15;

    @Override
    public StatsChanges calculate(Client client) {
        ItemContainer equipmentContainer = client.getItemContainer(94);
        double percH = this.hasBracelet(equipmentContainer) ? 0.15 : 0.1;
        StatChange hitPoints = Builders.heal(Stats.HITPOINTS, Builders.perc(percH, 0)).effect(client);
        StatChange runEnergy = Builders.heal(Stats.RUN_ENERGY, 30).effect(client);
        StatsChanges changes = new StatsChanges(2);
        changes.setStatChanges(new StatChange[]{hitPoints, runEnergy});
        changes.setPositivity(Stream.of(changes.getStatChanges()).map(StatChange::getPositivity).max(Comparator.naturalOrder()).get());
        return changes;
    }

    private boolean hasBracelet(ItemContainer equipmentContainer) {
        if (equipmentContainer == null) {
            return false;
        }
        Item gloves = equipmentContainer.getItem(EquipmentInventorySlot.GLOVES.getSlotIdx());
        if (gloves != null) {
            return BRACELETS.contains((Object)gloves.getId());
        }
        return false;
    }
}

