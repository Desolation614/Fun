/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.corp;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(value="corp")
public interface CorpConfig
extends Config {
    public static final String GROUP = "corp";

    @ConfigItem(keyName="leftClickCore", name="Left-click walk on core", description="Prioritizes Walk here over Attack on the dark energy core.", position=1)
    default public boolean leftClickCore() {
        return true;
    }

    @ConfigItem(keyName="showDamage", name="Show damage overlay", description="Show total damage overlay.", position=0)
    default public boolean showDamage() {
        return true;
    }

    @ConfigItem(keyName="markDarkCore", name="Mark dark core", description="Marks the dark energy core.", position=1)
    default public boolean markDarkCore() {
        return true;
    }
}

