/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.runepouch;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="runepouch")
public interface RunepouchConfig
extends Config {
    @ConfigItem(keyName="fontcolor", name="Font color", description="Color of the font for the number of runes in pouch.", position=1)
    default public Color fontColor() {
        return Color.yellow;
    }

    @ConfigItem(keyName="runePouchOverlayMode", name="Display mode", description="Configures where rune pouch overlay is displayed.", position=3)
    default public RunepouchOverlayMode runePouchOverlayMode() {
        return RunepouchOverlayMode.BOTH;
    }

    public static enum RunepouchOverlayMode {
        INVENTORY,
        MOUSE_HOVER,
        BOTH;

    }
}

