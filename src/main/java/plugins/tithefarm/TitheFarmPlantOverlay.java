/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  net.runelite.api.Client
 *  net.runelite.api.Perspective
 *  net.runelite.api.Point
 *  net.runelite.api.coords.LocalPoint
 *  net.runelite.api.coords.WorldPoint
 */
package net.runelite.client.plugins.tithefarm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantState;
import net.runelite.client.plugins.tithefarm.TitheFarmPlugin;
import net.runelite.client.plugins.tithefarm.TitheFarmPluginConfig;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;
import net.runelite.client.util.ColorUtil;

public class TitheFarmPlantOverlay
extends Overlay {
    private final Client client;
    private final TitheFarmPlugin plugin;
    private final TitheFarmPluginConfig config;
    private final Map<TitheFarmPlantState, Color> borders = new HashMap<TitheFarmPlantState, Color>();
    private final Map<TitheFarmPlantState, Color> fills = new HashMap<TitheFarmPlantState, Color>();

    @Inject
    TitheFarmPlantOverlay(Client client, TitheFarmPlugin plugin, TitheFarmPluginConfig config) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    public void updateConfig() {
        this.borders.clear();
        this.fills.clear();
        Color colorUnwateredBorder = this.config.getColorUnwatered();
        Color colorUnwatered = ColorUtil.colorWithAlpha(colorUnwateredBorder, (int)((double)colorUnwateredBorder.getAlpha() / 2.5));
        this.borders.put(TitheFarmPlantState.UNWATERED, colorUnwateredBorder);
        this.fills.put(TitheFarmPlantState.UNWATERED, colorUnwatered);
        Color colorWateredBorder = this.config.getColorWatered();
        Color colorWatered = ColorUtil.colorWithAlpha(colorWateredBorder, (int)((double)colorWateredBorder.getAlpha() / 2.5));
        this.borders.put(TitheFarmPlantState.WATERED, colorWateredBorder);
        this.fills.put(TitheFarmPlantState.WATERED, colorWatered);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        for (TitheFarmPlant plant : this.plugin.getPlants()) {
            Point canvasLocation;
            LocalPoint localLocation;
            if (plant.getState() == TitheFarmPlantState.DEAD || plant.getState() == TitheFarmPlantState.GROWN || (localLocation = LocalPoint.fromWorld((Client)this.client, (WorldPoint)plant.getWorldLocation())) == null || (canvasLocation = Perspective.localToCanvas((Client)this.client, (LocalPoint)localLocation, (int)this.client.getPlane())) == null) continue;
            ProgressPieComponent progressPieComponent = new ProgressPieComponent();
            progressPieComponent.setPosition(canvasLocation);
            progressPieComponent.setProgress(1.0 - plant.getPlantTimeRelative());
            progressPieComponent.setBorderColor(this.borders.get((Object)plant.getState()));
            progressPieComponent.setFill(this.fills.get((Object)plant.getState()));
            progressPieComponent.render(graphics);
        }
        return null;
    }
}

