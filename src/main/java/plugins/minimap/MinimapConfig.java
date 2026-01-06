/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.minimap;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="minimap")
public interface MinimapConfig
extends Config {
    public static final String GROUP = "minimap";
    @ConfigSection(name="Minimap dot colors", description="The colors of dots on the minimap.", position=0)
    public static final String minimapDotSection = "minimapDotSection";

    @ConfigItem(keyName="zoom", name="Zoom", description="Enables zooming on the minimap.")
    default public boolean zoom() {
        return true;
    }

    @ConfigItem(keyName="hideMinimap", name="Hide minimap", description="Do not show the minimap on screen (resizable only).")
    default public boolean hideMinimap() {
        return false;
    }

    @ConfigItem(keyName="item", name="Item color", description="Set the minimap color items are drawn in.", section="minimapDotSection")
    public Color itemColor();

    @ConfigItem(keyName="npc", name="NPC color", description="Set the minimap color NPCs are drawn in.", section="minimapDotSection")
    public Color npcColor();

    @ConfigItem(keyName="player", name="Player color", description="Set the minimap color players are drawn in.", section="minimapDotSection")
    public Color playerColor();

    @ConfigItem(keyName="friend", name="Friends color", description="Set the minimap color your friends are drawn in.", section="minimapDotSection")
    public Color friendColor();

    @ConfigItem(keyName="team", name="Team color", description="Set the minimap color your team is drawn in.", section="minimapDotSection")
    public Color teamColor();

    @ConfigItem(keyName="clan", name="Friends chat color", description="Set the minimap color your friends chat members are drawn in.", section="minimapDotSection")
    public Color friendsChatColor();

    @ConfigItem(keyName="clanchat", name="Clan chat color", description="Set the minimap color your clan chat members are drawn in.", section="minimapDotSection")
    public Color clanChatColor();
}

