/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.tileindicators;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(value="tileindicators")
public interface TileIndicatorsConfig
extends Config {
    @ConfigSection(name="Destination tile", description="Destination tile configuration.", position=0)
    public static final String destinationTile = "destinationTile";
    @ConfigSection(name="Hovered tile", description="Hovered tile configuration.", position=1)
    public static final String hoveredTile = "hoveredTile";
    @ConfigSection(name="Current tile", description="Current tile configuration.", position=2)
    public static final String currentTile = "currentTile";

    @ConfigItem(keyName="highlightDestinationTile", name="Highlight destination tile", description="Highlights tile player is walking to.", position=1, section="destinationTile")
    default public boolean highlightDestinationTile() {
        return true;
    }

    @Alpha
    @ConfigItem(keyName="highlightDestinationColor", name="Highlight color", description="Configures the highlight color of current destination.", position=2, section="destinationTile")
    default public Color highlightDestinationColor() {
        return Color.GRAY;
    }

    @Alpha
    @ConfigItem(keyName="destinationTileFillColor", name="Fill color", description="Configures the fill color of destination tile.", position=3, section="destinationTile")
    default public Color destinationTileFillColor() {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(keyName="destinationTileBorderWidth", name="Border width", description="Width of the destination tile marker border.", position=4, section="destinationTile")
    default public double destinationTileBorderWidth() {
        return 2.0;
    }

    @ConfigItem(keyName="highlightHoveredTile", name="Highlight hovered tile", description="Highlights tile player is hovering with mouse.", position=1, section="hoveredTile")
    default public boolean highlightHoveredTile() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="highlightHoveredColor", name="Highlight color", description="Configures the highlight color of hovered tile.", position=2, section="hoveredTile")
    default public Color highlightHoveredColor() {
        return new Color(0, 0, 0, 0);
    }

    @Alpha
    @ConfigItem(keyName="hoveredTileFillColor", name="Fill color", description="Configures the fill color of hovered tile.", position=3, section="hoveredTile")
    default public Color hoveredTileFillColor() {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(keyName="hoveredTileBorderWidth", name="Border width", description="Width of the hovered tile marker border.", position=4, section="hoveredTile")
    default public double hoveredTileBorderWidth() {
        return 2.0;
    }

    @ConfigItem(keyName="highlightCurrentTile", name="Highlight true tile", description="Highlights true tile player is on as seen by server.", position=1, section="currentTile")
    default public boolean highlightCurrentTile() {
        return false;
    }

    @Alpha
    @ConfigItem(keyName="highlightCurrentColor", name="Highlight color", description="Configures the highlight color of current true tile.", position=2, section="currentTile")
    default public Color highlightCurrentColor() {
        return Color.CYAN;
    }

    @Alpha
    @ConfigItem(keyName="currentTileFillColor", name="Fill color", description="Configures the fill color of current true tile.", position=3, section="currentTile")
    default public Color currentTileFillColor() {
        return new Color(0, 0, 0, 50);
    }

    @ConfigItem(keyName="currentTileBorderWidth", name="Border width", description="Width of the true tile marker border.", position=4, section="currentTile")
    default public double currentTileBorderWidth() {
        return 2.0;
    }
}

