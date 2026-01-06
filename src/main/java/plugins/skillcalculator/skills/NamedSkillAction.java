/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;

public interface NamedSkillAction
extends SkillAction {
    public String getName();

    @Override
    default public String getName(ItemManager itemManager) {
        return this.getName();
    }
}

