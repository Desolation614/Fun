/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.xpdrop;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup(value="xpdrop")
public interface XpDropConfig
extends Config {
    @ConfigItem(keyName="hideSkillIcons", name="Hide skill icons", description="Configure if XP drops will show their respective skill icons.", position=0)
    default public boolean hideSkillIcons() {
        return false;
    }

    @ConfigItem(keyName="standardColor", name="Standard color", description="XP drop color when no prayer is active.", position=1)
    public Color standardColor();

    @ConfigItem(keyName="meleePrayerColor", name="Melee prayer color", description="XP drop color when a melee prayer is active.", position=2)
    default public Color getMeleePrayerColor() {
        return new Color(21, 128, 173);
    }

    @ConfigItem(keyName="rangePrayerColor", name="Range prayer color", description="XP drop color when a range prayer is active.", position=3)
    default public Color getRangePrayerColor() {
        return new Color(21, 128, 173);
    }

    @ConfigItem(keyName="magePrayerColor", name="Mage prayer color", description="XP drop color when a mage prayer is active.", position=4)
    default public Color getMagePrayerColor() {
        return new Color(21, 128, 173);
    }

    @ConfigItem(keyName="fakeXpDropDelay", name="Fake XP drop delay", description="Configures how many ticks should pass between fake XP drops, 0 to disable.", position=5)
    @Units(value=" ticks")
    default public int fakeXpDropDelay() {
        return 0;
    }
}

