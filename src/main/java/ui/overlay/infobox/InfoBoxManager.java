/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ComparisonChain
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  net.runelite.api.Client
 *  net.runelite.api.MenuAction
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.ui.overlay.infobox;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.InfoBoxMenuClicked;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxOverlay;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.AsyncBufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InfoBoxManager {
    private static final Logger log = LoggerFactory.getLogger(InfoBoxManager.class);
    private static final String INFOBOXLAYER_KEY = "infoboxlayer";
    private static final String INFOBOXOVERLAY_KEY = "infoboxoverlay";
    private static final String INFOBOXOVERLAY_ORIENTATION_PREFIX = "orient_";
    private static final String DEFAULT_LAYER = "InfoBoxOverlay";
    private static final String DETACH = "Detach";
    private static final String FLIP = "Flip";
    private static final String DELETE = "Delete";
    private static final OverlayMenuEntry DETACH_ME = new OverlayMenuEntry(MenuAction.RUNELITE_INFOBOX, "Detach", "InfoBox");
    private static final OverlayMenuEntry FLIP_ME = new OverlayMenuEntry(MenuAction.RUNELITE_INFOBOX, "Flip", "InfoBox Group");
    private static final OverlayMenuEntry DELETE_ME = new OverlayMenuEntry(MenuAction.RUNELITE_INFOBOX, "Delete", "InfoBox Group");
    private final Map<String, InfoBoxOverlay> layers = new ConcurrentHashMap<String, InfoBoxOverlay>();
    private final RuneLiteConfig runeLiteConfig;
    private final TooltipManager tooltipManager;
    private final Client client;
    private final EventBus eventBus;
    private final OverlayManager overlayManager;
    private final ConfigManager configManager;

    @Inject
    private InfoBoxManager(RuneLiteConfig runeLiteConfig, TooltipManager tooltipManager, Client client2, EventBus eventBus, OverlayManager overlayManager, ConfigManager configManager) {
        this.runeLiteConfig = runeLiteConfig;
        this.tooltipManager = tooltipManager;
        this.client = client2;
        this.eventBus = eventBus;
        this.overlayManager = overlayManager;
        this.configManager = configManager;
        eventBus.register(this);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("runelite") && event.getKey().equals("infoBoxSize")) {
            this.layers.values().forEach(l -> l.getInfoBoxes().forEach(this::updateInfoBoxImage));
        }
    }

    @Subscribe
    public synchronized void onProfileChanged(ProfileChanged profileChanged) {
        List<InfoBox> infoBoxes = this.getInfoBoxes();
        infoBoxes.forEach(this::removeInfoBox);
        new ArrayList<InfoBoxOverlay>(this.layers.values()).forEach(this::removeOverlay);
        infoBoxes.forEach(this::addInfoBox);
    }

    @Subscribe
    public void onInfoBoxMenuClicked(InfoBoxMenuClicked event) {
        InfoBoxOverlay dest;
        InfoBoxOverlay source;
        if (DETACH.equals(event.getEntry().getOption())) {
            this.splitInfobox(event.getInfoBox().getName() + "_" + System.currentTimeMillis(), event.getInfoBox());
        } else if (FLIP.equals(event.getEntry().getOption())) {
            InfoBoxOverlay infoBoxOverlay = this.layers.get(event.getInfoBox().layer);
            ComponentOrientation newOrientation = infoBoxOverlay.flip();
            this.setOrientation(infoBoxOverlay.getName(), newOrientation);
        } else if (DELETE.equals(event.getEntry().getOption()) && (source = this.layers.get(event.getInfoBox().layer)) != (dest = this.layers.computeIfAbsent(DEFAULT_LAYER, this::makeOverlay))) {
            this.mergeInfoBoxes(source, dest);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addInfoBox(InfoBox infoBox) {
        String layerName;
        Preconditions.checkNotNull((Object)infoBox);
        log.debug("Adding InfoBox {}", (Object)infoBox);
        this.updateInfoBoxImage(infoBox);
        infoBox.layer = layerName = this.getLayer(infoBox);
        InfoBoxOverlay overlay = this.layers.computeIfAbsent(layerName, this::makeOverlay);
        List<OverlayMenuEntry> menuEntries = infoBox.getMenuEntries();
        menuEntries.add(DETACH_ME);
        menuEntries.add(FLIP_ME);
        if (!layerName.equals(DEFAULT_LAYER)) {
            menuEntries.add(DELETE_ME);
        }
        InfoBoxManager infoBoxManager = this;
        synchronized (infoBoxManager) {
            int idx = InfoBoxManager.findInsertionIndex(overlay.getInfoBoxes(), infoBox, (b1, b2) -> ComparisonChain.start().compare((Comparable)((Object)b1.getPriority()), (Comparable)((Object)b2.getPriority())).compare((Comparable)((Object)b1.getPlugin().getName()), (Comparable)((Object)b2.getPlugin().getName())).result());
            overlay.getInfoBoxes().add(idx, infoBox);
        }
        BufferedImage image = infoBox.getImage();
        if (image instanceof AsyncBufferedImage) {
            AsyncBufferedImage abi = (AsyncBufferedImage)image;
            abi.onLoaded(() -> this.updateInfoBoxImage(infoBox));
        }
    }

    public synchronized void removeInfoBox(InfoBox infoBox) {
        if (infoBox == null || infoBox.layer == null) {
            return;
        }
        if (this.layers.get(infoBox.layer).getInfoBoxes().remove(infoBox)) {
            log.debug("Removed InfoBox {}", (Object)infoBox);
        }
        infoBox.layer = null;
        infoBox.getMenuEntries().remove(DETACH_ME);
        infoBox.getMenuEntries().remove(FLIP_ME);
        infoBox.getMenuEntries().remove(DELETE_ME);
    }

    public synchronized void removeIf(Predicate<InfoBox> filter) {
        for (InfoBoxOverlay overlay : this.layers.values()) {
            if (!overlay.getInfoBoxes().removeIf(filter)) continue;
            log.debug("Removed InfoBoxes for filter {} from {}", filter, (Object)overlay);
        }
    }

    public List<InfoBox> getInfoBoxes() {
        return this.layers.values().stream().map(InfoBoxOverlay::getInfoBoxes).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public synchronized void cull() {
        this.layers.values().forEach(l -> l.getInfoBoxes().removeIf(InfoBox::cull));
    }

    public void updateInfoBoxImage(InfoBox infoBox) {
        BufferedImage image;
        if (infoBox.getImage() == null) {
            return;
        }
        BufferedImage resultImage = image = infoBox.getImage();
        double width = image.getWidth(null);
        double height = image.getHeight(null);
        double size = Math.max(2, this.runeLiteConfig.infoBoxSize());
        if (size < width || size < height) {
            double scalex = size / width;
            double scaley = size / height;
            if (scalex == 1.0 && scaley == 1.0) {
                return;
            }
            double scale = Math.min(scalex, scaley);
            int newWidth = (int)(width * scale);
            int newHeight = (int)(height * scale);
            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, 2);
            Graphics2D g = scaledImage.createGraphics();
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();
            resultImage = scaledImage;
        }
        infoBox.setScaledImage(resultImage);
    }

    private InfoBoxOverlay makeOverlay(String name) {
        ComponentOrientation orientation = this.getOrientation(name);
        if (orientation == null) {
            if (name.equals(DEFAULT_LAYER)) {
                orientation = this.runeLiteConfig.infoBoxVertical() ? ComponentOrientation.VERTICAL : ComponentOrientation.HORIZONTAL;
                this.setOrientation(name, orientation);
            } else {
                orientation = ComponentOrientation.HORIZONTAL;
            }
        }
        InfoBoxOverlay infoBoxOverlay = new InfoBoxOverlay(this, this.tooltipManager, this.client, this.runeLiteConfig, this.eventBus, name, orientation);
        this.overlayManager.add(infoBoxOverlay);
        this.eventBus.register(infoBoxOverlay);
        return infoBoxOverlay;
    }

    private void removeOverlay(InfoBoxOverlay overlay) {
        this.eventBus.unregister(overlay);
        this.overlayManager.remove(overlay);
        this.layers.remove(overlay.getName());
    }

    private synchronized void splitInfobox(String newLayer, InfoBox infoBox) {
        String oldLayer = infoBox.layer;
        InfoBoxOverlay oldOverlay = this.layers.get(infoBox.layer);
        Collection filtered = oldOverlay.getInfoBoxes().stream().filter(i -> i.getName().equals(infoBox.getName())).collect(Collectors.toList());
        oldOverlay.getInfoBoxes().removeAll(filtered);
        if (oldOverlay.getInfoBoxes().isEmpty()) {
            log.debug("Deleted layer: {}", (Object)oldOverlay.getName());
            this.unsetOrientation(oldOverlay.getName());
            this.removeOverlay(oldOverlay);
        }
        InfoBoxOverlay newOverlay = this.layers.computeIfAbsent(newLayer, this::makeOverlay);
        newOverlay.getInfoBoxes().addAll(filtered);
        for (InfoBox i2 : filtered) {
            this.setLayer(i2, newLayer);
            i2.layer = newLayer;
            if (i2.getMenuEntries().contains(DELETE_ME)) continue;
            i2.getMenuEntries().add(DELETE_ME);
        }
        log.debug("Moving infobox named {} (layer {}) to layer {}: {} boxes", new Object[]{infoBox.getName(), oldLayer, newLayer, filtered.size()});
    }

    public synchronized void mergeInfoBoxes(InfoBoxOverlay source, InfoBoxOverlay dest) {
        List<InfoBox> infoBoxesToMove = source.getInfoBoxes();
        boolean isDefault = dest.getName().equals(DEFAULT_LAYER);
        log.debug("Merging InfoBoxes from {} into {} ({} boxes)", new Object[]{source.getName(), dest.getName(), infoBoxesToMove.size()});
        for (InfoBox infoBox : infoBoxesToMove) {
            this.setLayer(infoBox, dest.getName());
            infoBox.layer = dest.getName();
            if (!isDefault) continue;
            infoBox.getMenuEntries().remove(DELETE_ME);
        }
        dest.getInfoBoxes().addAll(infoBoxesToMove);
        source.getInfoBoxes().clear();
        this.unsetOrientation(source.getName());
        this.removeOverlay(source);
        log.debug("Deleted layer: {}", (Object)source.getName());
    }

    private String getLayer(InfoBox infoBox) {
        String name = this.configManager.getConfiguration(INFOBOXLAYER_KEY, infoBox.getName());
        if (Strings.isNullOrEmpty((String)name)) {
            return DEFAULT_LAYER;
        }
        return name;
    }

    private void setLayer(InfoBox infoBox, String layer) {
        if (layer.equals(DEFAULT_LAYER)) {
            this.configManager.unsetConfiguration(INFOBOXLAYER_KEY, infoBox.getName());
        } else {
            this.configManager.setConfiguration(INFOBOXLAYER_KEY, infoBox.getName(), layer);
        }
    }

    ComponentOrientation getOrientation(String name) {
        return (ComponentOrientation)((Object)this.configManager.getConfiguration(INFOBOXOVERLAY_KEY, INFOBOXOVERLAY_ORIENTATION_PREFIX + name, (Type)((Object)ComponentOrientation.class)));
    }

    void setOrientation(String name, ComponentOrientation orientation) {
        this.configManager.setConfiguration(INFOBOXOVERLAY_KEY, INFOBOXOVERLAY_ORIENTATION_PREFIX + name, orientation);
    }

    void unsetOrientation(String name) {
        this.configManager.unsetConfiguration(INFOBOXOVERLAY_KEY, INFOBOXOVERLAY_ORIENTATION_PREFIX + name);
    }

    private static <T> int findInsertionIndex(List<? extends T> list, T key, Comparator<? super T> c) {
        int idx = Collections.binarySearch(list, key, c);
        if (idx < 0) {
            return -idx - 1;
        }
        for (int i = idx + 1; i < list.size(); ++i) {
            T cur = list.get(i);
            int cmp = c.compare(cur, key);
            if (cmp <= 0) continue;
            return i;
        }
        return list.size();
    }
}

