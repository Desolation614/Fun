/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  javax.inject.Inject
 */
package net.runelite.client.plugins.screenmarkers;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.screenmarkers.ScreenMarker;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerCreationOverlay;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerMouseListener;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerOverlay;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerWidgetHighlightOverlay;
import net.runelite.client.plugins.screenmarkers.ui.ScreenMarkerPluginPanel;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(name="Screen Markers", description="Enable drawing of screen markers on top of the client", tags={"boxes", "overlay", "panel"})
public class ScreenMarkerPlugin
extends Plugin {
    private static final String PLUGIN_NAME = "Screen Markers";
    private static final String CONFIG_GROUP = "screenmarkers";
    private static final String CONFIG_KEY = "markers";
    private static final String ICON_FILE = "panel_icon.png";
    private static final String DEFAULT_MARKER_NAME = "Marker";
    private static final Dimension DEFAULT_SIZE = new Dimension(2, 2);
    private final List<ScreenMarkerOverlay> screenMarkers = new ArrayList<ScreenMarkerOverlay>();
    @Inject
    private ConfigManager configManager;
    @Inject
    private MouseManager mouseManager;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ScreenMarkerCreationOverlay overlay;
    @Inject
    private Gson gson;
    @Inject
    private ColorPickerManager colorPickerManager;
    @Inject
    private ScreenMarkerWidgetHighlightOverlay widgetHighlight;
    private ScreenMarkerMouseListener mouseListener;
    private ScreenMarkerPluginPanel pluginPanel;
    private NavigationButton navigationButton;
    private ScreenMarker currentMarker;
    private boolean creatingScreenMarker = false;
    private boolean drawingScreenMarker = false;
    private Rectangle selectedWidgetBounds = null;
    private Point startLocation = null;

    @Override
    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.widgetHighlight);
        this.loadConfig(this.configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(this.screenMarkers::add);
        this.screenMarkers.forEach(this.overlayManager::add);
        this.pluginPanel = new ScreenMarkerPluginPanel(this);
        this.pluginPanel.rebuild();
        BufferedImage icon = ImageUtil.loadImageResource(this.getClass(), ICON_FILE);
        this.navigationButton = NavigationButton.builder().tooltip(PLUGIN_NAME).icon(icon).priority(5).panel(this.pluginPanel).build();
        this.clientToolbar.addNavigation(this.navigationButton);
        this.mouseListener = new ScreenMarkerMouseListener(this);
    }

    @Override
    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.widgetHighlight);
        this.overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
        this.screenMarkers.clear();
        this.clientToolbar.removeNavigation(this.navigationButton);
        this.setMouseListenerEnabled(false);
        this.creatingScreenMarker = false;
        this.drawingScreenMarker = false;
        this.pluginPanel = null;
        this.currentMarker = null;
        this.mouseListener = null;
        this.navigationButton = null;
        this.selectedWidgetBounds = null;
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        this.screenMarkers.clear();
        this.loadConfig(this.configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY)).forEach(this.screenMarkers::add);
        this.overlayManager.removeIf(ScreenMarkerOverlay.class::isInstance);
        this.screenMarkers.forEach(this.overlayManager::add);
        SwingUtilities.invokeLater(this.pluginPanel::rebuild);
    }

    public void setMouseListenerEnabled(boolean enabled) {
        if (enabled) {
            this.mouseManager.registerMouseListener(this.mouseListener);
        } else {
            this.mouseManager.unregisterMouseListener(this.mouseListener);
        }
    }

    public void startCreation(Point location) {
        this.startCreation(location, DEFAULT_SIZE);
        if (this.selectedWidgetBounds == null) {
            this.drawingScreenMarker = true;
        }
    }

    public void startCreation(Point location, Dimension size) {
        this.currentMarker = new ScreenMarker(Instant.now().toEpochMilli(), "Marker " + (this.screenMarkers.size() + 1), 3, ScreenMarkerPluginPanel.SELECTED_COLOR, ScreenMarkerPluginPanel.SELECTED_FILL_COLOR, true, false);
        this.startLocation = location;
        this.overlay.setPreferredLocation(location);
        this.overlay.setPreferredSize(size);
    }

    public void finishCreation(boolean aborted) {
        ScreenMarker marker = this.currentMarker;
        if (!aborted && marker != null) {
            ScreenMarkerOverlay screenMarkerOverlay = new ScreenMarkerOverlay(marker);
            screenMarkerOverlay.setPreferredLocation(this.overlay.getBounds().getLocation());
            screenMarkerOverlay.setPreferredSize(this.overlay.getBounds().getSize());
            this.screenMarkers.add(screenMarkerOverlay);
            this.overlayManager.saveOverlay(screenMarkerOverlay);
            this.overlayManager.add(screenMarkerOverlay);
            this.pluginPanel.rebuild();
            this.updateConfig();
        }
        this.creatingScreenMarker = false;
        this.drawingScreenMarker = false;
        this.selectedWidgetBounds = null;
        this.startLocation = null;
        this.currentMarker = null;
        this.setMouseListenerEnabled(false);
        this.pluginPanel.setCreation(false);
    }

    public void completeSelection() {
        this.pluginPanel.getCreationPanel().unlockConfirm();
    }

    public void deleteMarker(ScreenMarkerOverlay marker) {
        this.screenMarkers.remove(marker);
        this.overlayManager.remove(marker);
        this.overlayManager.resetOverlay(marker);
        this.pluginPanel.rebuild();
        this.updateConfig();
    }

    void resizeMarker(Point point) {
        this.drawingScreenMarker = true;
        Rectangle bounds = new Rectangle(this.startLocation);
        bounds.add(point);
        this.overlay.setPreferredLocation(bounds.getLocation());
        this.overlay.setPreferredSize(bounds.getSize());
    }

    public void updateConfig() {
        if (this.screenMarkers.isEmpty()) {
            this.configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
            return;
        }
        String json = this.gson.toJson(this.screenMarkers.stream().map(ScreenMarkerOverlay::getMarker).collect(Collectors.toList()));
        this.configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
    }

    private Stream<ScreenMarkerOverlay> loadConfig(String json) {
        if (Strings.isNullOrEmpty((String)json)) {
            return Stream.empty();
        }
        List screenMarkerData = (List)this.gson.fromJson(json, new TypeToken<ArrayList<ScreenMarker>>(){}.getType());
        return screenMarkerData.stream().filter(Objects::nonNull).map(ScreenMarkerOverlay::new);
    }

    public List<ScreenMarkerOverlay> getScreenMarkers() {
        return this.screenMarkers;
    }

    public ColorPickerManager getColorPickerManager() {
        return this.colorPickerManager;
    }

    ScreenMarker getCurrentMarker() {
        return this.currentMarker;
    }

    public boolean isCreatingScreenMarker() {
        return this.creatingScreenMarker;
    }

    public void setCreatingScreenMarker(boolean creatingScreenMarker) {
        this.creatingScreenMarker = creatingScreenMarker;
    }

    public boolean isDrawingScreenMarker() {
        return this.drawingScreenMarker;
    }

    public Rectangle getSelectedWidgetBounds() {
        return this.selectedWidgetBounds;
    }

    public void setSelectedWidgetBounds(Rectangle selectedWidgetBounds) {
        this.selectedWidgetBounds = selectedWidgetBounds;
    }
}

