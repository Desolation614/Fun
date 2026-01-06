/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.skillcalculator.skills.SkillAction;

public interface ItemSkillAction
extends SkillAction {
    public int getItemId();

    @Override
    default public int getIcon() {
        return this.getItemId();
    }

    @Override
    default public String getName(ItemManager itemManager) {
        return itemManager.getItemComposition(this.getItemId()).getMembersName();
    }

    @Override
    default public boolean isMembers(ItemManager itemManager) {
        return itemManager.getItemComposition(this.getItemId()).isMembers();
    }
}

