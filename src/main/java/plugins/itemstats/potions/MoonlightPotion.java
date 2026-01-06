/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Client
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.itemstats.potions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.plugins.itemstats.Effect;
import net.runelite.client.plugins.itemstats.StatChange;
import net.runelite.client.plugins.itemstats.StatsChanges;
import net.runelite.client.plugins.itemstats.potions.StatEffect;
import net.runelite.client.plugins.itemstats.stats.Stat;
import net.runelite.client.plugins.itemstats.stats.Stats;

public class MoonlightPotion
implements Effect {
    @Override
    public StatsChanges calculate(Client client) {
        StatsChanges changes = new StatsChanges(4);
        ArrayList statChanges = new ArrayList();
        this.getStatChange(client, Stats.ATTACK).ifPresent(e -> statChanges.add(e.getEffect().effect(client)));
        this.getStatChange(client, Stats.DEFENCE).ifPresent(e -> statChanges.add(e.getEffect().effect(client)));
        this.getStatChange(client, Stats.STRENGTH).ifPresent(e -> statChanges.add(e.getEffect().effect(client)));
        this.getStatChange(client, Stats.PRAYER).ifPresent(e -> statChanges.add(e.getEffect().effect(client)));
        changes.setStatChanges((StatChange[])statChanges.toArray(StatChange[]::new));
        return changes;
    }

    private Optional<StatEffect> getStatChange(Client client, Stat stat) {
        return Arrays.stream(StatEffect.values()).filter(t -> t.getStat() == stat && client.getBoostedSkillLevel(Skill.HERBLORE) >= t.getLevelRequirement()).max(Comparator.comparingInt(StatEffect::getLevelRequirement));
    }
}

