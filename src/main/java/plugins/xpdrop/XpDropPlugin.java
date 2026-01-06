/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  com.google.inject.Provides
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.EnumComposition
 *  net.runelite.api.Prayer
 *  net.runelite.api.Skill
 *  net.runelite.api.events.GameTick
 *  net.runelite.api.events.ScriptPreFired
 *  net.runelite.api.events.StatChanged
 *  net.runelite.api.widgets.Widget
 */
package net.runelite.client.plugins.xpdrop;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Provides;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.StatChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xpdrop.XpDropConfig;

@PluginDescriptor(name="XP Drop", description="Enable customization of the way XP drops are displayed", tags={"experience", "levels", "tick", "prayer", "xpdrop"})
public class XpDropPlugin
extends Plugin {
    private static final Multimap<Prayer, PrayerType> PRAYER_TYPE = new ImmutableMultimap.Builder().put((Object)Prayer.BURST_OF_STRENGTH, (Object)PrayerType.MELEE).put((Object)Prayer.CLARITY_OF_THOUGHT, (Object)PrayerType.MELEE).put((Object)Prayer.SHARP_EYE, (Object)PrayerType.RANGE).put((Object)Prayer.MYSTIC_WILL, (Object)PrayerType.MAGIC).put((Object)Prayer.SUPERHUMAN_STRENGTH, (Object)PrayerType.MELEE).put((Object)Prayer.IMPROVED_REFLEXES, (Object)PrayerType.MELEE).put((Object)Prayer.HAWK_EYE, (Object)PrayerType.RANGE).put((Object)Prayer.MYSTIC_LORE, (Object)PrayerType.MAGIC).put((Object)Prayer.ULTIMATE_STRENGTH, (Object)PrayerType.MELEE).put((Object)Prayer.INCREDIBLE_REFLEXES, (Object)PrayerType.MELEE).put((Object)Prayer.EAGLE_EYE, (Object)PrayerType.RANGE).put((Object)Prayer.MYSTIC_MIGHT, (Object)PrayerType.MAGIC).put((Object)Prayer.CHIVALRY, (Object)PrayerType.MELEE).put((Object)Prayer.PIETY, (Object)PrayerType.MELEE).put((Object)Prayer.RIGOUR, (Object)PrayerType.RANGE).put((Object)Prayer.AUGURY, (Object)PrayerType.MAGIC).put((Object)Prayer.RP_ANCIENT_STRENGTH, (Object)PrayerType.MELEE).put((Object)Prayer.RP_ANCIENT_SIGHT, (Object)PrayerType.RANGE).put((Object)Prayer.RP_ANCIENT_WILL, (Object)PrayerType.MAGIC).putAll((Object)Prayer.RP_TRINITAS, (Object[])new PrayerType[]{PrayerType.MELEE, PrayerType.RANGE, PrayerType.MAGIC}).put((Object)Prayer.RP_DECIMATE, (Object)PrayerType.MELEE).put((Object)Prayer.RP_ANNIHILATE, (Object)PrayerType.RANGE).put((Object)Prayer.RP_VAPORISE, (Object)PrayerType.MAGIC).putAll((Object)Prayer.RP_INTENSIFY, (Object[])new PrayerType[]{PrayerType.MELEE, PrayerType.RANGE, PrayerType.MAGIC}).build();
    @Inject
    private Client client;
    @Inject
    private XpDropConfig config;
    private int tickCounter = 0;
    private int previousExpGained;
    private boolean hasDropped = false;
    private int xpdropColor;
    private Skill lastSkill = null;
    private final Map<Skill, Integer> previousSkillExpTable = new EnumMap<Skill, Integer>(Skill.class);

    @Provides
    XpDropConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(XpDropConfig.class);
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired scriptPreFired) {
        if (scriptPreFired.getScriptId() == 996) {
            int[] intStack = this.client.getIntStack();
            int intStackSize = this.client.getIntStackSize();
            int widgetId = intStack[intStackSize - 4];
            this.processXpDrop(widgetId);
        }
    }

    private void processXpDrop(int widgetId) {
        Widget xpdrop = this.client.getWidget(widgetId);
        Widget[] children = xpdrop.getChildren();
        Widget text = children[0];
        Collection<PrayerType> prayers = this.getActivePrayerType();
        if (prayers.isEmpty()) {
            this.hideSkillIcons(xpdrop);
            this.resetTextColor(text);
            return;
        }
        List xpDropTypes = Arrays.stream(children).skip(1L).filter(Objects::nonNull).map(Widget::getSpriteId).map(id -> {
            if (id == 197 || id == 198 || id == 199) {
                return PrayerType.MELEE;
            }
            if (id == 200) {
                return PrayerType.RANGE;
            }
            if (id == 202) {
                return PrayerType.MAGIC;
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (xpDropTypes.contains((Object)PrayerType.RANGE)) {
            if (prayers.contains((Object)PrayerType.RANGE)) {
                this.xpdropColor = this.config.getRangePrayerColor().getRGB();
            }
        } else if (xpDropTypes.contains((Object)PrayerType.MAGIC)) {
            if (prayers.contains((Object)PrayerType.MAGIC)) {
                this.xpdropColor = this.config.getMagePrayerColor().getRGB();
            }
        } else if (xpDropTypes.contains((Object)PrayerType.MELEE) && prayers.contains((Object)PrayerType.MELEE)) {
            this.xpdropColor = this.config.getMeleePrayerColor().getRGB();
        }
        if (this.xpdropColor != 0) {
            text.setTextColor(this.xpdropColor);
        } else {
            this.resetTextColor(text);
        }
        this.hideSkillIcons(xpdrop);
    }

    private void resetTextColor(Widget widget) {
        Color standardColor = this.config.standardColor();
        if (standardColor != null) {
            int color = standardColor.getRGB();
            widget.setTextColor(color);
        } else {
            EnumComposition colorEnum = this.client.getEnum(1169);
            int defaultColorId = this.client.getVarbitValue(4695);
            int color = colorEnum.getIntValue(defaultColorId);
            widget.setTextColor(color);
        }
    }

    private void hideSkillIcons(Widget xpdrop) {
        if (this.config.hideSkillIcons()) {
            Object[] children = xpdrop.getChildren();
            Arrays.fill(children, 1, children.length, null);
        }
    }

    private Collection<PrayerType> getActivePrayerType() {
        for (Prayer prayer : PRAYER_TYPE.keySet()) {
            if (this.client.getServerVarbitValue(prayer.getVarbit()) != 1) continue;
            return PRAYER_TYPE.get((Object)prayer);
        }
        return Collections.emptyList();
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        this.xpdropColor = 0;
        int fakeTickDelay = this.config.fakeXpDropDelay();
        if (fakeTickDelay == 0 || this.lastSkill == null) {
            return;
        }
        if (this.hasDropped) {
            this.hasDropped = false;
            this.tickCounter = 0;
            return;
        }
        if (++this.tickCounter % fakeTickDelay != 0) {
            return;
        }
        this.client.runScript(new Object[]{2091, this.lastSkill.ordinal(), this.previousExpGained});
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Skill skill = statChanged.getSkill();
        int xp = statChanged.getXp();
        this.lastSkill = skill;
        Integer previous = this.previousSkillExpTable.put(skill, xp);
        if (previous != null) {
            this.previousExpGained = xp - previous;
            this.hasDropped = true;
        }
    }

    static enum PrayerType {
        MELEE,
        RANGE,
        MAGIC;

    }
}

