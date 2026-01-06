/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.plugins.groundmarkers;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(value="groundMarker")
public interface GroundMarkerConfig
extends Config {
    public static final String GROUND_MARKER_CONFIG_GROUP = "groundMarker";
    public static final String SHOW_IMPORT_EXPORT_KEY_NAME = "showImportExport";

    @Alpha
    @ConfigItem(keyName="markerColor", name="Tile color", description="The default color for marked tiles.")
    default public Color markerColor() {
        return Color.YELLOW;
    }

    @ConfigItem(keyName="drawOnMinimap", name="Draw tiles on minimap", description="Configures whether marked tiles should be drawn on minimap.")
    default public boolean drawTileOnMinimmap() {
        return false;
    }

    @ConfigItem(keyName="showImportExport", name="Show import/export/clear options", description="Show the Import, Export, and Clear options on the world map orb right-click menu.")
    default public boolean showImportExport() {
        return true;
    }

    @ConfigItem(keyName="borderWidth", name="Border width", description="Width of the marked tile border.")
    default public double borderWidth() {
        return 2.0;
    }

    @ConfigItem(keyName="fillOpacity", name="Fill opacity", description="Opacity of the tile fill color.")
    @Range(max=255)
    default public int fillOpacity() {
        return 50;
    }
}

