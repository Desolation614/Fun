/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.interacthighlight;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(value="interacthighlight")
public interface InteractHighlightConfig
extends Config {
    @ConfigSection(name="NPCs", description="Settings for NPC highlight.", position=0)
    public static final String npcSection = "npcSection";
    @ConfigSection(name="Objects", description="Settings for object highlight.", position=1)
    public static final String objectSection = "objectSection";

    @ConfigItem(keyName="npcShowHover", name="Show on hover", description="Outline NPCs when hovered.", position=1, section="npcSection")
    default public boolean npcShowHover() {
        return true;
    }

    @ConfigItem(keyName="npcShowInteract", name="Show on interact", description="Outline NPCs when interacted.", position=2, section="npcSection")
    default public boolean npcShowInteract() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="npcHoverHighlightColor", name="NPC hover", description="The color of the hover outline for NPCs.", position=3, section="npcSection")
    default public Color npcHoverHighlightColor() {
        return new Color(-1862271232, true);
    }

    @Alpha
    @ConfigItem(keyName="npcAttackHoverHighlightColor", name="NPC attack hover", description="The color of the attack hover outline for NPCs.", position=4, section="npcSection")
    default public Color npcAttackHoverHighlightColor() {
        return new Color(-1862271232, true);
    }

    @Alpha
    @ConfigItem(keyName="npcInteractHighlightColor", name="NPC interact", description="The color of the target outline for NPCs.", position=5, section="npcSection")
    default public Color npcInteractHighlightColor() {
        return new Color(-1862336512, true);
    }

    @Alpha
    @ConfigItem(keyName="npcAttackHighlightColor", name="NPC attack", description="The color of the outline on attacked NPCs.", position=6, section="npcSection")
    default public Color npcAttackHighlightColor() {
        return new Color(-1862336512, true);
    }

    @ConfigItem(keyName="objectShowHover", name="Show on hover", description="Outline objects when hovered.", position=1, section="objectSection")
    default public boolean objectShowHover() {
        return true;
    }

    @ConfigItem(keyName="objectShowInteract", name="Show on interact", description="Outline objects when interacted.", position=2, section="objectSection")
    default public boolean objectShowInteract() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="objectHoverHighlightColor", name="Object hover", description="The color of the hover outline for objects.", position=4, section="objectSection")
    default public Color objectHoverHighlightColor() {
        return new Color(-1878982657, true);
    }

    @Alpha
    @ConfigItem(keyName="objectInteractHighlightColor", name="Object interact", description="The color of the target outline for objects.", position=6, section="objectSection")
    default public Color objectInteractHighlightColor() {
        return new Color(-1862336512, true);
    }

    @ConfigItem(keyName="borderWidth", name="Border width", description="Width of the outlined border.", position=7)
    default public int borderWidth() {
        return 4;
    }

    @ConfigItem(keyName="outlineFeather", name="Outline feather", description="Specify between 0-4 how much of the model outline should be faded.", position=8)
    @Range(max=4)
    default public int outlineFeather() {
        return 4;
    }
}

