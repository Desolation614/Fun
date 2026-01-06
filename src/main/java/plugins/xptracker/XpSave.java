/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.runelite.api.Skill
 */
package net.runelite.client.plugins.xptracker;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Skill;
import net.runelite.client.config.ConfigSerializer;
import net.runelite.client.plugins.xptracker.XpSaveSerializer;
import net.runelite.client.plugins.xptracker.XpSaveSingle;

@ConfigSerializer(value=XpSaveSerializer.class)
class XpSave {
    Map<Skill, XpSaveSingle> skills = new LinkedHashMap<Skill, XpSaveSingle>();
    Set<Skill> compactViewSkills = EnumSet.noneOf(Skill.class);
    XpSaveSingle overall;

    XpSave() {
    }
}

