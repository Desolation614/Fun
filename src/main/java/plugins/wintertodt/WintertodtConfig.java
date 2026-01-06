/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.wintertodt;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.wintertodt.config.WintertodtNotifyDamage;

@ConfigGroup(value="wintertodt")
public interface WintertodtConfig
extends Config {
    @ConfigItem(position=0, keyName="showOverlay", name="Show overlay", description="Toggles the status overlay.")
    default public boolean showOverlay() {
        return true;
    }

    @ConfigItem(position=1, keyName="damageNotificationColor", name="Damage notification", description="Color of damage notification text in chat.")
    default public Color damageNotificationColor() {
        return Color.CYAN;
    }

    @ConfigItem(position=2, keyName="roundNotification", name="Round notification", description="Notifies you before the round starts (in seconds).")
    @Range(max=60)
    @Units(value="s")
    default public int roundNotification() {
        return 5;
    }

    @ConfigItem(position=3, keyName="notifyCold", name="Ambient damage notification", description="Notifies when hit by the Wintertodt's ambient cold damage.")
    default public WintertodtNotifyDamage notifyCold() {
        return WintertodtNotifyDamage.INTERRUPT;
    }

    @ConfigItem(position=4, keyName="notifySnowfall", name="Snowfall damage notification", description="Notifies when hit by the Wintertodt's snowfall attack.")
    default public WintertodtNotifyDamage notifySnowfall() {
        return WintertodtNotifyDamage.INTERRUPT;
    }

    @ConfigItem(position=5, keyName="notifyBrazierDamage", name="Brazier damage notification", description="Notifies when hit by the brazier breaking.")
    default public WintertodtNotifyDamage notifyBrazierDamage() {
        return WintertodtNotifyDamage.INTERRUPT;
    }

    @ConfigItem(position=6, keyName="notifyFullInv", name="Full inventory notification", description="Notifies when your inventory fills up with bruma roots.")
    default public boolean notifyFullInv() {
        return true;
    }

    @ConfigItem(position=7, keyName="notifyEmptyInv", name="Empty inventory notification", description="Notifies when you run out of bruma roots.")
    default public boolean notifyEmptyInv() {
        return true;
    }

    @ConfigItem(position=8, keyName="notifyBrazierOut", name="Brazier extinguish notification", description="Notifies when the brazier goes out.")
    default public boolean notifyBrazierOut() {
        return true;
    }
}

