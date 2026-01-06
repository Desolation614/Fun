/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.tithefarm;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="tithefarmplugin")
public interface TitheFarmPluginConfig
extends Config {
    @Alpha
    @ConfigItem(position=1, keyName="hexColorUnwatered", name="Unwatered plant", description="Color of unwatered plant timer.")
    default public Color getColorUnwatered() {
        return new Color(255, 187, 0);
    }

    @Alpha
    @ConfigItem(position=2, keyName="hexColorWatered", name="Watered plant", description="Color of watered plant timer.")
    default public Color getColorWatered() {
        return new Color(0, 153, 255);
    }
}

