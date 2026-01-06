/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ArrayListMultimap
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.widgets.WidgetItem
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.ui.overlay;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ArrayListMultimap;
import java.awt.Dimension;
import java.awt.Point;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.WidgetOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OverlayManager {
    private static final Logger log = LoggerFactory.getLogger(OverlayManager.class);
    public static final String OPTION_CONFIGURE = "Configure";
    private static final String OVERLAY_CONFIG_PREFERRED_LOCATION = "_preferredLocation";
    private static final String OVERLAY_CONFIG_PREFERRED_POSITION = "_preferredPosition";
    private static final String OVERLAY_CONFIG_PREFERRED_SIZE = "_preferredSize";
    private static final String RUNELITE_CONFIG_GROUP_NAME = RuneLiteConfig.class.getAnnotation(ConfigGroup.class).value();
    static final Comparator<Overlay> OVERLAY_COMPARATOR = (a, b) -> {
        OverlayPosition bPos;
        OverlayPosition aPos = (OverlayPosition)((Object)((Object)MoreObjects.firstNonNull((Object)((Object)a.getPreferredPosition()), (Object)((Object)a.getPosition()))));
        if (aPos != (bPos = (OverlayPosition)((Object)((Object)MoreObjects.firstNonNull((Object)((Object)b.getPreferredPosition()), (Object)((Object)b.getPosition())))))) {
            return aPos.compareTo(bPos);
        }
        return aPos == OverlayPosition.DYNAMIC || aPos == OverlayPosition.DETACHED ? Float.compare(a.getPriority(), b.getPriority()) : Float.compare(b.getPriority(), a.getPriority());
    };
    private final List<Overlay> overlays = new ArrayList<Overlay>();
    private Collection<WidgetItem> widgetItems = Collections.emptyList();
    private ArrayListMultimap<Object, Overlay> overlayMap = ArrayListMultimap.create();
    private final ConfigManager configManager;
    private final RuneLiteConfig runeLiteConfig;

    @Inject
    private OverlayManager(ConfigManager configManager, RuneLiteConfig runeLiteConfig) {
        this.configManager = configManager;
        this.runeLiteConfig = runeLiteConfig;
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!"runelite".equals(event.getGroup()) || !"overlayBackgroundColor".equals(event.getKey())) {
            return;
        }
        this.overlays.forEach(this::updateOverlayConfig);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Subscribe
    public void onProfileChanged(ProfileChanged event) {
        OverlayManager overlayManager = this;
        synchronized (overlayManager) {
            this.overlays.forEach(o -> {
                this.loadOverlay((Overlay)o);
                o.revalidate();
            });
        }
        this.rebuildOverlayLayers();
    }

    Collection<Overlay> getLayer(OverlayLayer layer) {
        return Collections.unmodifiableCollection(this.overlayMap.get((Object)layer));
    }

    Collection<Overlay> getForInterface(int interfaceId) {
        return Collections.unmodifiableCollection(this.overlayMap.get((Object)(interfaceId << 16 | 0xFFFF)));
    }

    Collection<Overlay> getForLayer(int layerId) {
        return Collections.unmodifiableCollection(this.overlayMap.get((Object)layerId));
    }

    public synchronized boolean add(Overlay overlay) {
        if (this.overlays.contains(overlay)) {
            return false;
        }
        this.overlays.add(overlay);
        this.loadOverlay(overlay);
        this.updateOverlayConfig(overlay);
        if (overlay instanceof WidgetItemOverlay) {
            ((WidgetItemOverlay)overlay).setOverlayManager(this);
        }
        this.rebuildOverlayLayers();
        return true;
    }

    public synchronized boolean remove(Overlay overlay) {
        boolean remove = this.overlays.remove(overlay);
        if (remove) {
            this.rebuildOverlayLayers();
        }
        return remove;
    }

    public synchronized boolean removeIf(Predicate<Overlay> filter) {
        boolean removeIf = this.overlays.removeIf(filter);
        if (removeIf) {
            this.rebuildOverlayLayers();
        }
        return removeIf;
    }

    public synchronized boolean anyMatch(Predicate<Overlay> filter) {
        return this.overlays.stream().anyMatch(filter);
    }

    public synchronized void clear() {
        this.overlays.clear();
        this.rebuildOverlayLayers();
    }

    public synchronized void saveOverlay(Overlay overlay) {
        this.saveOverlayPosition(overlay);
        this.saveOverlaySize(overlay);
        this.saveOverlayLocation(overlay);
        this.rebuildOverlayLayers();
    }

    public synchronized void resetOverlay(Overlay overlay) {
        overlay.setPreferredPosition(null);
        overlay.setPreferredSize(null);
        overlay.setPreferredLocation(null);
        this.saveOverlay(overlay);
        overlay.revalidate();
    }

    synchronized void rebuildOverlayLayers() {
        ArrayListMultimap overlayMap = ArrayListMultimap.create();
        for (Overlay overlay : this.overlays) {
            OverlayLayer layer = overlay.getLayer();
            if (overlay.getPreferredLocation() != null && overlay.getPreferredPosition() == null && layer == OverlayLayer.UNDER_WIDGETS && !(overlay instanceof WidgetOverlay)) {
                layer = OverlayLayer.ABOVE_WIDGETS;
            }
            switch (layer) {
                case ABOVE_SCENE: 
                case UNDER_WIDGETS: 
                case ALWAYS_ON_TOP: {
                    overlayMap.put((Object)layer, (Object)overlay);
                    break;
                }
                case ABOVE_WIDGETS: {
                    overlayMap.put((Object)0x224FFFF, (Object)overlay);
                    overlayMap.put((Object)0xA1FFFF, (Object)overlay);
                    overlayMap.put((Object)0xA4FFFF, (Object)overlay);
                }
            }
            for (int drawHook : overlay.getDrawHooks()) {
                overlayMap.put((Object)drawHook, (Object)overlay);
            }
        }
        for (Object key : overlayMap.keys()) {
            overlayMap.get(key).sort(OVERLAY_COMPARATOR);
        }
        this.overlayMap = overlayMap;
    }

    private void loadOverlay(Overlay overlay) {
        Point location = this.loadOverlayLocation(overlay);
        Dimension size = this.loadOverlaySize(overlay);
        OverlayPosition position = this.loadOverlayPosition(overlay);
        if (overlay.isMovable()) {
            overlay.setPreferredLocation(location);
        } else if (location != null) {
            log.info("Resetting preferred location of non-movable overlay {} (class {})", (Object)overlay.getName(), (Object)overlay.getClass().getName());
            overlay.setPreferredLocation(null);
            this.saveOverlayLocation(overlay);
        }
        overlay.setPreferredSize(size);
        if (overlay.isSnappable()) {
            overlay.setPreferredPosition(position);
        } else if (position != null) {
            log.info("Resetting preferred position of non-snappable overlay {} (class {})", (Object)overlay.getName(), (Object)overlay.getClass().getName());
            overlay.setPreferredPosition(null);
            this.saveOverlayPosition(overlay);
        }
    }

    private void updateOverlayConfig(Overlay overlay) {
        if (overlay instanceof OverlayPanel) {
            ((OverlayPanel)overlay).setPreferredColor(this.runeLiteConfig.overlayBackgroundColor());
        }
    }

    private void saveOverlayLocation(Overlay overlay) {
        String key = overlay.getName() + OVERLAY_CONFIG_PREFERRED_LOCATION;
        if (overlay.getPreferredLocation() != null) {
            this.configManager.setConfiguration(RUNELITE_CONFIG_GROUP_NAME, key, overlay.getPreferredLocation());
        } else {
            this.configManager.unsetConfiguration(RUNELITE_CONFIG_GROUP_NAME, key);
        }
    }

    private void saveOverlaySize(Overlay overlay) {
        String key = overlay.getName() + OVERLAY_CONFIG_PREFERRED_SIZE;
        if (overlay.getPreferredSize() != null) {
            this.configManager.setConfiguration(RUNELITE_CONFIG_GROUP_NAME, key, overlay.getPreferredSize());
        } else {
            this.configManager.unsetConfiguration(RUNELITE_CONFIG_GROUP_NAME, key);
        }
    }

    private void saveOverlayPosition(Overlay overlay) {
        String key = overlay.getName() + OVERLAY_CONFIG_PREFERRED_POSITION;
        if (overlay.getPreferredPosition() != null) {
            this.configManager.setConfiguration(RUNELITE_CONFIG_GROUP_NAME, key, overlay.getPreferredPosition());
        } else {
            this.configManager.unsetConfiguration(RUNELITE_CONFIG_GROUP_NAME, key);
        }
    }

    private Point loadOverlayLocation(Overlay overlay) {
        String key = overlay.getName() + OVERLAY_CONFIG_PREFERRED_LOCATION;
        return (Point)this.configManager.getConfiguration(RUNELITE_CONFIG_GROUP_NAME, key, (Type)((Object)Point.class));
    }

    private Dimension loadOverlaySize(Overlay overlay) {
        String key = overlay.getName() + OVERLAY_CONFIG_PREFERRED_SIZE;
        return (Dimension)this.configManager.getConfiguration(RUNELITE_CONFIG_GROUP_NAME, key, (Type)((Object)Dimension.class));
    }

    private OverlayPosition loadOverlayPosition(Overlay overlay) {
        String locationKey = overlay.getName() + OVERLAY_CONFIG_PREFERRED_POSITION;
        return (OverlayPosition)((Object)this.configManager.getConfiguration(RUNELITE_CONFIG_GROUP_NAME, locationKey, (Type)((Object)OverlayPosition.class)));
    }

    List<Overlay> getOverlays() {
        return this.overlays;
    }

    public Collection<WidgetItem> getWidgetItems() {
        return this.widgetItems;
    }

    public void setWidgetItems(Collection<WidgetItem> widgetItems) {
        this.widgetItems = widgetItems;
    }
}

