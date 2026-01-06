/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.metronome;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(value="metronome")
public interface MetronomePluginConfiguration
extends Config {
    public static final int VOLUME_MAX = 127;

    @ConfigItem(keyName="tickCount", name="Tick count", description="Configures the tick on which a sound will be played.")
    default public int tickCount() {
        return 1;
    }

    @Range(max=127)
    @ConfigItem(keyName="tickVolume", name="Tick volume", description="Configures the volume of the tick sound. A value of 0 will disable tick sounds.")
    default public int tickVolume() {
        return 96;
    }

    @Range(max=127)
    @ConfigItem(keyName="tockVolume", name="Tock volume", description="Configures the volume of the tock sound. A value of 0 will disable tock sounds.")
    default public int tockVolume() {
        return 0;
    }
}

