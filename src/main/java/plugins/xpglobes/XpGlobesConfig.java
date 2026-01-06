/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xpglobes;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup(value="xpglobes")
public interface XpGlobesConfig
extends Config {
    @ConfigItem(keyName="enableTooltips", name="Enable tooltips", description="Configures whether or not to show tooltips.", position=0)
    default public boolean enableTooltips() {
        return true;
    }

    @ConfigItem(keyName="showXpLeft", name="Show XP left", description="Shows XP left inside the globe tooltip box.", position=1)
    default public boolean showXpLeft() {
        return true;
    }

    @ConfigItem(keyName="showActionsLeft", name="Show actions left", description="Shows the number of actions left inside the globe tooltip box.", position=2)
    default public boolean showActionsLeft() {
        return true;
    }

    @ConfigItem(keyName="showXpHour", name="Show XP/hr", description="Shows XP per hour inside the globe tooltip box.", position=3)
    default public boolean showXpHour() {
        return true;
    }

    @ConfigItem(keyName="showTimeTilGoal", name="Show time til goal", description="Shows the amount of time until goal level in the globe tooltip box.", position=4)
    default public boolean showTimeTilGoal() {
        return true;
    }

    @ConfigItem(keyName="hideMaxed", name="Hide maxed skills", description="Stop globes from showing up for level 99 skills.", position=14)
    default public boolean hideMaxed() {
        return false;
    }

    @ConfigItem(keyName="showVirtualLevel", name="Show virtual level", description="Shows virtual level if over 99 in a skill and 'Hide maxed skills' is not checked.", position=15)
    default public boolean showVirtualLevel() {
        return false;
    }

    @ConfigItem(keyName="enableCustomArcColor", name="Enable custom arc color", description="Enables the custom coloring of the globe's arc instead of using the skill's default color.", position=16)
    default public boolean enableCustomArcColor() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="Progress arc color", name="Progress arc color", description="Change the color of the progress arc in the XP orb.", position=17)
    default public Color progressArcColor() {
        return Color.ORANGE;
    }

    @Alpha
    @ConfigItem(keyName="Progress orb outline color", name="Progress orb outline color", description="Change the color of the progress orb outline.", position=18)
    default public Color progressOrbOutLineColor() {
        return Color.BLACK;
    }

    @Alpha
    @ConfigItem(keyName="Progress orb background color", name="Progress orb background color", description="Change the color of the progress orb background.", position=19)
    default public Color progressOrbBackgroundColor() {
        return new Color(128, 128, 128, 127);
    }

    @ConfigItem(keyName="Progress arc width", name="Progress arc width", description="Change the stroke width of the progress arc.", position=20)
    @Units(value="px")
    default public int progressArcStrokeWidth() {
        return 2;
    }

    @ConfigItem(keyName="Orb size", name="Size of orbs", description="Change the size of the XP orbs.", position=21)
    @Units(value="px")
    default public int xpOrbSize() {
        return 40;
    }

    @ConfigItem(keyName="Orb duration", name="Duration of orbs", description="Change the duration the XP orbs are visible.", position=22)
    @Units(value="s")
    default public int xpOrbDuration() {
        return 10;
    }

    @ConfigItem(keyName="alignOrbsVertically", name="Vertical orbs", description="Aligns the orbs vertically instead of horizontally.", hidden=true)
    default public boolean alignOrbsVertically() {
        return false;
    }

    @ConfigItem(keyName="alignOrbsVertically", name="", description="")
    public void setAlignOrbsVertically(Boolean var1);
}

