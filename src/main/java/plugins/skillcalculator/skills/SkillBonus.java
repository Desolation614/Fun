/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.skillcalculator.skills;

import java.util.Collections;
import java.util.Set;

public interface SkillBonus {
    public String getName();

    public float getValue();

    default public Set<? extends SkillBonus> getCanBeStackedWith() {
        return Collections.emptySet();
    }
}

