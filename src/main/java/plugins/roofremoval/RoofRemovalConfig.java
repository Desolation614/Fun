/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.roofremoval;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="roofremoval")
public interface RoofRemovalConfig
extends Config {
    public static final String CONFIG_GROUP = "roofremoval";
    @ConfigSection(name="Modes", description="In what situations should roofs be removed.", position=0)
    public static final String modesSection = "modes";
    @ConfigSection(name="Area overrides", description="Always remove roofs in specific areas.", position=1)
    public static final String overridesSection = "overrides";

    @ConfigItem(keyName="removePosition", name="Player's position", description="Remove roofs above the player's position.", section="modes")
    default public boolean removePosition() {
        return true;
    }

    @ConfigItem(keyName="removeHovered", name="Hovered tile", description="Remove roofs above the hovered tile.", section="modes")
    default public boolean removeHovered() {
        return true;
    }

    @ConfigItem(keyName="removeDestination", name="Destination tile", description="Remove roofs above the destination tile.", section="modes")
    default public boolean removeDestination() {
        return true;
    }

    @ConfigItem(keyName="removeBetween", name="Between camera & player", description="Remove roofs between the camera and the player at low camera angles.", section="modes")
    default public boolean removeBetween() {
        return true;
    }

    @ConfigItem(keyName="overridePOH", name="Player owned house", description="Always remove roofs while in the player owned house.", section="overrides")
    default public boolean overridePOH() {
        return false;
    }
}

