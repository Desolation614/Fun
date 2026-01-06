/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.fps;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(value="fpscontrol")
public interface FpsConfig
extends Config {
    @ConfigItem(keyName="limitFps", name="Limit global FPS", description="Global FPS limit in effect regardless of whether window is in focus or not.", position=1)
    default public boolean limitFps() {
        return false;
    }

    @Range(min=1, max=360)
    @ConfigItem(keyName="maxFps", name="Global FPS target", description="Desired max global frames per second.", position=2)
    default public int maxFps() {
        return 50;
    }

    @ConfigItem(keyName="limitFpsUnfocused", name="Limit FPS unfocused", description="FPS limit while window is out of focus.", position=3)
    default public boolean limitFpsUnfocused() {
        return false;
    }

    @Range(min=1, max=360)
    @ConfigItem(keyName="maxFpsUnfocused", name="Unfocused FPS target", description="Desired max frames per second for unfocused.", position=4)
    default public int maxFpsUnfocused() {
        return 50;
    }

    @ConfigItem(keyName="drawFps", name="Draw FPS indicator", description="Show a number in the corner for the current FPS.", position=5)
    default public boolean drawFps() {
        return true;
    }
}

