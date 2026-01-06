/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.herbiboars;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="herbiboar")
public interface HerbiboarConfig
extends Config {
    @ConfigItem(position=0, keyName="showStart", name="Show start objects", description="Show highlights for starting rocks and logs.")
    default public boolean isStartShown() {
        return true;
    }

    @ConfigItem(position=1, keyName="showClickboxes", name="Show clickboxes", description="Show clickboxes on trail objects and tunnels instead of tiles.")
    default public boolean showClickBoxes() {
        return false;
    }

    @Alpha
    @ConfigItem(position=2, keyName="colorStart", name="Start color", description="Color for rocks that start the trails.")
    default public Color getStartColor() {
        return Color.CYAN;
    }

    @ConfigItem(position=3, keyName="showTunnel", name="Show end tunnels", description="Show highlights for tunnels with herbiboars.")
    default public boolean isTunnelShown() {
        return true;
    }

    @Alpha
    @ConfigItem(position=4, keyName="colorTunnel", name="Tunnel color", description="Color for tunnels with herbiboars.")
    default public Color getTunnelColor() {
        return Color.GREEN;
    }

    @ConfigItem(position=5, keyName="showObject", name="Show trail objects", description="Show highlights for mushrooms, mud, seaweed, etc.")
    default public boolean isObjectShown() {
        return true;
    }

    @Alpha
    @ConfigItem(position=6, keyName="colorGameObject", name="Trail object color", description="Color for mushrooms, mud, seaweed, etc.")
    default public Color getObjectColor() {
        return Color.CYAN;
    }

    @ConfigItem(position=7, keyName="showTrail", name="Show trail", description="Show highlights for trail prints.")
    default public boolean isTrailShown() {
        return true;
    }

    @Alpha
    @ConfigItem(position=8, keyName="colorTrail", name="Trail color", description="Color for mushrooms, mud, seaweed, etc.")
    default public Color getTrailColor() {
        return Color.WHITE;
    }
}

