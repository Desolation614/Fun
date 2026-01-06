/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.inventorytags;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(value="inventorytags")
public interface InventoryTagsConfig
extends Config {
    public static final String GROUP = "inventorytags";
    @ConfigSection(name="Tag display mode", description="How tags are displayed in the inventory.", position=0)
    public static final String tagStyleSection = "tagStyleSection";

    @ConfigItem(position=0, keyName="showTagOutline", name="Outline", description="Configures whether or not item tags show be outlined.", section="tagStyleSection")
    default public boolean showTagOutline() {
        return true;
    }

    @ConfigItem(position=1, keyName="tagUnderline", name="Underline", description="Configures whether or not item tags should be underlined.", section="tagStyleSection")
    default public boolean showTagUnderline() {
        return false;
    }

    @ConfigItem(position=2, keyName="tagFill", name="Fill", description="Configures whether or not item tags should be filled.", section="tagStyleSection")
    default public boolean showTagFill() {
        return false;
    }

    @Range(max=255)
    @ConfigItem(position=3, keyName="fillOpacity", name="Fill opacity", description="Configures the opacity of the tag fill", section="tagStyleSection")
    default public int fillOpacity() {
        return 50;
    }
}

