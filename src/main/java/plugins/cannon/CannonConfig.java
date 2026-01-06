/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.cannon;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Range;

@ConfigGroup(value="cannon")
public interface CannonConfig
extends Config {
    @ConfigItem(keyName="showEmptyCannonNotification", name="Enable cannon notifications", description="Configures whether to notify you when your cannon is low on cannonballs.", position=1)
    default public Notification showCannonNotifications() {
        return Notification.ON;
    }

    @Range(max=60)
    @ConfigItem(keyName="lowWarningThreshold", name="Low warning threshold", description="Configures the number of cannonballs remaining before a notification is sent.<br>Regardless of this value, a notification will still be sent when your cannon is empty.", position=2)
    default public int lowWarningThreshold() {
        return 0;
    }

    @ConfigItem(keyName="showInfobox", name="Show cannonball infobox", description="Configures whether to show the cannonballs in an infobox.", position=3)
    default public boolean showInfobox() {
        return false;
    }

    @ConfigItem(keyName="showDoubleHitSpot", name="Show double hit spots", description="Configures whether to show the NPC double hit spot.", position=4)
    default public boolean showDoubleHitSpot() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="highlightDoubleHitColor", name="Double hit spots", description="Configures the highlight color of double hit spots.", position=5)
    default public Color highlightDoubleHitColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="showCannonSpots", name="Show common cannon spots", description="Configures whether to show common cannon spots or not.", position=6)
    default public boolean showCannonSpots() {
        return true;
    }
}

