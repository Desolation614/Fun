/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.timestamp;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="timestamp")
public interface TimestampConfig
extends Config {
    public static final String GROUP = "timestamp";

    @ConfigItem(keyName="opaqueTimestamp", name="Timestamps (opaque)", position=1, description="Color of timestamps from the Timestamps plugin (opaque).")
    public Color opaqueTimestamp();

    @ConfigItem(keyName="transparentTimestamp", name="Timestamps (transparent)", position=2, description="Color of timestamps from the Timestamps plugin (transparent).")
    public Color transparentTimestamp();

    @ConfigItem(keyName="format", name="Timestamp format", position=3, description="Customize your timestamp format by using the following characters:<br>'yyyy' : year<br>'MM' : month<br>'dd' : day<br>'HH' : hour in 24 hour format<br>'hh' : hour in 12 hour format<br>'mm' : minute<br>'ss' : second<br>'a'  : AM/PM")
    default public String timestampFormat() {
        return "[HH:mm]";
    }
}

