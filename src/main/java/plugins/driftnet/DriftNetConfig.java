/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.driftnet;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(value="driftnet")
public interface DriftNetConfig
extends Config {
    @ConfigItem(position=1, keyName="showNetStatus", name="Show net status", description="Show net status and fish count.")
    default public boolean showNetStatus() {
        return true;
    }

    @ConfigItem(position=2, keyName="countColor", name="Fish count color", description="Color of the fish count text.")
    default public Color countColor() {
        return Color.WHITE;
    }

    @ConfigItem(position=3, keyName="highlightUntaggedFish", name="Highlight untagged fish", description="Highlight the untagged fish.")
    default public boolean highlightUntaggedFish() {
        return true;
    }

    @ConfigItem(position=4, keyName="timeoutDelay", name="Tagged timeout", description="Time required for a tag to expire.")
    @Range(min=1, max=100)
    @Units(value=" ticks")
    default public int timeoutDelay() {
        return 60;
    }

    @Alpha
    @ConfigItem(keyName="untaggedFishColor", name="Untagged fish color", description="Color of untagged fish.", position=5)
    default public Color untaggedFishColor() {
        return Color.CYAN;
    }

    @ConfigItem(keyName="tagAnnette", name="Tag Annette", description="Tag Annette when no nets in inventory.", position=6)
    default public boolean tagAnnetteWhenNoNets() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="annetteTagColor", name="Annette tag color", description="Color of Annette tag.", position=7)
    default public Color annetteTagColor() {
        return Color.RED;
    }
}

