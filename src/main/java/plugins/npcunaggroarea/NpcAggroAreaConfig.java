/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.npcunaggroarea;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Notification;

@ConfigGroup(value="npcUnaggroArea")
public interface NpcAggroAreaConfig
extends Config {
    public static final String CONFIG_GROUP = "npcUnaggroArea";
    public static final String CONFIG_CENTER1 = "center1";
    public static final String CONFIG_CENTER2 = "center2";
    public static final String CONFIG_LOCATION = "location";
    public static final String CONFIG_DURATION = "duration";

    @ConfigItem(keyName="npcUnaggroAlwaysActive", name="Always active", description="Always show this plugin's overlays.<br>Otherwise, they will only be shown when any NPC name matches the list.", position=1)
    default public boolean alwaysActive() {
        return false;
    }

    @ConfigItem(keyName="npcUnaggroNames", name="NPC names", description="Enter names of NPCs where you wish to use this plugin.", position=2)
    default public String npcNamePatterns() {
        return "";
    }

    @ConfigItem(keyName="npcUnaggroShowTimer", name="Show timer", description="Display a timer until NPCs become unaggressive.", position=3)
    default public boolean showTimer() {
        return true;
    }

    @ConfigItem(keyName="npcUnaggroShowAreaLines", name="Show area lines", description="Display lines, when walked past, the unaggressive timer resets.", position=4)
    default public boolean showAreaLines() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="npcAggroAreaColor", name="Aggressive color", description="Choose color to use for marking NPC unaggressive area when NPCs are aggressive.", position=5)
    default public Color aggroAreaColor() {
        return new Color(1694498560, true);
    }

    @Alpha
    @ConfigItem(keyName="npcUnaggroAreaColor", name="Unaggressive color", description="Choose color to use for marking NPC unaggressive area after NPCs have lost aggression.", position=6)
    default public Color unaggroAreaColor() {
        return new Color(0xFFFF00);
    }

    @ConfigItem(keyName="notifyExpire", name="Notify expiration", description="Send a notification when the unaggressive timer expires.", position=7)
    default public Notification notifyExpire() {
        return Notification.OFF;
    }

    @ConfigItem(keyName="hideIfOutOfCombat", name="Hide when out of combat", description="Hides unaggressive area lines when out of combat.", position=8)
    default public boolean hideIfOutOfCombat() {
        return false;
    }

    @ConfigItem(keyName="showOnSlayerTask", name="Show on slayer task", description="Enable for current slayer task NPCs.", position=9)
    default public boolean showOnSlayerTask() {
        return false;
    }
}

