/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.dailytaskindicators;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="dailytaskindicators")
public interface DailyTasksConfig
extends Config {
    @ConfigItem(position=1, keyName="showHerbBoxes", name="Show herb boxes", description="Show a message when you can collect your daily herb boxes at NMZ.")
    default public boolean showHerbBoxes() {
        return true;
    }

    @ConfigItem(position=2, keyName="showStaves", name="Show claimable staves", description="Show a message when you can collect your daily battlestaves from Zaff.")
    default public boolean showStaves() {
        return true;
    }

    @ConfigItem(position=3, keyName="showEssence", name="Show claimable essence", description="Show a message when you can collect your daily pure essence from Wizard Cromperty.")
    default public boolean showEssence() {
        return false;
    }

    @ConfigItem(position=4, keyName="showRunes", name="Show claimable random runes", description="Show a message when you can collect your daily random runes from Lundail.")
    default public boolean showRunes() {
        return false;
    }

    @ConfigItem(position=5, keyName="showSand", name="Show claimable sand", description="Show a message when you can collect your daily sand from Bert.")
    default public boolean showSand() {
        return false;
    }

    @ConfigItem(position=6, keyName="showFlax", name="Show claimable bow strings", description="Show a message when you can convert noted flax to bow strings with the Flax keeper.")
    default public boolean showFlax() {
        return false;
    }

    @ConfigItem(position=7, keyName="showBonemeal", name="Show claimable bonemeal & slime", description="Show a message when you can collect bonemeal & slime from Robin.")
    default public boolean showBonemeal() {
        return false;
    }

    @ConfigItem(position=8, keyName="showDynamite", name="Show claimable dynamite", description="Show a message when you can collect dynamite from Thirus.")
    default public boolean showDynamite() {
        return false;
    }

    @ConfigItem(position=9, keyName="showArrows", name="Show claimable ogre arrows", description="Show a message when you can collect ogre arrows from Rantz.")
    default public boolean showArrows() {
        return false;
    }
}

