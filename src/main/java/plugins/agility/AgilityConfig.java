/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.agility;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Units;

@ConfigGroup(value="agility")
public interface AgilityConfig
extends Config {
    @ConfigSection(name="Hallowed Sepulchre", description="Settings for Hallowed Sepulchre highlights.", position=17)
    public static final String sepulchreSection = "Hallowed Sepulchre";

    @ConfigItem(keyName="showClickboxes", name="Show clickboxes", description="Show agility course and other obstacle clickboxes.", position=0)
    default public boolean showClickboxes() {
        return true;
    }

    @ConfigItem(keyName="showLapCount", name="Show lap count", description="Enable/disable the lap counter.", position=1)
    default public boolean showLapCount() {
        return true;
    }

    @ConfigItem(keyName="lapTimeout", name="Hide lap count", description="Time until the lap counter hides/resets.", position=2)
    @Units(value=" mins")
    default public int lapTimeout() {
        return 5;
    }

    @ConfigItem(keyName="lapsToLevel", name="Show laps until goal", description="Show number of laps remaining until next goal is reached.", position=3)
    default public boolean lapsToLevel() {
        return true;
    }

    @ConfigItem(keyName="lapsPerHour", name="Show laps per hour", description="Shows how many laps you can expect to complete per hour.", position=4)
    default public boolean lapsPerHour() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="overlayColor", name="Overlay color", description="Color of agility overlay.", position=5)
    default public Color getOverlayColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName="highlightMarks", name="Highlight marks of grace", description="Enable/disable the highlighting of retrievable marks of grace.", position=6)
    default public boolean highlightMarks() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="markHighlight", name="Mark highlight color", description="Color of highlighted marks of grace.", position=7)
    default public Color getMarkColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="highlightPortals", name="Highlight portals", description="Enable/disable the highlighting of Prifddinas portals.", position=8)
    default public boolean highlightPortals() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="portalsHighlight", name="Portals color", description="Color of highlighted Prifddinas portals.", position=9)
    default public Color getPortalsColor() {
        return Color.MAGENTA;
    }

    @ConfigItem(keyName="highlightShortcuts", name="Highlight agility shortcuts", description="Enable/disable the highlighting of agility shortcuts.", position=10)
    default public boolean highlightShortcuts() {
        return true;
    }

    @ConfigItem(keyName="trapOverlay", name="Show trap overlay", description="Enable/disable the highlighting of traps on agility courses.", position=11)
    default public boolean showTrapOverlay() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="trapHighlight", name="Trap overlay color", description="Color of agility trap overlay.", position=12)
    default public Color getTrapColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="agilityArenaNotifier", name="Agility Arena notifier", description="Notify on ticket location change in Agility Arena.", position=13)
    default public Notification notifyAgilityArena() {
        return Notification.ON;
    }

    @ConfigItem(keyName="agilityArenaTimer", name="Agility Arena timer", description="Configures whether Agility Arena timer is displayed.", position=14)
    default public boolean showAgilityArenaTimer() {
        return true;
    }

    @ConfigItem(keyName="highlightStick", name="Highlight stick", description="Highlight the retrievable stick in the Werewolf Agility Course.", position=15)
    default public boolean highlightStick() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="stickHighlightColor", name="Stick highlight color", description="Color of highlighted stick.", position=16)
    default public Color stickHighlightColor() {
        return Color.RED;
    }

    @ConfigItem(keyName="highlightSepulchreNpcs", name="Highlight projectiles", description="Highlights arrows and swords in the Sepulchre.", position=17, section="Hallowed Sepulchre")
    default public boolean highlightSepulchreNpcs() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="sepulchreHighlightColor", name="Projectile color", description="Overlay color for arrows and swords.", position=18, section="Hallowed Sepulchre")
    default public Color sepulchreHighlightColor() {
        return Color.GREEN;
    }

    @ConfigItem(keyName="highlightSepulchreObstacles", name="Highlight obstacles", description="Highlights pillars and stairs in the Sepulchre.", position=19, section="Hallowed Sepulchre")
    default public boolean highlightSepulchreObstacles() {
        return true;
    }

    @ConfigItem(keyName="highlightSepulchreSkilling", name="Highlight skill challenges", description="Highlights skilling challenges in the Sepulchre.", position=20, section="Hallowed Sepulchre")
    default public boolean highlightSepulchreSkilling() {
        return true;
    }
}

